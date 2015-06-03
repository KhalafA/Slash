package GUI.View;

import javax.swing.*;
import java.awt.*;

public class SelectionCanvas extends JComponent{
    private int x, y, width, height;

    public void paint(Graphics g) {
        g.drawRect(x, y, width, height);
    }

    public void updateSquare(int squareX, int squareY, int squareWidth, int squareHeight) {
        x = squareX;
        y = squareY;
        width = squareWidth;
        height = squareHeight;
    }
}
