package com.afs.dragbits.server;

import com.afs.dragbits.network.Protocol;
import com.google.gson.Gson;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Escucha paquetes UDP broadcast (puerto 54321) para descubrimiento de servidores.
 * Responde a DISCOVER (plano o JSON {"type":"DISCOVER"}) con SERVER_INFO.
 */
public class BroadcastListener implements Runnable {

    private static final int BROADCAST_PORT = 54321;
    private static final int TCP_PORT = 54322;
    private static final String SERVER_NAME = "DragBits Server";

    private final DatagramSocket socket;
    private final TCPServer tcpServer;
    private volatile boolean running = true;
    private final Gson gson = new Gson();

    public BroadcastListener(TCPServer tcpServer) throws SocketException {
        this.tcpServer = tcpServer;
        this.socket = new DatagramSocket(BROADCAST_PORT);
        this.socket.setBroadcast(true);
        this.socket.setSoTimeout(1000);
    }

    @Override
    public void run() {
        System.out.println("[BroadcastListener] Escuchando DISCOVER en puerto " + BROADCAST_PORT);

        byte[] buffer = new byte[1024];

        while (running) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String received = new String(packet.getData(), 0, packet.getLength()).trim();
                if (isDiscoverMessage(received)) {
                    handleDiscover(packet.getAddress(), packet.getPort());
                }
            } catch (IOException e) {
                if (running) {
                    // Timeout o error de red, continuar
                }
            }
        }

        socket.close();
        System.out.println("[BroadcastListener] Detenido");
    }

    private boolean isDiscoverMessage(String received) {
        try {
            // Aceptar tanto "DISCOVER" plano como JSON {"type":"DISCOVER"}
            if (received.startsWith(Protocol.TYPE_DISCOVER)) return true;
            if (received.startsWith("{")) {
                com.google.gson.JsonObject obj = JsonParser.parseString(received).getAsJsonObject();
                return Protocol.TYPE_DISCOVER.equals(obj.get("type").getAsString());
            }
        } catch (Exception ignored) {}
        return false;
    }

    private void handleDiscover(InetAddress clientAddress, int clientPort) {
        try {
            String serverIp = getLocalIpAddress();
            Protocol.ServerInfoMessage info = new Protocol.ServerInfoMessage();
            info.ip = serverIp;
            info.tcpPort = TCP_PORT;
            info.serverName = SERVER_NAME;
            info.playersConnected = tcpServer.getConnectedPlayersCount();
            info.maxPlayers = 2;

            String jsonStr = gson.toJson(info);
            byte[] data = jsonStr.getBytes("UTF-8");

            DatagramPacket response = new DatagramPacket(data, data.length, clientAddress, clientPort);
            socket.send(response);

            System.out.println("[BroadcastListener] Respondido DISCOVER a " + clientAddress.getHostAddress() +
                    " -> " + serverIp + ":" + TCP_PORT);
        } catch (Exception e) {
            System.err.println("[BroadcastListener] Error respondiendo DISCOVER: " + e.getMessage());
        }
    }

    private String getLocalIpAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                if (ni.isLoopback() || !ni.isUp()) continue;

                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (addr instanceof java.net.Inet4Address && !addr.isLoopbackAddress()) {
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[BroadcastListener] Error obteniendo IP local: " + e.getMessage());
        }
        return "127.0.0.1";
    }

    public void stop() {
        running = false;
        socket.close();
    }
}