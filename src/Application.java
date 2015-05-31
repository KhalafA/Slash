import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.math.BigInteger;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.SecureRandom;

public class Application {
    private SecureRandom random = new SecureRandom();

    private Server server;
    private Thread serverThread;

    private boolean serverStatus;

    private final JFrame frame;
    private final StartPane startPane;

    private int liveConnections = 0;
    private final static CaptureView captureView = new CaptureView();

    //TODO: Let User select which parts he wants clients to see
    public Application(){
        String pass = getRandomString();
        String name = getRandomString();

        serverStatus = false;
        startServer(name, pass, 0);

        frame = new JFrame("Slash");

        startPane = new StartPane(server.getLocalHostIP(), server.getPort() + "",name,pass, this, frame);
        startPane.setServerStatus(serverStatus);



        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                    ex.printStackTrace();
                }

                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                frame.add(startPane);
                frame.setLocationRelativeTo(null);
                frame.setResizable(true);
                frame.pack();
                frame.setVisible(true);

            }
        });
    }

    public void setCaptureView(int x, int y, int xx, int yy){
        captureView.setStats(x,y,xx,yy);
    }

    public void minimizeWindow(){
        frame.setState(frame.ICONIFIED);
    }

    public void restartServer(String name, String pass, String port){
        serverStatus = false;
        startPane.setServerStatus(serverStatus);

        try {
            server.stop();
            serverThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int portNumber = tryParse(port);

        if(isPortAvailable(portNumber) && portNumber > 0){
            startServer(name, pass, portNumber);
        }else {
            startServer(name, pass, 0);
        }
    }

    public Integer tryParse(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            errorMsg("Port has to be a number");
            return -1;
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
        server = new Server(name, pass, port, this, captureView);
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
            //server.stop();
            serverStatus = true;
            startPane.setServerStatus(serverStatus);


            Client client = new Client(ipField, Integer.parseInt(portField), passField, nameField);
            frame.setVisible(false);
            frame.dispose();
        }catch (ConnectException e){
            errorMsg("Could not locate server");
        } catch (IOException ex){
            errorMsg("Could not close Server");
        }
    }

    private String getRandomString(){
        return new BigInteger(50, random).toString(32);
    }

    public void incomingConnection(){
        liveConnections++;
        System.out.println(liveConnections);

        minimizeWindow();
    }

    public void errorMsg(String s){
        JOptionPane.showMessageDialog(new JFrame(), s, "Dialog",
                JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        new Application();
    }
}
