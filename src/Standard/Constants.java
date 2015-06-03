package Standard;

import java.awt.*;

public class Constants {

    /*
        Error messages
     */

    public static final String serverNotFound = "Could not locate server";
    public static final String illegalPortNumber = "Port has to be a number";  //When port is not a Number
    public static final String failedToCloseServer = "Could not close Servers";
    public static final String lostConnectionToServer = "You lost connect to the server";

    /*
        Request
     */

    public static final String startRequest = "Grab";
    public static final String pauseRequest= "Stop";
    public static final String stopRequest = "Disconnect";

    /*
        Sending
     */

    public static final String imageEncoding = "jpg";
    public static final int sleepIntervalBetweenScreenCaptures = 1000;

    /*
        GUI
     */

    public static final String mainTitle = "Slash";

    public static final String mainTab = "Info";
    public static final int mainTabID = 0;

    public static final String clientListTab = "Client's";
    public static final int clientListTabID = 1;

    //Capture Panel
    public static final int captureViewWidth = 450;
    public static final int captureViewHeight = 350;

    public static final String captureViewTitle = "Connected: ";

    //Fields
    public static final int intputFieldWidth = 200;
    public static final int inputFieldHeight = 35;

    public static final int serverSpace = 20; //extra space under the fields when its a server

    public static final String fieldLabelPostFix = ": ";

    public static final String NAME = "Name";
    public static final String IP = "IP";
    public static final String PORT = "Port";
    public static final String PASS = "Pass";

    //Client Panel
    public static final String connectBtn = "Connect to Partner";
    public static final String clientPanelTitle = "Control Remote Computer";

    //Server Panel
    public static final String readyForConnections = "Ready for Connections!";
    public static final String settingUpServer = "Setting up...";
    public static final String captureBtn = "Select Screen shot Area";
    public static final String serverPanelTitle = "Allow Remote Control";

    //Main Panel
    public static final int mainViewWidth = 450;
    public static final int mainViewHeight = 220;

    //Table panel
    public static final String CAPTURING = "Capturing";
    public static final String PAUSED = "Paused";

    public static final String idRow = "ID";
    public static final String nameRow = "Name";
    public static final String statusRow = "Status";

    public static final String kickBtn = "Kick";



    //Image manipulation
    public static final float scaleFactor = 1.3f;
    public static final float offset = 0;
    public static final RenderingHints hints = null;

    //User input timers
    public static final int oneFieldInputTimer = 1000; //saves input value 1 sec after last change.
    public static final int allFieldsInputTimer = 5000; //Start setting up server 5 secs after last change in any field.

}
