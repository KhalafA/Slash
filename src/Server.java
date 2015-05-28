import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;


public class Server implements Runnable{
    private String localHostIp;
    private int port;
    private boolean isReady;

    private Application application;

    public Server(Application application){
        this.application = application;

        localHostIp = getLocalHostIP();
        isReady = false;
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(0);
            port = serverSocket.getLocalPort();

            System.out.println("Connect on ip: " + localHostIp + ", Port: " + port);
            isReady = true;

            while(!Thread.currentThread().isInterrupted()){
                System.out.println("Get next client...");
                Socket skt = serverSocket.accept();

                notifyNewConnection();

                new Thread(new SocketHandler(skt)).start();
            }
        }catch (IOException ex) {
            System.out.println("Failed set up ServerSocket");
            ex.printStackTrace();
        }
    }

    private void notifyNewConnection(){
        application.incomingConnection();
    }

    public String getLocalHostIP(){
        InetAddress localhost = null;
        try {
            localhost = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return localhost.getHostAddress();
    }

    public int getPort(){
        return port;
    }

    public boolean getReady(){
        return isReady;
    }


}