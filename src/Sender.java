import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
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

                Thread.sleep(1000);

            } catch (AWTException e) {
                e.printStackTrace();
            } catch (IOException e) {
                interrupt();
            } catch (InterruptedException e) {
                interrupt();
            }
        }
    }

    public void grabScreen(OutputStream os) throws AWTException, IOException {
        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        BufferedImage capture = new Robot().createScreenCapture(screenRect);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        ImageIO.write(capture, "jpg", baos);
        baos.close();

        os.write((Integer.toString(baos.size()) + "\n").getBytes());
        os.write(baos.toByteArray());
    }

    public void interrupt(){
        isInterrupted = true;
    }
}