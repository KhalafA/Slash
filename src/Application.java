import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.math.BigInteger;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.SecureRandom;

public class Application {
    private SecureRandom random = new SecureRandom();

    private Server server;
    private Thread serverThread;

    private Client client;
    private Thread clientThread;

    private boolean serverStatus;

    private final JFrame frame;
    private final StartPane startPane;

    private int liveConnections = 0;

    public Application(){
        String pass = getRandomString();
        String name = getRandomString();

        serverStatus = false;
        startServer(name, pass, 0);

        startPane = new StartPane(server.getLocalHostIP(), server.getPort() + "",name,pass, this);
        startPane.setServerStatus(serverStatus);

        frame = new JFrame("Slash");

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                    ex.printStackTrace();
                }

                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.add(startPane);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setResizable(false);
                frame.setVisible(true);

            }
        });
    }


    public void restartServer(String name, String pass, String port){
        serverStatus = false;
        startPane.setServerStatus(serverStatus);

        try {
            server.stop();
            serverThread.join();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int portNumber = Integer.parseInt(port);

        if(isPortAvailable(portNumber)){
            startServer(name, pass, portNumber);
        }else {
            startServer(name, pass, 0);
        }
    }

    private boolean isPortAvailable(int port){
        boolean result = true;

        try {
            (new Socket("localhost", port)).close();

            result = false;
        }
        catch(SocketException e) {
            // Could not connect.
        } catch (UnknownHostException e) {
            //
        } catch (IOException e) {
            //
        }catch (IllegalArgumentException e){
            //
        }

        return result;
    }

    private void startServer(String name, String pass, int port){
        server = new Server(name, pass, port, this);
        serverThread = new Thread(server);
        serverThread.start();
    }

    public void serverReadyForConnections(){
        //System.out.println("Server is ready!");
        serverStatus = true;
        if(startPane != null){
            startPane.setServerStatus(serverStatus);
        }
    }


    //Client Connecting to a server
    public void setupConnection(String ipField, String portField, String passField, String nameField) {
        try {
            server.stop();
        } catch (IOException e) {
            e.printStackTrace();
        }

        client = new Client(ipField, Integer.parseInt(portField), passField, nameField);
        frame.setVisible(false);
        frame.dispose();

        startPane.setServerStatus(false);

    }

    private String getRandomString(){
        return new BigInteger(50, random).toString(32);
    }

    public void incomingConnection(){
        liveConnections++;
        System.out.println(liveConnections);
        frame.setState(Frame.ICONIFIED);
    }

    public static void main(String[] args) {
        new Application();
    }
}
