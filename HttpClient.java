import java.net.MalformedURLException;
import java.net.URL;

public class HttpClient {

    String protocol, host, filename;
    int port;

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: You must provide a URL as first argument");
            System.exit(1);
        }
        HttpClient.readURL(args[0]);
    }

    void readURL(String in) {
        try {
            URL url = new URL(in);
            System.out.println(url.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    String getURL(String url) {
        // return the content of the URL
        return null;
    }
}
