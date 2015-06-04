package Standard;

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

    public MouseMover(){
        list = new LinkedBlockingQueue<>();

        running = true;

        try {
            robot = new Robot();
            robot.setAutoDelay(1000);

            screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
            screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;

        } catch (AWTException e) {
            e.printStackTrace();
        }
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


        pos[0] = (mouseEvents.getWidthPos() * screenWidth)/100;
        pos[1] = (mouseEvents.getHeightPos() * screenHeight)/100;

        return pos;
    }

    private void executeEvent(MouseEvents e){
        //System.out.println("Executing action");
        MouseEvents mouseEvents = e;

        double[] pos = calcMouseLocation(mouseEvents);

        callEventMethod(e.getAction(), pos[0], pos[1]);
        /*
            Todo: now reciving mouse inputs from the clients, next step is to move the mouse with those inputs.
            Also, alot client to deciede weather someone should have control, and never let two people at once.
         */

        //System.out.println("Do: " + mouseEvents.getAction() + ", At x: "+ pos[0] + ", y: " + pos[1]);
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


}
