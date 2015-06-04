package Standard;


import java.io.Serializable;

public class MouseEvents implements Serializable{
    private String action;
    private float widthPos;
    private float heightPos;


    public MouseEvents(String action, float x, float y) {
        this.action = action;
        this.widthPos = x;
        this.heightPos = y;
    }

    public String getAction() {
        return action;
    }

    public float getWidthPos() {
        return widthPos;
    }

    public float getHeightPos() {
        return heightPos;
    }
}