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

    private int startX, startY, endX, endY;

    public TransparentBackground(final JFrame frame, final Application application) {
        this.frame = frame;

        updateBackground();

        imagePanel = new ImagePanel(background);

        imagePanel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("Cicked: " + e.getLocationOnScreen());
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

                application.setCaptureView(startX, startY, endX, endY);
                System.out.println("Capturing: x: " + startX + ", y: " + startY + ", Width: " + endX + ", Height: "+ endY + "££££");
                frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
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
