package GUI.View;

import Auth.Verification;
import Standard.Application;
import Standard.Constants;
import GUI.Logic.Receiver;
import Standard.MouseEventSender;
import javafx.scene.input.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class CapturePane extends JPanel {
    private Socket socket;
    private JPanel screenPane;

    private JButton startCaptureButton;
    private JButton pauseCapturingButton;
    private JButton stopCapturingButton;
    private JButton requestControlButton;

    private JPanel btnPanel;

    private Receiver receiver;
    private Thread receiverThread;

    private MouseEventSender eventSender;
    private Thread eventSenderThread;

    private Application application;

    private boolean controlStatus;

    private String ip;

    public CapturePane(String ip, int port, String pass, String name, String clientName, final Application application) throws IOException {
        this.application = application;
        this.ip = ip;

        controlStatus = false;

        setLayout(new BorderLayout());
        screenPane = new ScreenPane();
        add(screenPane);


        startCaptureButton = new JButton(Constants.startRequest);
        pauseCapturingButton = new JButton(Constants.pauseRequest);
        stopCapturingButton = new JButton(Constants.stopRequest);
        requestControlButton = new JButton("Control");

        btnPanel = new JPanel();

        btnPanel.add(startCaptureButton);
        btnPanel.add(pauseCapturingButton);
        btnPanel.add(requestControlButton);
        btnPanel.add(stopCapturingButton);

        socket = new Socket(ip, port);

        Verification verification = new Verification(socket, pass, name, this, clientName);
        new Thread(verification).start();

        startCaptureButton.setEnabled(false);
        pauseCapturingButton.setEnabled(false);
        requestControlButton.setEnabled(false);

        add(btnPanel, BorderLayout.SOUTH);

        final CapturePane thisPane = this;

        startCaptureButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toogleButtons();

                receiver = new Receiver(socket,(ScreenPane) screenPane, Constants.startRequest, application, thisPane);
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

        requestControlButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                boolean temp = !controlStatus;
                controlStatus = temp;

                System.out.println("Clicked Control " + controlStatus);

                if(controlStatus){
                    receiver.setRequest("Control");

                    requestControlButton.setText("Stop Controlling");
                }else {
                    requestControlButton.setText("Control");
                }
            }
        });


        stopCapturingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(receiver != null){
                    receiver.disconnected(false);
                }else {
                    close();
                    application.lostConnection(false);
                }
            }
        });
    }


    private void close(){
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void toogleButtons(){
        boolean enabled = startCaptureButton.isEnabled();

        startCaptureButton.setEnabled(!enabled);
        pauseCapturingButton.setEnabled(enabled);
        requestControlButton.setEnabled(enabled);
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

    public void startSendingEvents(String s) {
        String[] split = s.split(":");

        String portString = split[1];
        int port = Integer.parseInt(portString);

        //start a new event sender on a new thread
        eventSender = new MouseEventSender(ip, port, screenPane);
        eventSenderThread = new Thread(eventSender);
        eventSenderThread.start();
    }
}
