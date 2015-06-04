package Standard;

import java.awt.*;
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

            screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
            screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;

        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public void newEvent(MouseEvents mouseEvents) {
        System.out.println("adding action to list");
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
        System.out.println("Executing action");
        MouseEvents mouseEvents = e;

        double[] pos = calcMouseLocation(mouseEvents);

        /*
            Todo: now reciving mouse inputs from the clients, next step is to move the mouse with those inputs.
            Also, alot client to deciede weather someone should have control, and never let two people at once.
         */

        System.out.println("Do: " + mouseEvents.getAction() + ", At x: "+ pos[0] + ", y: " + pos[1]);
    }

    @Override
    public void run() {
        System.out.println("Started thread");
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
}
