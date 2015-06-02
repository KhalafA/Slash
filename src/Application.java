import javax.swing.*;
import java.io.IOException;
import java.math.BigInteger;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.HashMap;

public class Application {
    private SecureRandom random = new SecureRandom();

    private Server server;
    private Thread serverThread;

    private boolean serverStatus;

    private final StartPane startPane;

    private int liveConnections = 0;
    private final static CaptureView captureView = new CaptureView();

    private HashMap<Integer, Socket> connections;

    private ApplicationFrame applicationFrame;

    //TODO: Let User select which parts he wants clients to see
    public Application(){
        String pass = getRandomString();
        String name = getRandomString();

        connections = new HashMap<>();

        serverStatus = false;
        startServer(name, pass, 0);

        startPane = new StartPane(server.getLocalHostIP(), server.getPort() + "",name,pass, this);
        startPane.setServerStatus(serverStatus);

        applicationFrame = new ApplicationFrame(startPane, this);
    }

    /*
       ------------------------ Server -------------------------------------
     */

    private void startServer(String name, String pass, int port){
        server = new Server(name, pass, port, this, captureView);
        serverThread = new Thread(server);
        serverThread.start();
    }

    //Someone has connected to server
    public void incomingConnection(Verification v, int ID){
        applicationFrame.newConnection(v.getClientName(), ID);

        //minimizeWindow();
    }


    public void serverReadyForConnections(){
        serverStatus = true;
        if(startPane != null){
            startPane.setServerStatus(serverStatus);
        }
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

    private boolean isPortAvailable(int port){
        boolean result = true;

        try {
            (new Socket("localhost", port)).close();

            result = false;
        }
        catch(SocketException e) {
            // Could not connect.
        } catch (UnknownHostException e) {
        } catch (IOException e) {
        }catch (IllegalArgumentException e){
        }

        return result;
    }

    public void kick(Object valueAt) {
        int id = (int) valueAt;

        try {
            server.kick(id);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /*
       ------------------------ Client -------------------------------------
     */

    //Client Connecting to a server
    public void setupConnection(String ipField, String portField, String passField, String nameField, String clientName) {
        try {
            server.stop();
            serverStatus = true;
            startPane.setServerStatus(serverStatus);

            applicationFrame.setupClient(ipField, Integer.parseInt(portField), passField, nameField, clientName);

        }catch (ConnectException e){
            errorMsg("Could not locate server");
        } catch (IOException ex){
            errorMsg("Could not close Server");
        }
    }

    //client got kicked from server
    public void iGotKicked() {
        applicationFrame.clientDisconnected();
    }

    /*
       ------------------------ Util --------------------------------------
     */
    private String getRandomString(){
        return new BigInteger(50, random).toString(32);
    }

    public Integer tryParse(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            errorMsg("Port has to be a number");
            return -1;
        }
    }

    /*
        ------------------------ Gui Manipulation --------------------------
    */
    public void minimizeWindow(){
        applicationFrame.minimize();
    }

    public void setCaptureView(int x, int y, int xx, int yy){
        captureView.setStats(x,y,xx,yy);
    }

    public void setClientStatus(int id, boolean capturing) {
        applicationFrame.updateTableStatus(id, capturing);
    }

    /*
        ------------------------ Error ------------------------------------
    */
    public void errorMsg(String s){
        JOptionPane.showMessageDialog(new JFrame(), s, "Dialog",
                JOptionPane.ERROR_MESSAGE);
    }

    /*
        ------------------------ Main -------------------------------------
    */
    public static void main(String[] args) {
        new Application();
    }
}
