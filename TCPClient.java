import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class TCPClient {

    private String serverAddress;
    private int port;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public TCPClient(String serverAddress, int port) {
        this.serverAddress = serverAddress;
        this.port = port;
    }

    public void start() {
        try {
            socket = new Socket(InetAddress.getByName(this.serverAddress), this.port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            Thread readServerResponse = new Thread(new ServerResponseHandler());
            readServerResponse.start();

            Scanner scanner = new Scanner(System.in);
            String message;

            System.out.println("Enter message (or 'exit' to quit):");
            while (true) {
                message = scanner.nextLine();

                if ("exit".equalsIgnoreCase(message)) {
                    System.out.println("Exiting...");
                    break;
                }

                out.println(message);
                System.out.println("Message sent to server.");
            }

            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class ServerResponseHandler implements Runnable {
        @Override
        public void run() {
            try {
                String serverMessage;
                while ((serverMessage = in.readLine()) != null) {
                    System.out.println("\nServer response: " + serverMessage + "\nEnter message (or 'exit' to quit):");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        TCPClient client;
        if (args.length < 1) {
            System.out.println("Usage: java TCPClient <server_address> <port>");
            return;
        } else if (args.length == 1) {
            String serverAddress = args[0];
            client = new TCPClient(serverAddress, 8080);
        } else {
            String serverAddress = args[0];
            int port = Integer.parseInt(args[1]);
            client = new TCPClient(serverAddress, port);
        }
        
        client.start();
    }
}
