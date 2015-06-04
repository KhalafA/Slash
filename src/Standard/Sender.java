package Standard;

import GUI.Logic.CaptureLogic;

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
    private CaptureLogic captureLogic;

    private int squareX;
    private int squareY;
    private int squareWidth;
    private int squareHeight;

    public Sender(OutputStream outputStream, CaptureLogic captureLogic){
        this.outputStream = outputStream;
        this.captureLogic = captureLogic;

        isInterrupted = false;
    }

    @Override
    public void run() {
        while (!isInterrupted){
            try {
                grabScreen(outputStream);

                Thread.sleep(Constants.sleepIntervalBetweenScreenCaptures);

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

        if(captureLogic.getFullScreenStatus()){
            screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        }else {
            squareX = captureLogic.getSquareX();
            squareY = captureLogic.getSquareY();
            squareHeight = captureLogic.getSquareHeight();
            squareWidth = captureLogic.getSquareWidth();

            screenRect = new Rectangle(squareX, squareY, squareHeight, squareWidth);
        }

        BufferedImage capture = new Robot().createScreenCapture(screenRect);

        Image cursor = ImageIO.read(new File("cursor.png"));
        int x = MouseInfo.getPointerInfo().getLocation().x;
        int y = MouseInfo.getPointerInfo().getLocation().y;

        Graphics2D graphics2D = capture.createGraphics();
        graphics2D.drawImage(cursor, x, y, 16, 16, null); // cursor.gif is 16x16 size.

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        ImageIO.write(capture, Constants.imageEncoding, baos);
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
}