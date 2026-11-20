package com.afs.dragbits.server;

import com.afs.dragbits.network.Protocol;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Servidor TCP principal (puerto 54322).
 * Acepta conexiones y crea un ClientHandler por cada cliente.
 */
public class TCPServer implements Runnable {

    private static final int TCP_PORT = 54322;
    private static final int MAX_PLAYERS = 2;

    private final ServerSocket serverSocket;
    private final ExecutorService executor;
    private final GameRoom gameRoom;
    private final AtomicInteger playerIdCounter = new AtomicInteger(0);
    private volatile boolean running = true;

    public TCPServer(GameRoom gameRoom) throws IOException {
        this.gameRoom = gameRoom;
        this.serverSocket = new ServerSocket(TCP_PORT);
        this.executor = Executors.newFixedThreadPool(4); // 2 clientes + margen
        System.out.println("[TCPServer] Iniciado en puerto " + TCP_PORT);
    }

    @Override
    public void run() {
        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                int playerId = playerIdCounter.getAndIncrement();

                if (gameRoom.getConnectedPlayersCount() >= MAX_PLAYERS) {
                    // Servidor lleno
                    Protocol.ErrorMessage error = new Protocol.ErrorMessage("SERVER_FULL", "Servidor lleno (máx 2 jugadores)");
                    try {
                        clientSocket.getOutputStream().write((error.toJson() + "\n").getBytes("UTF-8"));
                    } catch (IOException ignored) {}
                    clientSocket.close();
                    System.out.println("[TCPServer] Conexión rechazada: servidor lleno");
                    continue;
                }

                ClientHandler handler = new ClientHandler(clientSocket, playerId, gameRoom, this);
                executor.submit(handler);
                System.out.println("[TCPServer] Nuevo cliente conectado: ID " + playerId);
            } catch (IOException e) {
                if (running) {
                    System.err.println("[TCPServer] Error aceptando conexión: " + e.getMessage());
                }
            }
        }
    }

    public void stop() {
        running = false;
        try {
            serverSocket.close();
        } catch (IOException e) {
            System.err.println("[TCPServer] Error cerrando socket: " + e.getMessage());
        }
        executor.shutdownNow();
        System.out.println("[TCPServer] Detenido");
    }

    public int getConnectedPlayersCount() {
        return gameRoom.getConnectedPlayersCount();
    }
}