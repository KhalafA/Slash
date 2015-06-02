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
    private JButton pauseCapturing;
    private JButton stopCapturing;

    private JPanel btnPannel;

    private Receiver receiver;
    private Thread receiverThread;

    private Application application;

    public CapturePane(String ip, int port, String pass, String name, String clientName, final Application application) throws IOException {
        this.application = application;

        setLayout(new BorderLayout());
        screenPane = new ScreenPane();
        startCaptureButton = new JButton("Capture");
        pauseCapturing = new JButton("Pause");
        stopCapturing = new JButton("Disconnect");

        btnPannel = new JPanel();
        add(screenPane);

        btnPannel.add(startCaptureButton);
        btnPannel.add(pauseCapturing);

        socket = new Socket(ip, port);

        Verify verify = new Verify(socket, pass, name, this, clientName);
        new Thread(verify).start();

        startCaptureButton.setEnabled(false);
        pauseCapturing.setEnabled(false);


        pauseCapturing.setEnabled(false);

        add(btnPannel, BorderLayout.SOUTH);

        startCaptureButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toogleButtons();

                receiver = new Receiver(socket, screenPane, "grab", application);
                receiverThread = new Thread(receiver);
                receiverThread.start();

                //updateTitle("Live view");
            }
        });

        pauseCapturing.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toogleButtons();

                receiver.setRequest("stop");

                //updateTitle("Paused... ");
            }
        });
    }

    private void toogleButtons(){
        boolean enabled = startCaptureButton.isEnabled();

        startCaptureButton.setEnabled(!enabled);
        pauseCapturing.setEnabled(enabled);
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
