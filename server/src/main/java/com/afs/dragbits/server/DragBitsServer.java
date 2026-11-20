package com.afs.dragbits.server;

/**
 * Punto de entrada del servidor DragBits.
 * Inicia BroadcastListener (UDP 54321) y TCPServer (TCP 54322).
 */
public class DragBitsServer {

    private static BroadcastListener broadcastListener;
    private static TCPServer tcpServer;
    private static GameRoom gameRoom;
    private static Thread broadcastThread;
    private static Thread tcpThread;
    private static Thread gameLoopThread;
    private static volatile boolean running = true;

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  DragBits Server - Iniciando...");
        System.out.println("========================================");

        try {
            // Crear sala de juego
            gameRoom = new GameRoom();

            // Iniciar TCPServer primero
            tcpServer = new TCPServer(gameRoom);
            tcpThread = new Thread(tcpServer, "TCPServer");
            tcpThread.start();

            // Iniciar BroadcastListener (UDP) - necesita tcpServer
            broadcastListener = new BroadcastListener(tcpServer);
            broadcastThread = new Thread(broadcastListener, "BroadcastListener");
            broadcastThread.start();

            // Esperar un poco para que los servidores arranquen
            Thread.sleep(500);

            // Obtener IP local para mostrar
            String localIp = getLocalIpAddress();
            System.out.println("");
            System.out.println("========================================");
            System.out.println("  Servidor iniciado correctamente");
            System.out.println("  IP: " + localIp);
            System.out.println("  UDP Broadcast: 54321");
            System.out.println("  TCP Juego:     54322");
            System.out.println("========================================");
            System.out.println("Esperando jugadores...");
            System.out.println("");

            // Game loop principal (60Hz)
            gameLoopThread = new Thread(() -> {
                long lastTime = System.nanoTime();
                final double nsPerFrame = 1_000_000_000.0 / 60.0;

                while (running) {
                    long now = System.nanoTime();
                    float delta = (now - lastTime) / 1_000_000_000.0f;
                    lastTime = now;

                    // Limitar delta para evitar saltos grandes
                    if (delta > 0.1f) delta = 0.1f;

                    gameRoom.update(delta);

                    // Dormir para mantener 60Hz
                    long elapsed = System.nanoTime() - now;
                    long sleepTime = (long) (nsPerFrame - elapsed);
                    if (sleepTime > 0) {
                        try {
                            Thread.sleep(sleepTime / 1_000_000, (int) (sleepTime % 1_000_000));
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                }
            }, "GameLoop");
            gameLoopThread.start();

            // Shutdown hook para cierre limpio
            Runtime.getRuntime().addShutdownHook(new Thread(DragBitsServer::shutdown));

            // Mantener main vivo
            while (running) {
                Thread.sleep(1000);
            }

        } catch (Exception e) {
            System.err.println("Error fatal iniciando servidor: " + e.getMessage());
            e.printStackTrace();
            shutdown();
            System.exit(1);
        }
    }

    private static String getLocalIpAddress() {
        try {
            java.util.Enumeration<java.net.NetworkInterface> interfaces =
                    java.net.NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                java.net.NetworkInterface ni = interfaces.nextElement();
                if (ni.isLoopback() || !ni.isUp()) continue;

                java.util.Enumeration<java.net.InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    java.net.InetAddress addr = addresses.nextElement();
                    if (addr instanceof java.net.Inet4Address && !addr.isLoopbackAddress()) {
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error obteniendo IP: " + e.getMessage());
        }
        return "127.0.0.1";
    }

    public static void shutdown() {
        if (!running) return;
        running = false;
        System.out.println("\n[DragBitsServer] Apagando servidor...");

        if (broadcastListener != null) broadcastListener.stop();
        if (tcpServer != null) tcpServer.stop();

        try {
            if (broadcastThread != null) broadcastThread.join(1000);
            if (tcpThread != null) tcpThread.join(1000);
            if (gameLoopThread != null) gameLoopThread.join(1000);
        } catch (InterruptedException ignored) {}

        System.out.println("[DragBitsServer] Servidor detenido");
    }
}