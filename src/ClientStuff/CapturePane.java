package ClientStuff;

import javafx.scene.paint.Stop;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class CapturePane extends JPanel {
    private Socket socket;
    private ScreenPane screenPane;
    private JButton grabButton, stopButton;
    private JPanel btnPannel;

    private Receiver receiver;
    private Thread receiverThread;

    public CapturePane() {
        setLayout(new BorderLayout());
        screenPane = new ClientStuff.ScreenPane();
        grabButton = new JButton("Grab");
        stopButton = new JButton("Stop");

        btnPannel = new JPanel();

        try {
            socket = new Socket("192.168.87.105", 6789);
        } catch (IOException ex) {
            System.out.println("Could not connect");
            System.exit(0);
        }
        add(screenPane);

        btnPannel.add(grabButton, BorderLayout.NORTH);
        btnPannel.add(stopButton, BorderLayout.SOUTH);

        stopButton.setEnabled(false);

        add(btnPannel, BorderLayout.SOUTH);

        grabButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                grabButton.setEnabled(false);
                stopButton.setEnabled(true);

                receiver = new Receiver(socket, screenPane);
                receiverThread = new Thread(receiver);
                receiverThread.start();
            }
        });

        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                close();
            }
        });
    }

    public void close(){
        grabButton.setEnabled(true);
        stopButton.setEnabled(false);

        try {
            receiver.close();
            receiverThread.interrupt();
            receiverThread.join();

        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
    }
}
