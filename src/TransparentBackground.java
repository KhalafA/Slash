import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

public class TransparentBackground extends JComponent{
    private Image background;
    private JFrame frame;
    private JPanel imagePanel;

    private Application application;

    private int startX, startY, endX, endY;

    public TransparentBackground(final JFrame frame, Application application) {
        this.frame = frame;
        this.application = application;

        updateBackground();

        imagePanel = new ImagePanel(background);

        imagePanel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                startX = e.getX();
                startY = e.getY();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                endX = e.getX();
                endY = e.getY();

                calcCaptureSquare();
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        frame.add(imagePanel);
    }

    private void calcCaptureSquare(){
        int squareX = 0;
        int squareY = 0;

        int squareWidth = 0;
        int squareHeight = 0;

        if(startX > endX){
            squareX = endX;
            squareWidth = startX-endX;
        }else {
            squareX = startX;
            squareWidth = endX-startX;
        }

        if(startY > endY){
            squareY = endY;
            squareHeight = startY - endY;
        }else {
            squareY = startY;
            squareHeight = endY - startY;
        }



        application.setCaptureView(squareX, squareY, squareWidth, squareHeight);
        System.out.println("Capturing: x: " + squareX + ", y: " + squareY + ", Width: " + squareWidth + ", Height: "+ squareHeight);
        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
    }

    public void updateBackground( ) {
        try {
            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            BufferedImage capture = new Robot().createScreenCapture(screenRect);

            RescaleOp op = new RescaleOp(1.3f, 0, null);
            capture = op.filter(capture, capture);

            background = capture;

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private class ImagePanel extends JPanel {
        private Image img;

        public ImagePanel(Image img) {
            this.img = img;
            Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
            setPreferredSize(size);
            setMinimumSize(size);
            setMaximumSize(size);
            setSize(size);
            setLayout(null);
        }

        public void paintComponent(Graphics g) {
            g.drawImage(img, 0, 0, null);
        }
    }
}
