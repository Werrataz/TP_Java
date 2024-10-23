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
        client.getURL();
    }

    void readURL(String in) {
        try {
            URL url = new URL(in);
            this.host = url.getHost();
            if (url.getPort() >= 0 && url.getPort() <= 49151) {
                this.port = url.getPort();
            }
            if(url.getFile().length() > 0) {
                this.filename = url.getFile();
            }
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
                if (compt > 50) { // Interuption after 5 seconds
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
            
            // InputStream inputStream = socket.getInputStream();
            // BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            // PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            // writer.println("GET " + this.filename + " HTTP/1.1");
            // writer.println("Host: " + this.host);

            // OutputStream out = new FileOutputStream(this.filename);
            // InputStream from = socket.getInputStream();
            // byte[] buffer = new byte[4096];
            // int byte_read;
            // while ((byte_read = from.read(buffer)) != -1) {
            //     out.write(buffer, 0, byte_read);
            // }
            socket.close();
            // out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
