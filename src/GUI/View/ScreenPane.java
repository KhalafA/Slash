package GUI.View;

import Standard.MouseEventSender;
import Standard.MouseEvents;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class ScreenPane extends JPanel {
    private JLabel background;
    private Timer movedTimer;
    private Timer dragTimer;

    private MouseEventSender mouseEventSender;

    private boolean isControlling;

    public ScreenPane() {
        setLayout(new BorderLayout());
        background = new JLabel();
        add(new JScrollPane(background));

        isControlling = false;

        background.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(isControlling){
                    buildEvent("Clicked", e.getX(), e.getY());
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if(isControlling){
                    buildEvent("Pressed", e.getX(), e.getY());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if(isControlling){
                    buildEvent("Released", e.getX(), e.getY());
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        background.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if(isControlling){
                    super.mouseDragged(e);
                    mouseDraggedTimer(e);
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                if(isControlling){
                    super.mouseMoved(e);
                    mouseMovedTimer(e);
                }
            }
        });
    }

    private void buildEvent(String action, int x, int y){
        float[] percent = calc(x,y);

        System.out.println(action);
        mouseEventSender.newEvent(new MouseEvents(action, percent[0], percent[1])); // send event
    }


    //calc the percentage
    private float[] calc(int x, int y){
        float[] screenPosInPercent = new float[2];

        double mouseX = x;
        double mouseY = y;

        double imageHeight = background.getHeight();
        double imageWidth = background.getWidth();

        screenPosInPercent[0] = (float) ((mouseX*100)/imageWidth);
        screenPosInPercent[1] = (float) ((mouseY*100)/imageHeight);

        return screenPosInPercent;
    }

    private void mouseDraggedTimer(final MouseEvent e){
        setDragCord(e);

        if(dragTimer != null){
            if(dragTimer.isRunning()){
                dragTimer.restart();
            }else {
                dragTimer.start();
            }
        }else {
            dragTimer = new Timer(100, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    buildEvent("Dragged", mouseDragX, mouseDragY);
                    dragTimer.stop();
                }
            });
            dragTimer.start();
        }
    }

    private void mouseMovedTimer(MouseEvent e){
        setMoveCord(e);

        if(movedTimer != null){
            if(movedTimer.isRunning()){
                movedTimer.restart();
            }else {
                movedTimer.start();
            }
        }else {
            movedTimer = new Timer(100, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    buildEvent("Moved", mouseMoveX, mouseMoveY);
                    movedTimer.stop();
                }
            });
            movedTimer.start();
        }
    }

    //Dumb hack -- called by the two timers. (Figure a way to combine the two methods)
    private int mouseMoveX;
    private int mouseMoveY;

    private void setMoveCord(MouseEvent e){
        mouseMoveX = e.getX();
        mouseMoveY = e.getY();
    }

    private int mouseDragX;
    private int mouseDragY;

    private void setDragCord(MouseEvent e){
        mouseDragX = e.getX();
        mouseDragY = e.getY();
    }


    public void setImage(BufferedImage img) {
        if (img != null) {
            ImageIcon icon = null;
            if (getWidth() > getHeight()) {
                icon = new ImageIcon(img.getScaledInstance(getWidth(), -1, Image.SCALE_SMOOTH));
            } else {
                icon = new ImageIcon(img.getScaledInstance(-1, getHeight(), Image.SCALE_SMOOTH));
            }
            background.setIcon(icon);
        } else {
            background.setIcon(null);
        }
        repaint();
    }

    public void setControlling(boolean isControlling) {
        this.isControlling = isControlling;
    }

    public void setMouseEventSender(MouseEventSender mouseEventSender){
        this.mouseEventSender = mouseEventSender;

        isControlling = true;
    }

}
