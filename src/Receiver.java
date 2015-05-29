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

    public Receiver(Socket socket, ScreenPane screenPane) {
        this.socket = socket;
        this.screenPane = screenPane;

        isInterrupted = false;
        requestChanged = true;
    }

    @Override
    public void run() {
        if (socket != null) {
            try {
                is = socket.getInputStream();
                os = socket.getOutputStream();

                while (!isInterrupted){

                    sendRequest(os, request);
                    readImage(is);
                }

                Thread.sleep(100);
            } catch (IOException exp) {
                //close
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void readImage(InputStream is) {
        try {
            String size = readResponse(is);
            int expectedByteCount = getExptectedByteCount(size);

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

            screenPane.setImage(image);
            bais.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendRequest(OutputStream os, String request) {
        if(requestChanged){
            try {
                writeRequest(os, request);
                requestChanged = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

        while ((in = is.read()) != '\n') {
            sb.append((char) in);
        }

        return sb.toString();

    }

    public void writeRequest(OutputStream os, String request) throws IOException {
        os.write((request + "\n").getBytes());
        os.flush();
    }
}