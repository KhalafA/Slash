import javax.swing.*;

public class ApplicationFrame extends JFrame{
    private JPanel panel;

    public ApplicationFrame(JPanel panel){
        super("Slash");
        this.panel = panel;

        setup();
    }

    private void setup(){
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        add(panel);
        setLocationRelativeTo(null);
        setResizable(true);
        pack();
        setVisible(true);
    }

    public void minimize() {
        setState(this.ICONIFIED);
    }
}
