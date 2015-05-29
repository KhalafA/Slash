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

    private String name;
    private String pass;

    private SocketHandler socketHandler;

    public Server(Application application, String pass, String name){
        this.application = application;
        this.pass = pass;
        this.name = name;

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

                socketHandler = new SocketHandler(skt, name, pass);

                new Thread(socketHandler).start();
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