import javax.swing.*;
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

    private final StartPane startPane;

    private int liveConnections = 0;
    private final static CaptureView captureView = new CaptureView();

    private ApplicationFrame applicationFrame;

    //TODO: Let User select which parts he wants clients to see
    public Application(){
        String pass = getRandomString();
        String name = getRandomString();

        serverStatus = false;
        startServer(name, pass, 0);

        startPane = new StartPane(server.getLocalHostIP(), server.getPort() + "",name,pass, this);
        startPane.setServerStatus(serverStatus);

        applicationFrame = new ApplicationFrame(startPane);
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
    public void incomingConnection(Verification v){
        liveConnections++;

        System.out.println(liveConnections);

        minimizeWindow();
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


    /*
       ------------------------ Client -------------------------------------
     */

    //Client Connecting to a server
    public void setupConnection(String ipField, String portField, String passField, String nameField) {
        try {
            server.stop();
            serverStatus = true;
            startPane.setServerStatus(serverStatus);

            new Client(ipField, Integer.parseInt(portField), passField, nameField);

            //TODO: Setup userInfo in the frame.

        }catch (ConnectException e){
            errorMsg("Could not locate server");
        } catch (IOException ex){
            errorMsg("Could not close Server");
        }
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
