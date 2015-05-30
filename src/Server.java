import com.sun.org.apache.xpath.internal.SourceTree;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;


public class Server implements Runnable{
    private String name;
    private String pass;
    private String localHostIp;
    private int port;

    private boolean running;

    private Application application;
    private SocketHandler socketHandler;

    private List<Socket> sockets;
    private ServerSocket serverSocket;

    public Server(String name, String pass, int port,  Application application){
        this.application = application;
        this.name = name;
        this.pass = pass;
        this. port = port;

        localHostIp = getLocalHostIP();
        sockets = new LinkedList<>();
        running = true;
    }

    @Override
    public void run() {
        while (running){
            try {
                serverSocket = new ServerSocket(port);
                port = serverSocket.getLocalPort();

                System.out.println("--------------------------------------------------");
                System.out.println("Server was successfully setup");
                System.out.println("Connect on ip: " + localHostIp + ", Port: " + port + ", Name: " + name + ", Pass: " + pass);


                application.serverReadyForConnections();


                while(true){
                    System.out.println("Get next client...");

                    Socket socket = serverSocket.accept();

                    sockets.add(socket);

                    notifyNewConnection();

                    socketHandler = new SocketHandler(socket, name, pass);

                    new Thread(socketHandler).start();
                }
            }catch (IOException ex) {
                //Failed to setup socket, or user server needs to reset
            }
        }
    }

    public void stop() throws IOException {
        running = false;
        for (Socket socket : sockets){
            socket.close();
        }

        serverSocket.close();
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
}