import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import javax.swing.*;

public class Client {


    public Client(String ip, int port, String pass, String name) throws IOException {

        final CapturePane capturePane = new CapturePane(ip, port, pass, name);
        final JFrame frame = new JFrame("Live Connection");

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                }

                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
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