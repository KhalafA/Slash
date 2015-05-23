package ServerStuff;

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
                }else if("done".equalsIgnoreCase(request)){
                    interrupt();
                }


            }

        } catch (IOException exp) {
            exp.printStackTrace();
        }
    }
    // Reads a request from the client
    // All requests must be terminated with a new line (\n)
    public String readRequest(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder(128);
        int in = -1;
        while ((in = is.read()) != '\n') {
            sb.append((char) in);
        }
        return sb.toString();
    }

    public void interrupt() throws IOException {
        isInterrupted = true;
        socket.close();
    }
}
