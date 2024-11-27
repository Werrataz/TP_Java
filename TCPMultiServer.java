import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class TCPMultiServer {

    private int port;
    private Map<String, ClientHandler> clients;

    public TCPMultiServer(int port) {
        if (port < 0 || port > 49151) {
            System.err.println("Port number must be between 0 and 49151");
            this.port = 8080;
        } else {
            this.port = port;
        }
        this.clients = new HashMap<>();
    }

    public TCPMultiServer() {
        this.port = 8080;
        this.clients = new HashMap<>();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(this.port)) {
            System.out.println("Server is listening on port " + this.port);

            while (true) {
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

    private class ClientHandler extends Thread {
        private Socket socket;
        private String nickname;
        private String user;
        private String id;
        private PrintWriter printOut;
        private BufferedReader readIn;
        private int numberOfMessages;
        private int numberOfBlockedMessages;
        private Boolean banned;
        protected static Map<String, ClientHandler> clients = new HashMap<>();
        

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                this.printOut = out;
                this.readIn = in;
                ChatProtocolParser parser = new ChatProtocolParser();
                String clientMessage;
                
                this.nickname = socket.getInetAddress().getHostAddress();
                this.banned = false;
                ClientHandler.clients.put(this.nickname, this);
                
                while ((clientMessage = in.readLine()) != null) {
                    ChatProtocolParser.ParsedCommand parsedMessage = parser.parseLine(clientMessage);
                    if (parsedMessage.type == ChatProtocolParser.CommandType.MESSAGE) {
                        this.numberOfMessages++;
                        if ("exit".equalsIgnoreCase(clientMessage)) {
                            System.out.println("Client disconnected");
                            break;
                        }
                        if (this.banned) {
                            out.println("Message from " + this.nickname + " was blocked");
                        } else {
                            System.out.println("Received from client " + this.nickname + ", Message: " + clientMessage);
                            out.println("message received");
                        }
                    } else if (parsedMessage.type == ChatProtocolParser.CommandType.NICKNAME) {
                        System.out.println("User: " + this.nickname + " will be renamed to " + parsedMessage.value);
                        ClientHandler.clients.remove(this.nickname);
                        this.nickname = parsedMessage.value;
                        ClientHandler.clients.put(this.nickname, this);
                        out.println("Successfully changed nickname to " + this.nickname);
                        
                    } else if (parsedMessage.type == ChatProtocolParser.CommandType.BAN) {
                        this.banned = true;
                        System.out.println("User: " + this.nickname + " is now banned");
                        out.println("User: " + this.nickname + " is now banned");

                    } else if (parsedMessage.type == ChatProtocolParser.CommandType.STAT) {
                        out.println("User: " + this.nickname + ", Number of messages: " + this.numberOfMessages + ", Number of blocked messages: " + this.numberOfBlockedMessages + ", pourcentage of blocked messages: " + (this.numberOfBlockedMessages / this.numberOfMessages) * 100 + "%");
                    } else if (parsedMessage.type == ChatProtocolParser.CommandType.MENTION) {
                        String mentionedUser = parsedMessage.value;
                        ClientHandler mentionedClient = ClientHandler.clients.get(mentionedUser);
                        System.out.println(mentionedClient);
                        if (mentionedClient != null) {
                            if(mentionedClient.sendMessage("Message from " + this.nickname + " : " + clientMessage)) {
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

        private Boolean sendMessage(String message) {
            try {
                this.printOut.println(message);
            } catch (Exception e) {
                return false;
            }
            return true;
        }
    }

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