package Standard;

import Auth.AuthenticationMsg;
import GUI.ApplicationFrame;
import GUI.Logic.CaptureLogic;
import GUI.View.StartPane;


import javax.swing.*;
import java.io.IOException;
import java.math.BigInteger;
import java.net.Socket;
import java.security.SecureRandom;

public class Application {
    private Server server;
    private Thread serverThread;
    private boolean isServerReadyForConnections;

    //Main GUI
    private ApplicationFrame applicationFrame;
    private StartPane startPane;

    //Capture Area
    private CaptureLogic captureLogic = new CaptureLogic();

    public Application(){

        String pass = getRandomString();
        String name = getRandomString();

        isServerReadyForConnections = false;
        startServer(name, pass, 0);

        startPane = new StartPane(server.getLocalHostIP(), server.getPort() + "",name,pass, this);
        startPane.setServerStatus(isServerReadyForConnections);

        applicationFrame = new ApplicationFrame(startPane, this);
    }

    /*
       ------------------------ Server -------------------------------------
     */

    private void startServer(String name, String pass, int port){
        server = new Server(name, pass, port, this, captureLogic);
        serverThread = new Thread(server);
        serverThread.start();
    }

    //Someone has connected to server
    public void incomingConnection(AuthenticationMsg v, int ID){
        applicationFrame.newConnection(v.getClientName(), ID);
    }

    //Servers setup was succesfull
    public void serverReadyForConnections(){
        isServerReadyForConnections = true;

        //this might be called before startpane is set up.
        if(startPane != null){
            startPane.setServerStatus(isServerReadyForConnections);
        }
    }

    public void restartServer(String name, String pass, String port){
        isServerReadyForConnections = false;
        startPane.setServerStatus(isServerReadyForConnections);

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
        catch(Exception e) {
            //Socket is Available
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

    //client Disconnected from server
    public void clientDisconnected(int id) {
        applicationFrame.clientDisconnected(id);
    }

    /*
       ------------------------ Client -------------------------------------
     */

    //Client Connecting to a server
    public void setupConnection(String ipField, String portField, String passField, String nameField, String clientName) {
        try {
            server.stop();
        } catch (IOException e) {
            errorMsg(Constants.failedToCloseServer);
        }

        try {
            isServerReadyForConnections = true;
            startPane.setServerStatus(isServerReadyForConnections);

            int portNumber = tryParse(portField);

            applicationFrame.setupClient(ipField, portNumber, passField, nameField, clientName);
        } catch (IOException e) {
            errorMsg(Constants.serverNotFound);
        }
    }

    //client got kicked from server
    public void iGotKicked() {
        applicationFrame.clientKicked();
        errorMsg(Constants.lostConnectionToServer);
    }

    /*
       ------------------------ Util --------------------------------------
     */
    private String getRandomString(){
        SecureRandom random = new SecureRandom();

        return new BigInteger(50, random).toString(32);
    }

    public Integer tryParse(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            errorMsg(Constants.illegalPortNumber);
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
        captureLogic.setStats(x,y,xx,yy);
    }

    public void setClientStatus(int id, boolean capturing) {
        applicationFrame.updateTableStatus(id, capturing);
    }

    public void updateTitle(String s) {
        applicationFrame.updateTitle(s);
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
