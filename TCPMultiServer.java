import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
   
public class TCPMultiServer {

    private int port;

    public TCPMultiServer(int port) {
        if (port < 0 || port > 49151) {
            System.err.println("Port number must be between 0 and 49151");
            this.port = 8080;
        } else {
            this.port = port;
        }
    }

    public TCPMultiServer() {
        this.port = 8080;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(this.port)) {
            System.out.println("Server is listening on port " + this.port);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected");

                // Create a new thread for each client connection
                new ClientHandler(socket).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // class to manage each client connection
    private static class ClientHandler extends Thread {
        private Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                String clientMessage;
                while ((clientMessage = in.readLine()) != null) {
                    System.out.println("Received from client " + socket.getInetAddress().getHostAddress() + ", Message : " + clientMessage);

                    if ("exit".equalsIgnoreCase(clientMessage)) {
                        System.out.println("Client disconnected");
                        break;
                    }

                    out.println("Server received: " + clientMessage);
                }

                System.out.println("Closing the connection");
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java TCPMultiServer <port>");
            return;
        }

        int port = Integer.parseInt(args[0]);

        TCPMultiServer server = new TCPMultiServer(port);
        server.start();
    }
}