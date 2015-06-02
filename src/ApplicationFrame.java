import javax.swing.*;
import java.io.IOException;

public class ApplicationFrame extends JFrame{
    private JPanel infoPane;
    private ServerHasConnectionsPane connectionPane;

    private JTabbedPane tabbedPane;
    private CapturePane capturePane;

    public ApplicationFrame(JPanel infoPane, Application application){
        super("Slash");
        this.infoPane = infoPane;

        connectionPane = new ServerHasConnectionsPane(application);
        setup();
    }

    private void setup(){
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Info", infoPane);
        tabbedPane.addTab("Clients", connectionPane);

        add(tabbedPane);
        setLocationRelativeTo(null);
        setResizable(true);
        pack();
        setVisible(true);
    }

    public void newConnection(String clientName, int ID){
        connectionPane.newClient(clientName, ID);

        if(tabbedPane.getSelectedIndex() == 0){
            tabbedPane.setSelectedIndex(1);
        }
    }



    public void setupClient(String ip, int port, String pass, String name, String clientName) throws IOException {
        //TODO: Set the capturePane to main

        setTitle("Live Connections");

        capturePane = new CapturePane(ip, port, pass, name, clientName);

        remove(tabbedPane);
        add(capturePane);

        revalidate();
        repaint();
    }

    public void minimize() {
        setState(this.ICONIFIED);
    }

    public void updateTableStatus(int id, boolean capturing) {
        connectionPane.updateTableRow(id, capturing);
    }
}
