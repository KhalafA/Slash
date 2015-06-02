import javax.swing.*;
import java.awt.*;

public class SelectionCanvas extends JComponent{
    private int x, y, width, height;

    public void paint(Graphics g) {
        System.out.println("drawing x:" + x + ", y: " + y + ", width: " + width + ", height: " + height);
        g.drawRect(x, y, width, height);
    }

    public void updateSquare(int squareX, int squareY, int squareWidth, int squareHeight) {
        x = squareX;
        y = squareY;
        width = squareWidth;
        height = squareHeight;
    }
}
