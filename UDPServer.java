import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPServer {

    int port;

    public static void main(String[] args) {
        UDPServer server;
        if (args.length > 0) {
            server = new UDPServer(Integer.parseInt(args[0]));
        } else {
            server = new UDPServer();
        }
        server.launch();
    }

    public UDPServer(int port) {
        if (port < 0 || port > 49151) {
            System.err.println("Port number must be between 0 and 49151");
            this.port = 8080;
        } else {
            this.port = port;
        }
    }

    public UDPServer() {
        this.port = 8080;
    }

    void launch() {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(this.port);
            System.out.println("UDP Server is running on port " + this.port);

            byte[] buffer = new byte[1024];
            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet); // Receive packet

                String received = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Received message: " + received);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            System.out.println("UDP Server is closed");
        }
    }
}