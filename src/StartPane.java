import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import sun.tools.jar.Main;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.LinkedList;

public class StartPane extends JPanel{
    private ServerPane serverPane;
    private ClientPane clientPane;
    private final Application application;
    private Timer timer;

    private String name;
    private String pass;
    private String port;
    private String ip;

    private JFrame frame;

    public StartPane(String ip, String port, String name, String pass, Application application, JFrame frame){
        this.application = application;
        this.name = name;
        this.pass = pass;
        this.port = port;
        this.ip = ip;
        this.frame = frame;

        serverPane = new ServerPane(ip, port, name, pass);
        clientPane = new ClientPane();

        setPreferredSize(new Dimension(450,220));
        GridLayout gridLayout = new GridLayout(0,2);

        setLayout(gridLayout);

        add(serverPane);
        add(clientPane);

    }

    public void setServerStatus(boolean status){
        serverPane.setServerStatus(status);
    }

    protected void changeInFormFields(){
        if(timer != null){
            if(timer.isRunning()){
                timer.restart();
            }else {
                setServerStatus(false);
                timer.start();
            }
        }else {
            setServerStatus(false);
            timer = new Timer(5000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    application.restartServer(name, pass, port);
                    stopTimer();
                }
            });
            timer.start();
        }
    }

    private void stopTimer(){
        timer.stop();
    }

    private void updateName(String value){
        name = value;
    }

    private void updatePort(String value){
        port = value;
    }

    private void updatePass(String value){
        pass = value;
    }

    protected void callUpdateMethod(String label, String value){
        Method method = null;
        try {
            method = getClass().getDeclaredMethod("update"+label, String.class);
            method.invoke(this, value);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        changeInFormFields();
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
        private JButton screenShootButton;

        private String ipString;
        private String portString;
        private String nameString;
        private String passString;

        private ServerPane(String ipString, String portString, String nameString, String passString){
            this.ipString = ipString;
            this.portString = portString;
            this.nameString = nameString;
            this.passString = passString;

            windowSetup();
        }

        private void windowSetup(){
            titleSetup("Allow Remote Control");
            serverStatus = false;

            fieldPane = new FieldPane(true);
            formSetup(nameString, ipString, portString, passString);
            fieldPane.setup();

            screenShootButton = new JButton("Select Screenshot Area");
            textField = new JLabel("Setting up...");

            add(fieldPane);
            add(screenShootButton);
            add(textField);

            screenShootButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                                ex.printStackTrace();
                            }

                            JFrame captureFrame = new JFrame();
                            final TransparentBackground bg = new TransparentBackground(captureFrame, application);
                            captureFrame.add(bg);
                            captureFrame.setExtendedState(captureFrame.MAXIMIZED_BOTH);
                            captureFrame.setUndecorated(true);
                            captureFrame.pack();

                            captureFrame.setVisible(true);
                        }
                    });
                }
            });
        }

        private void setServerStatus(boolean status){
            serverStatus = status;
            String statusText = "";
            Color color;

            statusText = serverStatus ? "Ready for Connections!" : "Setting up...";
            color = serverStatus ? Color.GREEN : Color.RED;

            textField.setText(statusText);
            textField.setForeground(color);
        }

        private void titleSetup(String s){
            title = s;

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
               FlieldPane, that is not a server

    */
    private class ClientPane extends JPanel{
        private String title;
        private JButton startBtn;

        private FieldPane fieldPane;

        private ClientPane(){
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
            fieldPane = new FieldPane(false);
            formSetup("Name", "IP", "Port", "Pass");
            fieldPane.setup();
        }

        private void formSetup(String nameString, String ipString, String portString, String passString){
            fieldPane.addFormField("Name", nameString);
            fieldPane.addFormField("IP", ipString);
            fieldPane.addFormField("Port", portString);
            fieldPane.addFormField("Pass", passString);
        }
    }

    /*
        FieldPane Class:
    */
    private class FieldPane extends JComponent{
        private LinkedList<FormField> list;
        private String postFix;
        private FormField fieldNString;

        private boolean isAServerPane;

        private FieldPane(boolean isAServerPane){
            this.isAServerPane = isAServerPane;

            list = new LinkedList<>();
            setPostFix(": ");
        }

        //setup the group layout. Automaticaly builds layout from all elements in the list
        private void setup(){
            int width = 200;
            int height = list.size()*35;

            if(!isAServerPane){
                height+=20;
            }
            setPreferredSize(new Dimension(width,height));

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

        //Add new FormField
        private void addFormField(String label, String value) {
            fieldNString = new FormField(label, value, postFix, isAServerPane);
            list.add(fieldNString);
        }

        //Add new disabled FormField
        private void addFormField(String label, String value, boolean enabled) {
            fieldNString = new FormField(label, value, postFix, isAServerPane);
            fieldNString.getTextField().setEnabled(enabled);
            list.add(fieldNString);
        }

        private void setPostFix(String s) {
            postFix = s;
        }

        //getter for text from FormFields found in list
        private String getField(String name) {
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

    /*
        One Set of Form stuff
            Label, Field, and text
            Application is update on text change.
     */
    private class FormField {
        private JLabel label;
        private JTextField textField;

        private String textString;
        private String fieldValue;

        private boolean changed;

        private String startFieldValue;
        private String endFrieldValue;

        private FormField(String textString, String fieldValue, String postFix, boolean isAServerPane) {
            this.textString = textString;
            this.fieldValue = fieldValue;

            changed = false;

            label = new JLabel(textString + postFix);
            textField = new JTextField(fieldValue);

            if(isAServerPane){
                eventListenerSetup();
            }
        }

        private void eventListenerSetup(){
            textField.getDocument().addDocumentListener(new DocumentListener() {
                Timer fieldTimer;

                @Override
                public void insertUpdate(DocumentEvent e) {
                    updateInOneField();

                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    updateInOneField();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    updateInOneField();
                }

                //Send application update 1 sec after users last edit
                private void updateInOneField() {
                    if (!changed) {
                        startFieldValue = fieldValue;
                        changed = true;
                    }

                    if (fieldTimer != null) {
                        if (fieldTimer.isRunning()) {
                            fieldTimer.restart();
                        } else {
                            fieldTimer.start();
                        }
                    } else {
                        fieldTimer = new Timer(1000, new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent arg0) {
                                endFrieldValue = getFieldValue();
                                changedHappend();
                                setFieldValue(endFrieldValue);
                                changed = false;
                                stopTimer();
                            }
                        });

                        fieldTimer.start();
                    }
                }

                private void stopTimer() {
                    fieldTimer.stop();
                }

                private void changedHappend() {
                    if (!startFieldValue.equals(endFrieldValue)) {
                        //System.out.println("change in: " + textString + " from: " + startFieldValue + " To: " + endFrieldValue);
                        callUpdateMethod(textString, endFrieldValue);
                    }
                }
            });
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

        private void setFieldValue(String value){
            fieldValue = value;
        }
    }
}

