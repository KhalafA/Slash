import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.math.BigInteger;
import java.security.SecureRandom;

public class Application {
    private SecureRandom random = new SecureRandom();

    private Server server;
    private Thread serverThread;
    private final StartPane startPane;

    private Client client;

    private final JFrame frame;


    public static void main(String[] args) {
        new Application();
    }



    public Application(){
        server = new Server(this);
        serverThread = new Thread(server);
        serverThread.start();

        startPane = new StartPane(server.getLocalHostIP(), server.getPort(), getRandomString(), getRandomString(), this);

        setServerStatus(true);

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

    //Client Connecting to a server
    public void setupConnection(String ipField, String portField){
        serverThread.interrupt();

        System.out.println(ipField);
        System.out.println(portField);


        client = new Client(ipField, Integer.parseInt(portField));
        frame.setVisible(false);
        frame.dispose();

        setServerStatus(false);

    }

    private void setServerStatus(boolean status){
        if (server.getReady()){
            startPane.setServerStatus(status);
        }
    }

    private String getRandomString(){
        return new BigInteger(50, random).toString(32);
    }

    public void incomingConnection(){
        frame.setState(Frame.ICONIFIED);
    }
}
