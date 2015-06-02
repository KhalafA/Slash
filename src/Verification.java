import java.io.Serializable;

public class Verification implements Serializable{
    private String serverName;
    private String serverPass;

    private String clientName;

    public Verification(String serverName, String serverPass, String clientName){
        this.serverName = serverName;
        this.serverPass = serverPass;
        this.clientName = clientName;
    }

    public String getServerName() {
        return serverName;
    }

    public String getServerPass() {
        return serverPass;
    }

    public String getClientName() {
        return clientName;
    }
}

