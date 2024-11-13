import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class TCPClient {

    private String serverAddress;
    private int port;

    public TCPClient(String serverAddress, int port) {
        this.serverAddress = serverAddress;
        this.port = port;
    }

    public void start() {
        try (Socket socket = new Socket(serverAddress, port)) {
            System.out.println("Connected to the server: " + serverAddress + " on port " + port);

            // Set up output stream to send messages to the server
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            Scanner scanner = new Scanner(System.in);
            String userInput;

            System.out.println("Enter your message (type 'exit' to quit):");
            while (scanner.hasNextLine()) {
                userInput = scanner.nextLine();
                if (userInput.equalsIgnoreCase("exit")) {
                    break;
                }

                // Send the user input to the server
                out.println(userInput);  // `println` sends the data with a newline and flushes it
                System.out.println("Message sent: " + userInput);
            }

            System.out.println("Closing connection...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String serverAddress = "localhost"; // Replace with the server's IP if needed
        int port = 8080; // Replace with the port used by the server

        TCPClient client = new TCPClient(serverAddress, port);
        client.start();
    }
}

