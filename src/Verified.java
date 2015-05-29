import java.io.Serializable;

public class Verified implements Serializable{
    private boolean verfied;

    public Verified(boolean verified){
        this.verfied = verified;
    }

    public boolean isVerified() {
        return verfied;
    }
}
