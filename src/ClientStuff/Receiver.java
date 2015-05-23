package ClientStuff;

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

    public Receiver(Socket socket, ScreenPane screenPane) {
        this.socket = socket;
        this.screenPane = screenPane;

        isInterrupted = false;
    }

    @Override
    public void run() {
        if (socket != null) {

            try {
                is = socket.getInputStream();
                os = socket.getOutputStream();

                writeRequest(os, "grab");

                while (!isInterrupted){
                    System.out.println("Reading image...");
                    String size = readResponse(is);

                    int expectedByteCount = Integer.parseInt(size);
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
                exp.printStackTrace();
            }
        }
    }

    protected String readResponse(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder(128);
        int in = -1;
        while ((in = is.read()) != '\n') {
            sb.append((char) in);
        }
        return sb.toString();
    }

    protected void writeRequest(OutputStream os, String request) throws IOException {
        os.write((request + "\n").getBytes());
        os.flush();
    }

    public void close() throws IOException {
        try {
            try {
                System.out.println("Write done...");
                writeRequest(socket.getOutputStream(), "shutdown");
            } finally {
                try {
                    System.out.println("Close outputstream");
                    socket.getOutputStream().close();
                } finally {
                    try {
                        System.out.println("Close inputStream");
                        socket.getInputStream().close();
                    } finally {
                        System.out.println("Close socket");
                        socket.close();
                    }
                }
            }
        } finally {
            socket = null;
        }
    }
}
