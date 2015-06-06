package Standard;

import GUI.Logic.CaptureLogic;

import java.awt.*;
import java.awt.event.InputEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.LinkedBlockingQueue;

public class MouseMover implements Runnable{
    private LinkedBlockingQueue<MouseEvents> list;
    private boolean running;

    private Robot robot;

    private double screenWidth;
    private double screenHeight;

    private int deltaX = 0, deltaY = 0; //Where the server is capturing

    private CaptureLogic captureLogic;

    public MouseMover(CaptureLogic captureLogic){
        this.captureLogic = captureLogic;

        list = new LinkedBlockingQueue<>();

        running = true;

        try {
            robot = new Robot();


            if(!isSet()){
                screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
                screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
            }

        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    private boolean isSet(){
        if(!captureLogic.getFullScreenStatus()){
            deltaX = captureLogic.getSquareX();
            deltaY = captureLogic.getSquareY();

            screenWidth = captureLogic.getSquareWidth();
            screenHeight = captureLogic.getSquareHeight();
            return true;
        }
        return false;
    }


    public void newEvent(MouseEvents mouseEvents) {
        try {
            list.put(mouseEvents);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private double[] calcMouseLocation(MouseEvents mouseEvents) {
        double[] pos = new double[2];

        isSet();

        pos[0] = ((mouseEvents.getWidthPos() * screenWidth)/100 ) + deltaX;
        pos[1] = ((mouseEvents.getHeightPos() * screenHeight)/100) + deltaY;

        return pos;
    }

    private void executeEvent(MouseEvents e){
        //System.out.println("Executing action");
        MouseEvents mouseEvents = e;

        double[] pos = calcMouseLocation(mouseEvents);

        callEventMethod(e.getAction(), pos[0], pos[1]);
    }

    protected void callEventMethod(String action, double x, double y){
        Method method = null;
        try {
            method = getClass().getDeclaredMethod("mouse"+action, double.class, double.class);
            method.invoke(this, x,y);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (running) {
                if (list.size() >= 1) {
                    executeEvent(list.take());
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void mouseClicked(double x, double y){
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
    }

    public void mousePressed(double x, double y) {
        robot.mousePress(InputEvent.BUTTON1_MASK);
    }

    public void mouseReleased(double x, double y) {
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
    }

    public void mouseDragged(double x, double y) {
        robot.mouseMove((int)x,(int)y);
    }

    public void mouseMoved(double x, double y) {
        robot.mouseMove((int)x,(int)y);
    }

}
