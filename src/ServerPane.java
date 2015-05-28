import javax.swing.*;

public class ServerPane extends JPanel {
    private String title;
    private JLabel textField;

    private FieldPane fieldPane;

    private String ipString, portString, nameString, passString;
    private boolean serverStatus;

    public ServerPane(String ipString, String portString, String nameString, String passString){
        this.ipString = ipString;
        this.portString = portString;
        this.nameString = nameString;
        this.passString = passString;

        serverStatus = false;

        title = "Allow Remote Control";

        setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder(title),
                        BorderFactory.createEmptyBorder(0, 0, 0, 0)));

        textField = new JLabel("Setting up...");
        fieldPane = new FieldPane(ipString, portString, nameString, passString, false);

        add(fieldPane);
        add(textField);
    }

    public void setServerStatus(boolean status){
        serverStatus = status;

        if(serverStatus){
            textField.setText("Ready for Connections!");
        }else {
            textField.setText("Setting up...");
        }
    }
}
