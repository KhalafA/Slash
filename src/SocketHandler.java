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

    private CaptureView captureView;
    private Server server;

    private int ID;

    public SocketHandler(Socket socket, String name, String pass, CaptureView captureView, Server server, int ID) {
        this.socket = socket;
        this.name = name;
        this.pass = pass;
        this.captureView = captureView;
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
                        if(obj instanceof Verification){
                            if(((Verification) obj).getServerName().equals(name) && ((Verification) obj).getServerPass().equals(pass)){
                                objectOutputStream.writeObject(new Verified(true));
                                server.clientInformation((Verification) obj, ID);
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

                    System.out.println("raeding!");

                    if ("grab".equalsIgnoreCase(request)) {
                            sender = new Sender(os, captureView);
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
                System.out.println("no connection");
                done = true;
                disconnected();
            }
        }

        return sb.toString();
    }

    private void disconnected(){
        System.out.println("Connection closed");
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
