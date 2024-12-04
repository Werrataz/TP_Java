import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.security.KeyStore;
import java.util.Scanner;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

/**
 * TCPClient is a client designed to work with the TCPMultiServer.
 */
public class TCPClient {

    private String serverAddress;
    private int port;
    private SSLSocket socket;
    private BufferedReader in;
    private PrintWriter out;

    /**
     * Constructs a TCPClient with the specified server address and port.
     *
     * @param serverAddress the address of the server to connect to
     * @param port the port number of the server
     */
    public TCPClient(String serverAddress, int port) {
        this.serverAddress = serverAddress;
        this.port = port;
    }

    /**
     * Starts the TCPClient, connects to the server, and handles user input and server responses.
     */
    public void start() {
        try {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(getClass().getResourceAsStream("/client.keystore"), "password".toCharArray());

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, "password".toCharArray());

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            socket = (SSLSocket) sslSocketFactory.createSocket(InetAddress.getByName(this.serverAddress), this.port);
            socket.startHandshake();

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

    /**
     * A Runnable class that handles responses from the server.
     */
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

    /**
     * The main method to run the TCPClient.
     *
     * @param args command line arguments, where the first argument is the server address and the second argument is the port number (the second is optional)
     */
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