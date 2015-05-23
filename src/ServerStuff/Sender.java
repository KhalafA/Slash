package ServerStuff;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Sender implements Runnable{
    private boolean isInterrupted;
    private OutputStream outputStream;

    public Sender(OutputStream outputStream){
        this.outputStream = outputStream;
        isInterrupted = false;
    }

    @Override
    public void run() {
        while (!isInterrupted){
            try {
                grabScreen(outputStream);

                Thread.sleep(300);

            } catch (AWTException e) {
                e.printStackTrace();
            } catch (IOException e) {
                isInterrupted = true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void grabScreen(OutputStream os) throws AWTException, IOException {
        System.out.println("Grab screen shot");
        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        BufferedImage capture = new Robot().createScreenCapture(screenRect);

        System.out.println("Writing image to buffer...");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(capture, "jpg", baos);
        baos.close();

        System.out.println("Write byte size = " + baos.size());
        os.write((Integer.toString(baos.size()) + "\n").getBytes());
        System.out.println("Write byte stream");
        os.write(baos.toByteArray());
        System.out.println("Image sent");
    }
}
