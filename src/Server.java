import ServerStuff.SocketHandler;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public Server() {
        try {
            ServerSocket welcomeSocket = new ServerSocket(6789);

            while (true) {
                System.out.println("Get next client...");
                Socket skt = welcomeSocket.accept();
                // Hand of the processing to the socket handler...
                new Thread(new SocketHandler(skt)).start();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String args[]) {
        new Server();

    }
}