import javax.swing.*;
import java.awt.*;

public class StartPane extends JPanel{
    private ServerPane serverPane;
    private ClientPane clientPane;
    private Application application;

    public StartPane(String ip, int port, String name, String pass, Application application){
        this.application = application;

        serverPane = new ServerPane(ip, port + "", name, pass);
        clientPane = new ClientPane(application);

        setPreferredSize(new Dimension(450,250));
        GridLayout gridLayout = new GridLayout(0,2);

        setLayout(gridLayout);

        add(serverPane);
        add(clientPane);
    }

    public void setServerStatus(boolean status){
        serverPane.setServerStatus(status);
    }

}

