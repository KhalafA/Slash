import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Client {
    private final CapturePane capturePane;
    private int port;
    private int ip;

    private final JFrame frame;

    public Client(String ip, int port, String pass, String name) {
        capturePane = new CapturePane(ip, port, pass, name);
        frame = new JFrame("Live Connection");

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                }

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

    public JFrame getFrame(){
        return frame;
    }
}