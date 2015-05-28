package Client;

import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Client {

    public static void main(String[] args) {
        new Client();
    }

    public Client() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                }

                final CapturePane capturePane = new CapturePane();

                JFrame frame = new JFrame("Live Connection");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.add(capturePane);
                frame.pack();
                frame.setLocationRelativeTo(null);
                  frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        //close connection

                    }
                });
                frame.setVisible(true);
            }
        });
    }
}