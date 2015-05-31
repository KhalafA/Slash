import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import javax.swing.*;

public class Client {


    public Client(String ip, int port, String pass, String name) throws IOException {
        final JFrame viewFrame = new JFrame("Live Connection");

        final CapturePane capturePane = new CapturePane(ip, port, pass, name, viewFrame);


        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                }

                viewFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                viewFrame.add(capturePane);
                viewFrame.pack();
                viewFrame.setLocationRelativeTo(null);
                viewFrame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        //close connection
                    }
                });

                viewFrame.setVisible(true);
            }
        });
    }
}