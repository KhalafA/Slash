package GUI.View;

import GUI.View.SelectionCanvas;
import Standard.Application;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

public class TransparentBackground extends JComponent{
    private Image background;
    private JFrame frame;
    private JPanel imagePanel;

    private JLayeredPane lpane = new JLayeredPane();

    private Application application;

    private int startX, startY, endX, endY;
    private int currentX, currentY;

    private int squareX, squareY, squareWidth, squareHeight;

    private SelectionCanvas canvas;

    public TransparentBackground(final JFrame frame, Application application) {
        this.frame = frame;
        this.application = application;

        canvas = new SelectionCanvas();

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

                System.out.println("Pressed");
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                endX = e.getX();
                endY = e.getY();

                System.out.println("Released");

                calcCaptureSquare(startX, startY, endX, endY);
                updateCaptureView();
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });


        imagePanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);


                setCurrentX(e.getX());
                setCurrentY(e.getY());

                drawRect();
            }
        });

        frame.add(lpane);
        lpane.setBounds(0,0,background.getWidth(this), background.getHeight(this));
        imagePanel.setBounds(0,0,background.getWidth(this), background.getHeight(this));
        imagePanel.setOpaque(true);
        canvas.setBounds(0,0,background.getWidth(this),background.getHeight(this));
        canvas.setOpaque(false);

        lpane.add(imagePanel, new Integer(0), 0);
        lpane.add(canvas, new Integer(1), 0);
    }

    private void drawRect() {
        calcCaptureSquare(startX, startY, currentX, currentY);

        canvas.updateSquare(squareX, squareY, squareWidth, squareHeight);
        canvas.repaint();
    }


    private void setCurrentX(int currentX) {
        this.currentX = currentX;
    }

    private void setCurrentY(int currentY) {
        this.currentY = currentY;
    }

    private void calcCaptureSquare(int startXPos, int startYPos, int endXPos, int endYPos){
        if(startXPos > endXPos){
            squareX = endXPos;
            squareWidth = startXPos-endXPos;
        }else {
            squareX = startXPos;
            squareWidth = endXPos-startXPos;
        }

        if(startYPos > endYPos){
            squareY = endYPos;
            squareHeight = startYPos - endYPos;
        }else {
            squareY = startYPos;
            squareHeight = endYPos - startYPos;
        }
    }

    private void updateCaptureView(){
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
