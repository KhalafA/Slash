package GUI.View;

import Auth.Verification;
import Standard.Application;
import Standard.Constants;
import Standard.Receiver;

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
    private JButton pauseCapturingButton;
    private JButton stopCapturingButton;

    private JPanel btnPanel;

    private Receiver receiver;
    private Thread receiverThread;

    private Application application;

    public CapturePane(String ip, int port, String pass, String name, String clientName, final Application application) throws IOException {
        this.application = application;

        setLayout(new BorderLayout());
        screenPane = new ScreenPane();
        startCaptureButton = new JButton(Constants.startRequest);
        pauseCapturingButton = new JButton(Constants.pauseRequest);
        stopCapturingButton = new JButton(Constants.stopRequest);

        btnPanel = new JPanel();
        add(screenPane);

        btnPanel.add(startCaptureButton);
        btnPanel.add(pauseCapturingButton);
        btnPanel.add(stopCapturingButton);

        socket = new Socket(ip, port);

        Verification verification = new Verification(socket, pass, name, this, clientName);
        new Thread(verification).start();

        startCaptureButton.setEnabled(false);
        pauseCapturingButton.setEnabled(false);


        pauseCapturingButton.setEnabled(false);

        add(btnPanel, BorderLayout.SOUTH);

        startCaptureButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toogleButtons();

                receiver = new Receiver(socket, screenPane, Constants.startRequest, application);
                receiverThread = new Thread(receiver);
                receiverThread.start();

                updateTitle(Constants.captureViewTitle + Constants.CAPTURING);
            }
        });

        pauseCapturingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toogleButtons();

                receiver.setRequest(Constants.pauseRequest);

                updateTitle(Constants.captureViewTitle + Constants.PAUSED);
            }
        });

        stopCapturingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Trying to dc");
                receiver.setRequest(Constants.stopRequest);
                receiver.disconnected();
            }
        });


    }

    private void toogleButtons(){
        boolean enabled = startCaptureButton.isEnabled();

        startCaptureButton.setEnabled(!enabled);
        pauseCapturingButton.setEnabled(enabled);
    }

    public void verified(){
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                startCaptureButton.setEnabled(true);
            }
        });
    }

    private void updateTitle(final String s){
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                application.updateTitle(s);
            }
        });
    }
}
