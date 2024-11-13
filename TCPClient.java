import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.management.OperatingSystemMXBean;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class TCPClient {

    private String serverAddress;
    private int port;

    // Constructor to initialize server address and port
    public TCPClient(String serverAddress, int port) {
        this.serverAddress = serverAddress;
        this.port = port;
    }

    // Method to send messages to the server
    public void start() {
        try (Socket socket = new Socket(InetAddress.getByName(this.serverAddress), this.port)) {
            // Use Scanner to read input from the user
            Scanner scanner = new Scanner(System.in);
            String message;

            while (true) {
                // Read input from the user
                System.out.print("Enter message (or 'exit' to quit): ");
                message = scanner.nextLine();

                // Check if the user wants to exit
                if ("exit".equalsIgnoreCase(message)) {
                    System.out.println("Exiting...");
                    break;
                }

                // Send the message to the server
                System.out.println(message);
                System.out.println("Message sent to server.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Main method to start the client
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java TCPClient <server_address> <port>");
            return;
        }

        String serverAddress = args[0];
        int port = Integer.parseInt(args[1]);

        // Create and start the TCP client
        TCPClient client = new TCPClient(serverAddress, port);
        client.start();
    }
}
