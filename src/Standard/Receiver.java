package Standard;

import GUI.View.ScreenPane;
import Standard.Application;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;

public class Receiver implements Runnable {
    private final ScreenPane screenPane;
    private boolean isInterrupted;
    private InputStream is;
    private OutputStream os;

    private Socket socket;
    private String request;

    private boolean requestChanged;

    private Application application;

    private boolean disconnected;

    public Receiver(Socket socket, ScreenPane screenPane, String request, Application application) {
        this.socket = socket;
        this.screenPane = screenPane;
        this.request = request;
        this.application = application;

        isInterrupted = false;
        requestChanged = true;
        disconnected = false;
    }

    @Override
    public void run() {
        if (socket != null) {
            try {
                is = socket.getInputStream();
                os = socket.getOutputStream();

                while (!isInterrupted){

                    sendRequest(os, request);

                    if(!request.equals("stop") && !disconnected){
                        readImage(is);
                    }else{
                        isInterrupted = true;
                        break;
                    }
                 }

            } catch (IOException exp) {
                disconnected();
            }
        }
    }

    private void readImage(InputStream is) throws IOException {

            String size = readResponse(is);
            int expectedByteCount = getExptectedByteCount(size);
            if(!disconnected) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream(expectedByteCount);
                byte[] buffer = new byte[1024];
                int bytesRead = 0;
                int bytesIn = 0;
                while (bytesRead < expectedByteCount) {
                    bytesIn = is.read(buffer);
                    bytesRead += bytesIn;
                    baos.write(buffer, 0, bytesIn);
                }

                baos.close();

                ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());

                BufferedImage image = ImageIO.read(bais);

                bais.close();
                screenPane.setImage(image);
            }
    }

    private void sendRequest(OutputStream os, String request) {
        if(requestChanged){

            writeRequest(os, request);
            requestChanged = false;

        }
    }

    public boolean getRequestStatus(){
        return requestChanged;
    }

    private int getExptectedByteCount(String size) {
        int expectedByteCount = -1;

        try{
            expectedByteCount = Integer.parseInt(size);
        }catch (NumberFormatException e){

        }

        return expectedByteCount;
    }

    public void setRequest(String r){
        request = r;
        requestChanged = true;
    }

    private String readResponse(InputStream is) throws IOException {
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
        System.out.println("Connection closed");
        disconnected = true;
        isInterrupted = true;
        application.iGotKicked();
        closeConnection();
    }

    private void closeConnection(){
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Thread.currentThread().interrupt();
    }

    public void writeRequest(OutputStream os, String request) {
        try {
            os.write((request + "\n").getBytes());
            os.flush();
        } catch (IOException e) {
           disconnected();
        }
    }
}