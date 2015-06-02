public class CaptureView{
    private int squareX;
    private int squareY;
    private int squareWidth;
    private int squareHeight;
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

    public int getSquareX() {
        return squareX;
    }

    public void setSquareX(int squareX) {
        stopFullScreen();
        this.squareX = squareX;
    }

    public int getSquareY() {
        return squareY;
    }

    public void setSquareY(int squareY) {
        stopFullScreen();
        this.squareY = squareY;
    }

    public int getSquareWidth() {
        return squareWidth;
    }

    public void setSquareWidth(int squareWidth) {
        stopFullScreen();
        this.squareWidth = squareWidth;
    }

    public int getSquareHeight() {
        return squareHeight;
    }

    public void setSquareHeight(int squareHeight) {
        stopFullScreen();
        this.squareHeight = squareHeight;
    }

    public void setStats(int x, int y, int xx, int yy) {
        setSquareX(x);
        setSquareY(y);
        setSquareHeight(xx);
        setSquareWidth(yy);
    }

    public String toString(){
        return "Capturing: x: " + squareX + ", y: " + squareY + ", Width: " + squareHeight + ", Height: "+ squareWidth;
    }
}
