import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;

public class HttpClient {

    String protocol, host, filename;
    int port;

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: You must provide a URL as first argument");
            System.exit(1);
        }
        HttpClient client = new HttpClient();
        client.port = 80;
        client.filename = "/rfc.txt";
        client.readURL(args[0]);
    }

    void readURL(String in) {
        try {
            URL url = new URL(in);
            this.host = url.getHost();
            this.port = url.getPort();
            this.filename = url.getFile();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    String getURL() {
        try {
            Socket socket = new Socket(this.host, this.port);
            int compt = 0;
            while (!socket.isConnected()) {
                if (compt > 50) { // Interupt after 5 seconds
                    System.err.println("Connection failed");
                    System.exit(1);
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                compt++;
            }
            System.out.println("Connected to " + this.host + " on port " + this.port);
            InputStream inputStream = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            OutputStream outputStream = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(outputStream, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
