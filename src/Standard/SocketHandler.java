package Standard;

import Auth.AuthenticatedMsg;
import Auth.AuthenticationMsg;
import GUI.Logic.CaptureLogic;

import java.io.*;
import java.net.Socket;

public class SocketHandler implements Runnable{
    private boolean isInterrupted;

    private Socket socket;
    private String request;
    private InputStream is;
    private OutputStream os;

    private Sender sender;
    private Thread senderThread;

    private String pass;
    private String name;

    boolean verified;

    private CaptureLogic captureLogic;
    private Server server;

    private MouseEventReceiver eventReciver;
    private Thread mouseEventThread;

    private MouseMover mouseMover;
    private Thread moverThread;

    private int ID;

    private boolean control;

    public SocketHandler(Socket socket, String name, String pass, CaptureLogic captureLogic, Server server, int ID) {
        this.socket = socket;
        this.name = name;
        this.pass = pass;
        this.captureLogic = captureLogic;
        this.server = server;

        this.ID = ID;

        isInterrupted = false;
        verified = false;

        control = false;
    }

    @Override
    public void run() {
        Object obj;
        if(!verified){
            try {
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

                while (!verified){
                    if((obj = objectInputStream.readObject()) != null){
                        if(obj instanceof AuthenticationMsg){
                            if(((AuthenticationMsg) obj).getServerName().equals(name) && ((AuthenticationMsg) obj).getServerPass().equals(pass)){
                                objectOutputStream.writeObject(new AuthenticatedMsg(true));
                                server.clientInformation((AuthenticationMsg) obj, ID);
                                verified = true;
                                break;
                            }
                        }
                    }
                }

                run();

            } catch (IOException e) {
                disconnected();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }else {
            try {
                is = socket.getInputStream();
                os = socket.getOutputStream();

                while (!isInterrupted) {
                    request = readRequest(is);

                    if (Constants.startRequest.equalsIgnoreCase(request)) {
                        System.out.println("Recived Start");
                        sender = new Sender(os, captureLogic);
                        senderThread = new Thread(sender);
                        senderThread.start();

                        server.updateClientStatus(ID, true);
                    } else if (Constants.pauseRequest.equalsIgnoreCase(request)) {
                        System.out.println("Recived Pause");
                        sender.pause();

                        server.updateClientStatus(ID, false);
                    } else if ("Control".equalsIgnoreCase(request)){

                        boolean temp = !control;
                        control = temp;

                        server.requestControl(ID, control);

                    }

                    //Disconnected client is caught like any disconnect.
                }

            } catch (IOException exp) {
                disconnected();
            }
        }
    }

    public void controlRequestGranted(boolean state){

        if(state){
            writeRequest(os, "Request Granted");

            eventReciver = new MouseEventReceiver(socket, this);
            mouseEventThread = new Thread(eventReciver);
            mouseEventThread.start();

            mouseMover = new MouseMover(captureLogic);
            moverThread = new Thread(mouseMover);
            moverThread.start();
        }else {
            writeRequest(os, "Request Declined");
        }
    }

    public Socket getSocket(){
        return socket;
    }


    public String readRequest(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder(128);
        int in = -1;

        boolean done = false;

        while (!done) {
            if((in = is.read()) != -1){
                if(in == '\n'){
                    done = true;
                }else {
                    sb.append((char) in);
                }
            }else {
                done = true;
                disconnected();
            }
        }

        return sb.toString();
    }

    private void disconnected(){
        isInterrupted = true;
        server.clientDisconnected(ID);
        closeConnection();
    }

    public void closeConnection(){
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Thread.currentThread().interrupt();
    }

    public void mouseAction(MouseEvents mouseEvents) {
        //Got new Event
        mouseMover.newEvent(mouseEvents);
    }

    public void writeRequest(OutputStream os, String request) {
        try {
            os.write((request + "\n").getBytes());
            os.flush();
        } catch (IOException e) {
            e.printStackTrace(); //connection is closed
        }
    }
}
