import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

public class StartPane extends JPanel{
    private ServerPane serverPane;
    private ClientPane clientPane;

    public StartPane(String ip, int port, String name, String pass, Application application){
        serverPane = new ServerPane(ip, port + "", name, pass);
        clientPane = new ClientPane(application);

        setPreferredSize(new Dimension(450,250));
        GridLayout gridLayout = new GridLayout(0,2);

        setLayout(gridLayout);

        add(serverPane);
        add(clientPane);
    }

    public void setServerStatus(boolean status){
        serverPane.setServerStatus(status);
    }

    /*
        ServerPane Class
            Has:
                FlieldPane
    */
    private class ServerPane extends JPanel{
        private String title;
        private JLabel textField;
        private FieldPane fieldPane;

        private boolean serverStatus;

        public ServerPane(String ipString, String portString, String nameString, String passString){
            titleSetup();

            serverStatus = false;
            textField = new JLabel("Setting up...");

            fieldPane = new FieldPane();
            formSetup(nameString, ipString, portString, passString);
            fieldPane.setup();

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

        private void titleSetup(){
            title = "Allow Remote Control";

            setBorder(
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createTitledBorder(title),
                            BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        }

        private void formSetup(String nameString, String ipString, String portString, String passString){
            fieldPane.addFormField("Name", nameString);
            fieldPane.addFormField("IP", ipString, false);
            fieldPane.addFormField("Port", portString);
            fieldPane.addFormField("Pass", passString);
        }
    }

    /*
       ClientPane Class
           Has:
               FlieldPane
    */
    private class ClientPane extends JPanel{
        private Application application;

        private String title;
        private JButton startBtn;

        private FieldPane fieldPane;

        public ClientPane(final Application application){
            this.application = application;

            title = "Control Remote Computer";

            setBorder(
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createTitledBorder(title),
                            BorderFactory.createEmptyBorder(0, 0, 0, 0)));

            startBtn = new JButton("Connect to Partner");
            buildFieldPane();


            add(fieldPane);
            add(startBtn);

            startBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    application.setupConnection(fieldPane.getField("IP"), fieldPane.getField("Port"), fieldPane.getField("Pass"), fieldPane.getField("Name"));
                }
            });
        }

        private void buildFieldPane(){
            fieldPane = new FieldPane();
            formSetup("Name", "IP", "Port", "Pass");
            fieldPane.setup();
        }

        private void formSetup(String nameString, String ipString, String portString, String passString){
            fieldPane.addFormField("Name", nameString);
            fieldPane.addFormField("IP", ipString);
            fieldPane.addFormField("Port", portString);
            fieldPane.addFormField("Pass", passString);
        }

        private void addFormField(String label, String value){
            fieldPane.addFormField(label, value);
        }
    }

    /*
        FieldPane Class
    */
    private class FieldPane extends JComponent{
        private LinkedList<FormField> list;
        private String postFix;
        private FormField fieldNString;

        public FieldPane(){
            list = new LinkedList<>();
            setPostFix(": ");
        }

        private void setup(){
            setPreferredSize(new Dimension(200,200));

            GroupLayout layout = new GroupLayout(this);
            setLayout(layout);

            layout.setAutoCreateGaps(true);
            layout.setAutoCreateContainerGaps(true);

            GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
                GroupLayout.ParallelGroup hGroupLabel = layout.createParallelGroup(GroupLayout.Alignment.CENTER);
                GroupLayout.ParallelGroup hGroupField = layout.createParallelGroup(GroupLayout.Alignment.CENTER);

            for(FormField entry : list){
                hGroupLabel.addComponent(entry.getLabel());
                hGroupField.addComponent(entry.getTextField());
            }
            hGroup.addGroup(hGroupLabel);
            hGroup.addGroup(hGroupField);

            layout.setHorizontalGroup(hGroup);


            GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

            for(FormField entry : list){
                vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).
                        addComponent(entry.getLabel()).addComponent(entry.getTextField()));
            }

            layout.setVerticalGroup(vGroup);
        }

        private void addFormField(String label, String value) {
            fieldNString = new FormField(label, value, postFix);
            list.add(fieldNString);
        }

        private void addFormField(String label, String value, boolean enabled) {
            fieldNString = new FormField(label, value, postFix);
            fieldNString.getTextField().setEnabled(enabled);
            list.add(fieldNString);
        }

        private void setPostFix(String s) {
            postFix = s;
        }

        public String getField(String name) {
            boolean found = false;
            String result = "";
            int counter = 0;

            while (!found){
                if(list.get(counter).getTextString().equals(name)){
                    result = list.get(counter).getFieldValue();
                    found = true;
                }else {
                    counter++;
                }
            }

            return result;
        }
    }

    private class FormField {
        private JLabel label;
        private JTextField textField;

        private String textString;
        private String postFix;
        private String fieldValue;

        public FormField(String textString, String fieldValue, String postFix) {
            this.textString = textString;
            this.fieldValue = fieldValue;
            this.postFix = postFix;

            build();
        }

        private void build(){
            label = new JLabel(textString + postFix);
            textField = new JTextField(fieldValue);
        }

        public JLabel getLabel() {
            return label;
        }

        public JTextField getTextField() {
            return textField;
        }

        public String getTextString() {
            return textString;
        }

        public String getFieldValue() {
            return textField.getText();
        }
    }
}

