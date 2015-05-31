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

    private JPanel btnPannel;

    private Receiver receiver;
    private Thread receiverThread;

    private JFrame viewFrame;

    public CapturePane(String ip, int port, String pass, String name, JFrame viewFrame) throws IOException {
        this.viewFrame = viewFrame;

        setLayout(new BorderLayout());
        screenPane = new ScreenPane();
        startCaptureButton = new JButton("Capture");
        stopCaptureButton = new JButton("Stop Capturing");

        btnPannel = new JPanel();
        add(screenPane);

        btnPannel.add(startCaptureButton);
        btnPannel.add(stopCaptureButton);

        socket = new Socket(ip, port);

        Verify verify = new Verify(socket, pass, name, this);
        new Thread(verify).start();

        startCaptureButton.setEnabled(false);
        stopCaptureButton.setEnabled(false);


        stopCaptureButton.setEnabled(false);

        add(btnPannel, BorderLayout.SOUTH);

        startCaptureButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toogleButtons();

                receiver = new Receiver(socket, screenPane, "grab");
                receiverThread = new Thread(receiver);
                receiverThread.start();

                updateTitle("Live view");
            }
        });

        stopCaptureButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toogleButtons();

                receiver.setRequest("stop");

                updateTitle("Paused... ");
            }
        });
    }

    private void updateTitle(final String s){
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                viewFrame.setTitle(s);
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
