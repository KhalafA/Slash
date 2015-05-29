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

    public CapturePane(String ip, int port) {
        setLayout(new BorderLayout());
        screenPane = new ScreenPane();
        startCaptureButton = new JButton("Capture");
        requestControlButton = new JButton("Control");
        stopCaptureButton = new JButton("Stop Capturing");

        btnPannel = new JPanel();

        try {
            socket = new Socket(ip, port);
        } catch (IOException ex) {
            System.out.println("Could not connect");
            System.exit(0);
        }
        add(screenPane);

        btnPannel.add(startCaptureButton);
        btnPannel.add(requestControlButton);
        btnPannel.add(stopCaptureButton);

        stopCaptureButton.setEnabled(false);

        add(btnPannel, BorderLayout.SOUTH);

        startCaptureButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                startCaptureButton.setEnabled(false);
                stopCaptureButton.setEnabled(true);

                receiver = new Receiver(socket, screenPane, "grab");
                receiverThread = new Thread(receiver);
                receiverThread.start();

                System.out.println("------------------");
                System.out.println("Started");
            }
        });


        stopCaptureButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                receiver.interrupt();

                startCaptureButton.setEnabled(true);
                stopCaptureButton.setEnabled(false);

                System.out.println("------------------");
                System.out.println("Closed");
            }
        });


    }

    public void writeRequest(OutputStream os, String request) throws IOException {
        os.write((request + "\n").getBytes());
        os.flush();
    }

}
