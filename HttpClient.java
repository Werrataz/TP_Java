import java.io.IOException;
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
        try {
            Socket socket = new Socket(client.host, client.port);
            System.out.println("Connected to " + client.host + " on port " + client.port);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    static String getURL(String url) {
        // return the content of the URL
        return null;
    }
}
