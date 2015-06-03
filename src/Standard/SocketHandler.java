package Standard;

import Auth.AuthenticatedMsg;
import Auth.AuthenticationMsg;
import GUI.Logic.CaptureLogic;
import Standard.Sender;
import Standard.Server;

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

    private int ID;

    public SocketHandler(Socket socket, String name, String pass, CaptureLogic captureLogic, Server server, int ID) {
        this.socket = socket;
        this.name = name;
        this.pass = pass;
        this.captureLogic = captureLogic;
        this.server = server;

        this.ID = ID;

        isInterrupted = false;
        verified = false;
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
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }else {
            try {
                is = socket.getInputStream();
                os = socket.getOutputStream();

                while (!isInterrupted) {
                    request = readRequest(is);

                    if ("grab".equalsIgnoreCase(request)) {
                            sender = new Sender(os, captureLogic);
                            senderThread = new Thread(sender);
                            senderThread.start();

                            server.updateClientStatus(ID, true);
                    } else if ("stop".equalsIgnoreCase(request)) {
                        sender.pause();

                        server.updateClientStatus(ID, false);
                    }
                }

            } catch (IOException exp) {
                disconnected();
            }
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
}
