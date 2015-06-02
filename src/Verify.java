import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Verify implements Runnable{
    private Socket socket;
    private String pass;
    private String name;

    private String clientName;

    private CapturePane capturePane;

    private boolean isInterrupted;

    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;

    public Verify(Socket socket, String pass, String name, CapturePane capturePane, String clientName) {
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

                System.out.println("Sending Verfication msg");
                objectOutputStream.writeObject(new Verification(name, pass, clientName));

                while (!isInterrupted) {

                    if((obj = objectInputStream.readObject()) != null){
                        if(obj instanceof Verified){
                            if(((Verified) obj).isVerified()){
                                System.out.println("Info verified");
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
