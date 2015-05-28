import javax.swing.*;
import java.awt.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class FieldPane extends JComponent {
    private JLabel ipLabel, portLabel, passLabel, nameLabel;

    private JTextField ipField, portField, passField, nameField;

    private String ipString, portString, nameString, passString;

    private boolean ipFieldEnabled;

    public FieldPane(String ipString, String portString, String nameString, String passString, boolean ipFieldEnabled){
        this.ipString = ipString;
        this.portString = portString;
        this.nameString = nameString;
        this.passString = passString;
        this.ipFieldEnabled = ipFieldEnabled;

        setup();
    }

    private void setup(){
        setPreferredSize(new Dimension(200,200));
        nameLabel = new JLabel("Name: ");
        ipLabel = new JLabel("IP: ");
        portLabel = new JLabel("Port: ");
        passLabel = new JLabel("pass: ");

        ipField = new JTextField(ipString);
        portField = new JTextField(portString);
        nameField = new JTextField(nameString);
        passField = new JTextField(passString);

        ipField.setEnabled(ipFieldEnabled);

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
        hGroup.addGroup(layout.createParallelGroup().
                addComponent(nameLabel).addComponent(ipLabel).addComponent(portLabel).addComponent(passLabel));
        hGroup.addGroup(layout.createParallelGroup().
                addComponent(nameField).addComponent(ipField).addComponent(portField).addComponent(passField));
        layout.setHorizontalGroup(hGroup);

        GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).
                addComponent(nameLabel).addComponent(nameField));
        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).
                addComponent(ipLabel).addComponent(ipField));
        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).
                addComponent(portLabel).addComponent(portField));
        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).
                addComponent(passLabel).addComponent(passField));
        layout.setVerticalGroup(vGroup);
    }

    public JTextField getIpField() {
        return ipField;
    }

    public JTextField getPortField() {
        return portField;
    }

    public JTextField getPassField() {
        return passField;
    }

    public JTextField getNameField() {
        return nameField;
    }
}
