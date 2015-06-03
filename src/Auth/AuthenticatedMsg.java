package Auth;

import java.io.Serializable;

public class AuthenticatedMsg implements Serializable{
    private boolean verfied;

    public AuthenticatedMsg(boolean verified){
        this.verfied = verified;
    }

    public boolean isVerified() {
        return verfied;
    }
}
