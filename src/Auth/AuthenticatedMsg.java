package Auth;

import java.io.Serializable;

public class AuthenticatedMsg implements Serializable{
    private boolean authenticated;

    public AuthenticatedMsg(boolean authenticated){
        this.authenticated = authenticated;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }
}
