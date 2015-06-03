package Auth;

import GUI.View.CapturePane;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Verification implements Runnable{
    private Socket socket;
    private String pass;
    private String name;

    private String clientName;

    private CapturePane capturePane;

    private boolean isInterrupted;

    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;

    public Verification(Socket socket, String pass, String name, CapturePane capturePane, String clientName) {
        this.socket = socket;
        this.pass = pass;
        this.name = name;
        this.clientName = clientName;
        this.capturePane = capturePane;

        isInterrupted = false;
    }

    @Override
    public void run() {
        Object obj;

        if(socket != null){
            try {
                objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                objectInputStream = new ObjectInputStream(socket.getInputStream());
                objectOutputStream.writeObject(new AuthenticationMsg(name, pass, clientName));

                while (!isInterrupted) {

                    if((obj = objectInputStream.readObject()) != null){
                        if(obj instanceof AuthenticatedMsg){
                            if(((AuthenticatedMsg) obj).isAuthenticated()){
                                capturePane.verified();
                                isInterrupted = true;
                            }
                        }
                    }

                    Thread.sleep(100);
                }


            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
