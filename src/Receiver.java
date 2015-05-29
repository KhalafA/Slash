import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;

public class Receiver implements Runnable {
    private final ScreenPane screenPane;
    private boolean isInterrupted;
    private InputStream is;
    private OutputStream os;

    private ByteArrayOutputStream baos;
    private ByteArrayInputStream bais;

    private Socket socket;
    private String request;

    public Receiver(Socket socket, ScreenPane screenPane, String request) {
        this.socket = socket;
        this.screenPane = screenPane;
        this.request = request;

        isInterrupted = false;
    }

    @Override
    public void run() {
        if (socket != null) {
            try {
                is = socket.getInputStream();
                os = socket.getOutputStream();

                writeRequest(os, request);

                while (!isInterrupted){
                    System.out.println("Reading image...");
                    String size = readResponse(is);

                    int expectedByteCount = 0;

                    try{
                        expectedByteCount = Integer.parseInt(size);
                    }catch (NumberFormatException e){
                        break;
                    }

                    System.out.println("Expecting " + expectedByteCount);
                    baos = new ByteArrayOutputStream(expectedByteCount);
                    byte[] buffer = new byte[1024];
                    int bytesRead = 0;
                    int bytesIn = 0;
                    while (bytesRead < expectedByteCount) {
                        bytesIn = is.read(buffer);
                        bytesRead += bytesIn;
                        baos.write(buffer, 0, bytesIn);
                    }

                    System.out.println("Read " + bytesRead);
                    baos.close();

                    bais = new ByteArrayInputStream(baos.toByteArray());

                    BufferedImage image = ImageIO.read(bais);
                    System.out.println("Got image...");
                    screenPane.setImage(image);
                    bais.close();
                }


            } catch (IOException exp) {
                //close
            }
        }
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

    public void interrupt(){
        try {
            writeRequest(os, "stop");
        } catch (IOException e) {
            e.printStackTrace();
        }

        isInterrupted = true;

        if(is != null){
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            is = null;
        }

        if(os != null){
            try {
                os.flush();
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            os = null;
        }
    }
}
