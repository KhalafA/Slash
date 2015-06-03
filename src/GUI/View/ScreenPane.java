package GUI.View;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ScreenPane extends JPanel {
    private JLabel background;

    public ScreenPane() {
        setLayout(new BorderLayout());
        background = new JLabel();
        add(new JScrollPane(background));
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(200, 200);
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

}
