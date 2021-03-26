package ATMSS.TouchDisplayHandler.Emulator;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;

import java.util.logging.Logger;


//======================================================================
// TouchDisplayEmulatorController
public class TouchDisplayEmulatorController {
    private String id;
    private AppKickstarter appKickstarter;
    private Logger log;
    private TouchDisplayEmulator touchDisplayEmulator;
    private MBox touchDisplayMBox;
    public Label blankScreenLabel;
    public Label confirmationLabel;
    public Rectangle noBtn;
    public Rectangle yesBtn;
    public PasswordField passwordField;

    private boolean loggedIn = false;


    //------------------------------------------------------------
    // initialize
    public void initialize(String id, AppKickstarter appKickstarter, Logger log, TouchDisplayEmulator touchDisplayEmulator) {
        this.id = id;
        this.appKickstarter = appKickstarter;
        this.log = log;
        this.touchDisplayEmulator = touchDisplayEmulator;
        this.touchDisplayMBox = appKickstarter.getThread("TouchDisplayHandler").getMBox();
    } // initialize


    //------------------------------------------------------------
    // td_mouseClick
    public void td_mouseClick(MouseEvent mouseEvent) {
        int x = (int) mouseEvent.getX();
        int y = (int) mouseEvent.getY();

        log.fine(id + ": mouse clicked: -- (" + x + ", " + y + ")");
        //touchDisplayMBox.send(new Msg(id, touchDisplayMBox, Msg.Type.TD_MouseClicked, x + " " + y));
        if (loggedIn == true) {
            //use Java Switch to send diff message types depending on x-y coord
        } else {
            touchDisplayMBox.send(new Msg(id, touchDisplayMBox, Msg.Type.TD_MouseClicked, x + " " + y));
        }
    } // td_mouseClick

    //may be replaced, it is stupid to send msg twice
    public void setLoginTrue() {
        loggedIn = true;
        touchDisplayMBox.send(new Msg(id, touchDisplayMBox, Msg.Type.TD_UpdateDisplay, "MainMenu"));
    }

    public void setLoginFalse() {
        loggedIn = false;
        //touchDisplayMBox.send(new Msg(id, touchDisplayMBox, Msg.Type.TD_UpdateDisplay, "BlankScreen"));
    }

    public void welcomePage() {
        blankScreenLabel.setText("Welcome to ATM system emulator\n\n\n\n\nPlease Insert ATM Card");
    }

    public void enterPINPage() {
        log.warning(id + ": At this moment, the program will give a lot of errors or no respond after sending the PIN for validation");
        blankScreenLabel.setText("Please Enter the PIN: \n\nPlease Press Enter Button after Entering PIN\n\nPlease Press Erase Button If You Type Wrong\n\nPlease Press Cancel If You Want to Cancel Transaction\n\n");
        passwordField.setVisible(true);
    }

    public void erasePIN(){
        passwordField.setText("");
    }

    public void changePIN() {
        //it is not a matter what text it is going to append on the touchscreen
        //the pin will be all masked
        if (passwordField.getText().length() < 9) {
            passwordField.appendText("0");
        }
    }
} // TouchDisplayEmulatorController
