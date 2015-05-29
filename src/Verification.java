import java.io.Serializable;

public class Verification implements Serializable{
    private String name;
    private String pass;

    public Verification(String name, String pass){
        this.name = name;
        this.pass = pass;
    }

    public String getName() {
        return name;
    }

    public String getPass() {
        return pass;
    }
}

