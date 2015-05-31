import java.awt.*;

public class CaptureView{
    private int startX;
    private int startY;
    private int endY;
    private int endX;
    private boolean fullScreen;

    public CaptureView() {
        fullScreen = true;
    }

    private void stopFullScreen(){
        fullScreen = false;
    }

    public boolean getFullScreenStatus(){
        return fullScreen;
    }

    public int getStartX() {
        return startX;
    }

    public void setStartX(int startX) {
        stopFullScreen();
        this.startX = startX;
    }

    public int getStartY() {
        return startY;
    }

    public void setStartY(int startY) {
        stopFullScreen();
        this.startY = startY;
    }

    public int getEndY() {
        return endY;
    }

    public void setEndY(int endY) {
        stopFullScreen();
        this.endY = endY;
    }

    public int getEndX() {
        return endX;
    }

    public void setEndX(int endX) {
        stopFullScreen();
        this.endX = endX;
    }

    public void setStats(int x, int y, int xx, int yy) {
        setStartX(x);
        setStartY(y);
        setEndX(xx);
        setEndY(yy);
    }

    public String toString(){
        return "Capturing: x: " + startX + ", y: " + startY + ", Width: " + endX + ", Height: "+ endY;
    }
}
