import ServerStuff.SocketHandler;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Server {

    public Server() {
        try {
            ServerSocket welcomeSocket = new ServerSocket(6789);

            while (true) {
                System.out.println("Get next client...");

                Socket skt = welcomeSocket.accept();

                new Thread(new SocketHandler(skt)).start();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public String getLocalHostIP() throws UnknownHostException {
        InetAddress localhost = InetAddress.getLocalHost();
        return localhost.getHostAddress();
    }

    public static void main(String args[]) {
        new Server();

    }
}