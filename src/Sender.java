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
        //System.out.println("Get Cursor Info");
        int x = MouseInfo.getPointerInfo().getLocation().x;
        int y = MouseInfo.getPointerInfo().getLocation().y;

        //System.out.println("Grab screen shot");
        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        BufferedImage capture = new Robot().createScreenCapture(screenRect);

        System.out.println("Get cursor image");
        Image cursor = ImageIO.read(new File("cursor.png"));

        System.out.println("Draw Cursor");
        Graphics2D graphics2D = capture.createGraphics();
        graphics2D.drawImage(cursor, x, y, 16, 16, null);

        //System.out.println("Writing image to buffer...");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        ImageIO.write(capture, "jpg", baos);
        baos.close();

        //System.out.println("Write byte size = " + baos.size());
        os.write((Integer.toString(baos.size()) + "\n").getBytes());
        //System.out.println("Write byte stream");
        os.write(baos.toByteArray());
        //System.out.println("Image sent");
    }

    public void interrupt(){
        isInterrupted = true;

        if(outputStream != null){
            try {
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            outputStream = null;
        }
    }
}
