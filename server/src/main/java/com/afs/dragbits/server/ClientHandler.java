package com.afs.dragbits.server;

import com.afs.dragbits.network.Protocol;
import com.badlogic.gdx.utils.Json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Maneja la comunicación con un cliente individual.
 * Un hilo lee mensajes, otro escribe (cola thread-safe).
 */
public class ClientHandler implements Runnable {

    private static final int PING_INTERVAL_MS = 2000;
    private static final int PONG_TIMEOUT_MS = 5000;

    private final Socket socket;
    private final int playerId;
    private final GameRoom gameRoom;
    private final TCPServer tcpServer;
    private final Json json = new Json();

    private volatile boolean running = true;
    private final BlockingQueue<String> sendQueue = new LinkedBlockingQueue<>();
    private long lastPongReceived = System.currentTimeMillis();
    private PrintWriter writer;
    private BufferedReader reader;

    public ClientHandler(Socket socket, int playerId, GameRoom gameRoom, TCPServer tcpServer) {
        this.socket = socket;
        this.playerId = playerId;
        this.gameRoom = gameRoom;
        this.tcpServer = tcpServer;
    }

    @Override
    public void run() {
        try {
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

            // Hilo escritor
            Thread writerThread = new Thread(this::writerLoop, "ClientWriter-" + playerId);
            writerThread.start();

            // Notificar a la sala que el jugador se conectó
            gameRoom.playerConnected(playerId, this);

            // Loop lector principal
            String line;
            while (running && (line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                handleMessage(line.trim());
            }
        } catch (IOException e) {
            if (running) {
                System.err.println("[ClientHandler-" + playerId + "] Error de conexión: " + e.getMessage());
            }
        } finally {
            cleanup();
        }
    }

    private void writerLoop() {
        while (running) {
            try {
                String msg = sendQueue.poll(100, TimeUnit.MILLISECONDS);
                if (msg != null) {
                    writer.println(msg);
                    writer.flush();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                if (running) {
                    System.err.println("[ClientHandler-" + playerId + "] Error escribiendo: " + e.getMessage());
                }
                break;
            }
        }
    }

    private void handleMessage(String jsonStr) {
        try {
            Protocol.Message msg = Protocol.parseMessage(jsonStr);
            if (msg == null) return;

            switch (msg.type) {
                case Protocol.TYPE_JOIN:
                    Protocol.JoinMessage join = (Protocol.JoinMessage) msg;
                    gameRoom.handleJoin(playerId, join.playerName, join.selectedCar, this);
                    break;
                case Protocol.TYPE_INPUT:
                    Protocol.InputMessage input = (Protocol.InputMessage) msg;
                    gameRoom.handleInput(playerId, input);
                    break;
                case Protocol.TYPE_READY:
                    gameRoom.handleReady(playerId);
                    break;
                case Protocol.TYPE_CHALLENGE:
                    Protocol.ChallengeMessage challenge = (Protocol.ChallengeMessage) msg;
                    gameRoom.handleChallenge(playerId, challenge.targetPlayerId);
                    break;
                case Protocol.TYPE_CHALLENGE_RESPONSE:
                    Protocol.ChallengeResponseMessage response = (Protocol.ChallengeResponseMessage) msg;
                    gameRoom.handleChallengeResponse(playerId, response.challengeId, response.accepted);
                    break;
                case Protocol.TYPE_LEAVE:
                    gameRoom.handleLeave(playerId, "LEFT");
                    running = false;
                    break;
                case Protocol.TYPE_PING:
                    Protocol.PingMessage ping = (Protocol.PingMessage) msg;
                    sendPong(ping.clientTime);
                    break;
                case Protocol.TYPE_REJOIN:
                    Protocol.RejoinMessage rejoin = (Protocol.RejoinMessage) msg;
                    gameRoom.handleRejoin(playerId, rejoin.playerName, this);
                    break;
                default:
                    System.out.println("[ClientHandler-" + playerId + "] Tipo desconocido: " + msg.type);
            }
        } catch (Exception e) {
            System.err.println("[ClientHandler-" + playerId + "] Error parseando mensaje: " + e.getMessage());
        }
    }

    private void sendPong(long clientTime) {
        Protocol.PongMessage pong = new Protocol.PongMessage();
        pong.clientTime = clientTime;
        pong.serverTime = System.currentTimeMillis();
        send(pong);
    }

    public void send(Protocol.Message msg) {
        if (running) {
            sendQueue.offer(msg.toJson());
        }
    }

    public void sendError(String code, String message) {
        Protocol.ErrorMessage error = new Protocol.ErrorMessage(code, message);
        send(error);
    }

    public int getPlayerId() {
        return playerId;
    }

    public boolean isConnected() {
        return running && !socket.isClosed() && socket.isConnected();
    }

    private void cleanup() {
        running = false;
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException ignored) {}
        gameRoom.playerDisconnected(playerId);
    }

    public void updateLastPong() {
        lastPongReceived = System.currentTimeMillis();
    }

    public boolean isTimedOut() {
        return System.currentTimeMillis() - lastPongReceived > PONG_TIMEOUT_MS;
    }
}