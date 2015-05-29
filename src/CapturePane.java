import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class CapturePane extends JPanel {
    private Socket socket;
    private ScreenPane screenPane;

    private JButton startCaptureButton;
    private JButton stopCaptureButton;
    private JButton requestControlButton;

    private JPanel btnPannel;

    private Receiver receiver;
    private Thread receiverThread;

    public CapturePane(String ip, int port, String pass, String name) {
        setLayout(new BorderLayout());
        screenPane = new ScreenPane();
        startCaptureButton = new JButton("Capture");
        stopCaptureButton = new JButton("Stop Capturing");

        btnPannel = new JPanel();
        add(screenPane);

        btnPannel.add(startCaptureButton);
        btnPannel.add(stopCaptureButton);


        try {
            socket = new Socket(ip, port);

            System.out.println("Starting Verfication class");
            Verify verify = new Verify(socket, pass, name, this);
            new Thread(verify).start();

            startCaptureButton.setEnabled(false);
            stopCaptureButton.setEnabled(false);

        } catch (IOException ex) {
            System.out.println("Could not connect");
            System.exit(0);
        }


        stopCaptureButton.setEnabled(false);

        add(btnPannel, BorderLayout.SOUTH);

        startCaptureButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                toogleButtons();

                if(receiver == null){
                    receiver = new Receiver(socket, screenPane);
                    receiver.setRequest("grab");
                    receiverThread = new Thread(receiver);
                    receiverThread.start();
                }else {
                    receiver.setRequest("grab");
                }
            }
        });

        stopCaptureButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                receiver.setRequest("stop");

                toogleButtons();


            }
        });
    }

    private void toogleButtons(){
        boolean enabled = startCaptureButton.isEnabled();

        startCaptureButton.setEnabled(!enabled);
        stopCaptureButton.setEnabled(enabled);
    }

    public void verified(){
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                startCaptureButton.setEnabled(true);
            }
        });

    }
}
