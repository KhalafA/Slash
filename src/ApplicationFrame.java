import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class ApplicationFrame extends JFrame{
    private Application application;
    private JPanel infoPane;
    private ServerHasConnectionsPane connectionPane;

    private JTabbedPane tabbedPane;
    private CapturePane capturePane;

    public ApplicationFrame(JPanel infoPane, Application application){
        super("Slash");
        this.infoPane = infoPane;
        this.application = application;

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
        setResizable(false);
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
        setSize(450, 350);
        setTitle("Live Connections");
        setResizable(true);

        capturePane = new CapturePane(ip, port, pass, name, clientName, application);

        remove(tabbedPane);
        add(capturePane);

        revalidate();
        repaint();
    }

    public void clientDisconnected(){
        setResizable(false);

        remove(capturePane);
        add(tabbedPane);

        revalidate();
        repaint();
        pack();
    }

    public void minimize() {
        setState(this.ICONIFIED);
    }

    public void updateTableStatus(int id, boolean capturing) {
        connectionPane.updateTableRow(id, capturing);
    }

    public void updateTitle(String s) {
        setTitle(s);
    }
}
