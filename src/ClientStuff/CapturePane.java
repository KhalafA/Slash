package ClientStuff;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class CapturePane extends JPanel {
    private Socket socket;
    private ScreenPane screenPane;
    private JButton grabButton;

    private Receiver receiver;
    private Thread receiverThread;

    public CapturePane() {
        setLayout(new BorderLayout());
        screenPane = new ClientStuff.ScreenPane();
        grabButton = new JButton("Grab");
        try {
            socket = new Socket("localhost", 6789);
        } catch (IOException ex) {
            grabButton.setEnabled(false);
            ex.printStackTrace();
        }
        add(screenPane);
        add(grabButton, BorderLayout.SOUTH);

        grabButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                receiver = new Receiver(socket, screenPane);
                receiverThread = new Thread(receiver);
                receiverThread.start();

            }
        });
    }

    public void close() throws IOException {
        receiver.close();
    }
}
