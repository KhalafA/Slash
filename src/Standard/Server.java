package Standard;

import Auth.AuthenticationMsg;
import GUI.Logic.CaptureLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;


public class Server implements Runnable{
    private String name;
    private String pass;
    private int port;

    private boolean running;

    private Application application;
    private SocketHandler socketHandler;

    private HashMap<Integer, SocketHandler> socketHandlers;
    private ServerSocket serverSocket;

    private CaptureLogic captureLogic;

    private int counter;

    public Server(String name, String pass, int port, Application application, CaptureLogic captureLogic){
        this.application = application;
        this.name = name;
        this.pass = pass;
        this. port = port;
        this.captureLogic = captureLogic;

        socketHandlers = new HashMap<>();
        running = true;

        counter = 0;
    }

    @Override
    public void run() {
        while (running){
            try {
                serverSocket = new ServerSocket(port);
                port = serverSocket.getLocalPort();

                application.serverReadyForConnections();


                while(true){

                    Socket socket = serverSocket.accept();

                    socketHandler = new SocketHandler(socket, name, pass, captureLogic, this, counter);
                    socketHandlers.put(counter, socketHandler);
                    counter++;
                    new Thread(socketHandler).start();
                }
            }catch (IOException ex) {
                //Closed socket, to start new server with new connection info
            }
        }
    }

    public void kick(int id) throws IOException {
        SocketHandler kickClient = socketHandlers.get(id);

        kickClient.closeConnection();
        socketHandlers.remove(id);
    }


    public void stop() throws IOException {
        running = false;
        for (SocketHandler value : socketHandlers.values()){
            value.getSocket().close();
        }

        serverSocket.close();
    }

    public void clientInformation(AuthenticationMsg v, int ID){
        application.incomingConnection(v, ID);
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

    public void updateClientStatus(int id, boolean capturing) {
        application.setClientStatus(id, capturing);
    }

    public void clientDisconnected(int id) {
        socketHandlers.remove(id);
        application.clientDisconnected(id);
    }
}