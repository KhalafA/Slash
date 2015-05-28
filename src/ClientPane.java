import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClientPane extends JPanel {
    private Application application;

    private String title;
    private JButton startBtn;

    private FieldPane fieldPane;

    private String ipString, portString, nameString, passString;

    public ClientPane(final Application application){
        this.application = application;

        title = "Control Remote Computer";

        setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder(title),
                        BorderFactory.createEmptyBorder(0, 0, 0, 0)));

        startBtn = new JButton("Connect to Partner");
        fieldPane = new FieldPane("IP", "Port", "Name", "Pass", true);

        add(fieldPane);
        add(startBtn);

        startBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                application.setupConnection(fieldPane.getIpField().getText(), fieldPane.getPortField().getText());
            }
        });
    }


}
