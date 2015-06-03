package GUI;

import GUI.View.CapturePane;
import GUI.View.ServerHasConnectionsPane;
import Standard.Application;
import Standard.Constants;

import javax.swing.*;
import java.io.IOException;

public class ApplicationFrame extends JFrame{
    private Application application;
    private JPanel infoPane;
    private ServerHasConnectionsPane connectionPane;

    private JTabbedPane tabbedPane;
    private CapturePane capturePane;

    public ApplicationFrame(JPanel infoPane, Application application){
        super(Constants.mainTitle);
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

        tabbedPane.addTab(Constants.mainTab, infoPane);
        tabbedPane.addTab(Constants.clientListTab, connectionPane);

        add(tabbedPane);
        setLocationRelativeTo(null);
        setResizable(false);
        pack();
        setVisible(true);
    }

    public void newConnection(String clientName, int ID){
        connectionPane.newClient(clientName, ID);

        if(tabbedPane.getSelectedIndex() == Constants.mainTabID){
            tabbedPane.setSelectedIndex(Constants.clientListTabID);
        }
    }



    public void setupClient(String ip, int port, String pass, String name, String clientName) throws IOException {
        setSize(Constants.captureViewWidth, Constants.captureViewHeight);
        setTitle(Constants.captureViewTitle);
        setResizable(true);

        capturePane = new CapturePane(ip, port, pass, name, clientName, application);

        remove(tabbedPane);
        add(capturePane);

        revalidate();
        repaint();
    }

    public void ClientDisconnected(){
        setResizable(false);

        remove(capturePane);
        add(tabbedPane);

        revalidate();
        repaint();
        pack();
    }

    public void clientDisconnected(int id){
        setResizable(false);
        
        connectionPane.removeClient(id);
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
