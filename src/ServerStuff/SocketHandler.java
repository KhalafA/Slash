package ServerStuff;

import com.sun.org.apache.xpath.internal.SourceTree;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SocketHandler implements Runnable{
    private boolean isInterrupted;

    private Socket socket;
    private String request;
    private InputStream is;
    private OutputStream os;

    Sender sender;
    Thread senderThread;

    public SocketHandler(Socket socket) {
        this.socket = socket;
        isInterrupted = false;
    }

    @Override
    public void run() {
        try {
            System.out.println("Processing client requests");
            is = socket.getInputStream();
            os = socket.getOutputStream();

            while (!isInterrupted){
                request = readRequest(is);

                if ("grab".equalsIgnoreCase(request)) {
                    sender = new Sender(os);
                    senderThread = new Thread(sender);
                    senderThread.start();
                }
            }
            close();
        } catch (IOException exp) {
            close();
        }
    }

    public String readRequest(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder(128);
        int in = -1;

        try{
            while ((in = is.read()) != '\n') {
                sb.append((char) in);
            }
        }catch (Error e){
            //Hack, find better way to do this
            close();
        }

        return sb.toString();
    }

    //TODO: Do not close when client stops the stream
    //TODO: Keep sending to other clients.
    public void close() {
        System.out.println("Closing");
        isInterrupted = true;

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if (os != null) {
                os.flush();
                os.close();
            }
            if (is != null) {
                is.close();
            }
        } catch (IOException e) {
            //Do nothing
        }

        System.exit(0);
    }
}
