import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TCPMultiServer is a multi-client TCP server that handles multiple clients concurrently.
 */
public class TCPMultiServer {

    private int port;
    private Map<String, ClientHandler> clients;

    /**
     * Constructs a TCPMultiServer with the specified port.
     * 
     * @param port The port number on which the server will listen.
     */
    public TCPMultiServer(int port) {
        if (port < 0 || port > 49151) {
            System.err.println("Port number must be between 0 and 49151");
            this.port = 8080;
        } else {
            this.port = port;
        }
        this.clients = new HashMap<>();
    }

    /**
     * Constructs a TCPMultiServer with the default port 8080.
     */
    public TCPMultiServer() {
        this.port = 8080;
        this.clients = new HashMap<>();
    }

    /**
     * Starts the TCP server and listens for client connections.
     */
    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(this.port)) {
            System.out.println("Server is listening on port " + this.port);

            while (true) {
                // Accept a new client connection
                Socket socket = serverSocket.accept();
                System.out.println("New client connected");
                ClientHandler clientHandler = new ClientHandler(socket);
                clients.put(socket.getInetAddress().getHostAddress(), clientHandler);
                clientHandler.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ClientHandler is a thread that handles communication with a single client.
     */
    private class ClientHandler extends Thread {
        private Socket socket;
        private String nickname;
        private PrintWriter printOut;
        private BufferedReader readIn;
        private int numberOfMessages;
        private final List<String> banList = new ArrayList<>();
        private int numberOfBlockedMessages = 0;
        protected static Map<String, ClientHandler> clients = new HashMap<>();

        /**
         * Constructs a ClientHandler for the specified socket.
         * 
         * @param socket The socket connected to the client.
         */
        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        /**
         * Runs the client handler thread, handling communication with the client.
         */
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                this.printOut = out;
                this.readIn = in;
                ChatProtocolParser parser = new ChatProtocolParser();
                String clientMessage;
                
                String nickname = socket.getInetAddress().getHostAddress();
                int i = 1;
                for (ClientHandler client : ClientHandler.clients.values()) {
                    if (client.nickname.equals(nickname)) {
                        nickname = String.format("%s-%d", nickname, i);
                        i++;
                    }
                }
                this.nickname = nickname;
                ClientHandler.clients.put(this.nickname, this);
                
                while ((clientMessage = in.readLine()) != null) {
                    ChatProtocolParser.ParsedCommand parsedMessage = parser.parseLine(clientMessage);
                    if (parsedMessage.type == ChatProtocolParser.CommandType.MESSAGE) {
                        this.numberOfMessages++;
                        if ("exit".equalsIgnoreCase(clientMessage)) {
                            System.out.println("Client disconnected");
                            break;
                        }
                        System.out.println("Received from client " + this.nickname + ", Message: " + clientMessage);
                        for (ClientHandler client : ClientHandler.clients.values()) {
                            if (!client.nickname.equals(this.nickname)) {
                                if (!client.banList.contains(this.nickname)) {
                                    client.sendMessage("Message from " + this.nickname + ": " + clientMessage);
                                }
                            } else {
                                out.println("Message sent to all");
                            }
                        }
                    } else if (parsedMessage.type == ChatProtocolParser.CommandType.NICKNAME) {
                        String newNickname = parsedMessage.value;
                        if (this.nickname.equals(newNickname)) {
                            out.println("You are already known as " + this.nickname);
                            continue;
                        }
                        int j = 1;
                        Boolean exists = false;
                        int secure = 0;
                        while(ClientHandler.clients.containsKey(newNickname)) {
                            newNickname = String.format("%s%d", newNickname, j);
                            j++;
                            exists = true;
                            if (secure > 100) {
                                newNickname = newNickname + java.util.UUID.randomUUID().toString();
                                break;
                            }
                        }
                        ClientHandler.clients.remove(this.nickname);
                        this.nickname = newNickname;
                        ClientHandler.clients.put(this.nickname, this);
                        if (exists) {
                            out.println("User '" + parsedMessage.value + "' already exists, renamed to " + this.nickname);
                        } else {
                            out.println("Successfully changed nickname to " + this.nickname);
                        }
                    } else if (parsedMessage.type == ChatProtocolParser.CommandType.BAN) {
                        this.banList.add(parsedMessage.value);
                        out.println("You will now not see the future messages sent by " + parsedMessage.value);
                    } else if (parsedMessage.type == ChatProtocolParser.CommandType.STAT) {
                        out.println("User: " + this.nickname + ", number of messages: " + this.numberOfMessages + ", number of blocked messages: " + this.numberOfBlockedMessages + ", pourcentage of blocked messages: " + (this.numberOfMessages == 0 ? (this.numberOfBlockedMessages / this.numberOfMessages * 100) : 0) + "%");
                    } else if (parsedMessage.type == ChatProtocolParser.CommandType.MENTION) {
                        String mentionedUser = parsedMessage.value;
                        ClientHandler mentionedClient = ClientHandler.clients.get(mentionedUser);
                        System.out.println(mentionedClient);
                        if (mentionedClient != null && mentionedClient.banList.contains(parsedMessage.value)) {
                            if(mentionedClient.sendMessage("Message from " + this.nickname + ": " + clientMessage)) {
                                out.println("Message sent to " + mentionedUser);
                            } else {
                                out.println("Error, " + mentionedUser + " is not reachable");
                            }
                        } else {
                            out.println("Error, nobody with the name " + mentionedUser + " is connected");
                        }
                    }
                }
                System.out.println("Closing the connection");
                ClientHandler.clients.remove(this.nickname);
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * Sends a message to the client.
         * 
         * @param message The message to send.
         * @return true if the message was sent successfully, false otherwise.
         */
        private Boolean sendMessage(String message) {
            try {
                this.printOut.println(message);
            } catch (Exception e) {
                return false;
            }
            return true;
        }
    }

    /**
     * The main method to start the TCPMultiServer.
     * 
     * @param args Command line arguments, where the first argument can be the server's port number (and others are ignored).
     */
    public static void main(String[] args) {
        TCPMultiServer server;
        if (args.length < 1) {
            server = new TCPMultiServer();
        } else {
            int port = Integer.parseInt(args[0]);
            server = new TCPMultiServer(port);
        }
        server.start();
    }
}