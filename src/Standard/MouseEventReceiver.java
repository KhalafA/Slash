package Standard;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class MouseEventReceiver implements Runnable {
    private Socket defSocket;
    private OutputStream os;

    private ObjectOutputStream outStream;
    private ObjectInputStream inStream;

    private boolean running;

    private ServerSocket serverSocket;

    private SocketHandler socketHandler;

    public MouseEventReceiver(Socket defSocket, SocketHandler socketHandler) {
        this.defSocket = defSocket;
        this.socketHandler = socketHandler;


        running = true;
    }

    @Override
    public void run() {
        try {
            os = defSocket.getOutputStream();

            serverSocket = new ServerSocket(0);
            int port = serverSocket.getLocalPort();

            writeRequest(os,"Port:"+port); // send free port to client

            Socket connectionSocket = serverSocket.accept(); // success

            outStream = new ObjectOutputStream(connectionSocket.getOutputStream());
            inStream = new ObjectInputStream(connectionSocket.getInputStream());

            Object obj;

            while(running){
                obj = inStream.readObject();

                if(obj != null && obj instanceof MouseEvents){
                    socketHandler.mouseAction((MouseEvents)obj);
                }


            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void writeRequest(OutputStream os, String request) {
        try {
            os.write((request + "\n").getBytes());
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
