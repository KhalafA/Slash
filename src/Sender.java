import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Sender implements Runnable{
    private boolean isInterrupted;
    private OutputStream outputStream;
    private CaptureView captureView;

    private int startX;
    private int startY;
    private int endY;
    private int endX;

    public Sender(OutputStream outputStream, CaptureView captureView){
        this.outputStream = outputStream;
        this.captureView = captureView;

        isInterrupted = false;
    }

    @Override
    public void run() {
        while (!isInterrupted){
            try {
                grabScreen(outputStream);

                Thread.sleep(100);

            } catch (AWTException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                interrupt();
            } catch (IOException e) {
                interrupt();
            }
        }
    }

    public void grabScreen(OutputStream os) throws AWTException, IOException {
        Rectangle screenRect;

        if(captureView.getFullScreenStatus()){
            screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        }else {
            startX = captureView.getStartX();
            startY = captureView.getStartY();
            endX = captureView.getEndX();
            endY = captureView.getEndY();

            screenRect = new Rectangle(startX, startY, endX - startX, endY - startY);
        }

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

    public void pause() {
        isInterrupted = true;
    }

    public void resume() {
        isInterrupted = false;
    }
}