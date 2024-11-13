import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {

    private int port;

    // Constructor to initialize the port
    public TCPServer(int port) {
        if (port < 0 || port > 49151) {
            System.err.println("Port number must be between 0 and 49151");
            this.port = 8080;
        } else {
            this.port = port;
        }
    }

    public TCPServer() {
        this.port = 8080;
    }

    // Method to start the server and listen for incoming connections
    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(this.port)) {
            System.out.println("Server is listening on port " + this.port);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println(socket);
                System.out.println("Client connected");

                // Create a BufferedReader to read data from the client
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                System.out.println(in);

                String clientMessage;
                while ((clientMessage = in.readLine()) != null) {
                    System.out.println("Received from client: " + clientMessage);

                    // Process the message (you can add your logic here)
                    if ("exit".equalsIgnoreCase(clientMessage)) {
                        System.out.println("Client disconnected");
                        break;
                    }

                    // Echo the message back to the client
                    out.println("Server received: " + clientMessage);
                }

                // Close the socket
                System.out.println("Closing the connection");
                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Main method to start the server
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java TCPServer <port>");
            return;
        }

        int port = Integer.parseInt(args[0]);

        // Create and start the TCP server
        TCPServer server = new TCPServer(port);
        server.start();
    }
}
