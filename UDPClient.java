import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class UDPClient {

   private String serverAddress;
   private int port;

   // Constructor to initialize server address and port
   public UDPClient(String serverAddress, int port) {
       this.serverAddress = serverAddress;
       this.port = port;
   }

   // Method to send messages to the server
   public void start() {
       try (DatagramSocket socket = new DatagramSocket()) {
           InetAddress serverInetAddress = InetAddress.getByName(this.serverAddress);

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

               // Convert message to bytes
               byte[] buffer = message.getBytes(StandardCharsets.UTF_8);


               // Create and send the UDP datagram to the server
               DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverInetAddress, this.port);
               socket.send(packet);


               System.out.println("Message sent to server.");
           }
       } catch (Exception e) {
           e.printStackTrace();
       }
   }

   // Main method to start the client
   public static void main(String[] args) {
       if (args.length < 2) {
           System.out.println("Usage: java UDPClient <server_address> <port>");
           return;
       }

       String serverAddress = args[0];
       int port = Integer.parseInt(args[1]);

       // Create and start the UDP client
       UDPClient client = new UDPClient(serverAddress, port);
       client.start();
   }
}




