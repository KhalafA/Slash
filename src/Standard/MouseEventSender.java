package Standard;

import GUI.View.ScreenPane;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.LinkedBlockingQueue;

public class MouseEventSender implements Runnable {
    private int port;
    private String ip;
    private boolean running;

    private ScreenPane sceenPane;

    private boolean eventReady;

    private Socket socket;

    private LinkedBlockingQueue<MouseEvents> list;

    public MouseEventSender(String ip, int port, JPanel screenPane) {
        this.ip = ip;
        this.port = port;
        this.sceenPane = (ScreenPane) screenPane;

        ((ScreenPane) screenPane).setMouseEventSender(this);

        running = true;
        list = new LinkedBlockingQueue();
    }

    @Override
    public void run() {
        try{
            socket = new Socket(ip,port);

            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

            while (running){
                if(list.size() >= 1){
                    objectOutputStream.writeObject(list.take()); //SEND!!!!
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void newEvent(MouseEvents e) {
        try {
            list.put(e);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
    }

    public void close(){
        running = false;

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}