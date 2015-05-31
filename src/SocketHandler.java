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

    public SocketHandler(Socket socket, String name, String pass, CaptureView captureView) {
        this.socket = socket;
        this.name = name;
        this.pass = pass;
        this.captureView = captureView;


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
                            if(((Verification) obj).getName().equals(name) && ((Verification) obj).getPass().equals(pass)){
                                objectOutputStream.writeObject(new Verified(true));
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

                            System.out.println("start " + isInterrupted);
                            sender = new Sender(os, captureView);
                            senderThread = new Thread(sender);
                            senderThread.start();
                            System.out.println("start " + isInterrupted);

                    } else if ("stop".equalsIgnoreCase(request)) {
                        sender.pause();
                    }
                }

                Thread.sleep(100);
            } catch (IOException exp) {
                //close
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public String readRequest(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder(560);
        int in = -1;

        while ((in = is.read()) != '\n') {
            sb.append((char) in);
        }

        return sb.toString();
    }
}
