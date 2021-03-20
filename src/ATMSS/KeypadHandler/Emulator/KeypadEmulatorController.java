package ATMSS.KeypadHandler.Emulator;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;


//======================================================================
// KeypadEmulatorController
public class KeypadEmulatorController {
    private String id;
    private AppKickstarter appKickstarter;
    private Logger log;
    private KeypadEmulator keypadEmulator;
    private MBox keypadMBox;


    //------------------------------------------------------------
    // initialize
    public void initialize(String id, AppKickstarter appKickstarter, Logger log, KeypadEmulator keypadEmulator) {
        this.id = id;
	this.appKickstarter = appKickstarter;
	this.log = log;
	this.keypadEmulator = keypadEmulator;
	this.keypadMBox = appKickstarter.getThread("KeypadHandler").getMBox();
    } // initialize


    //------------------------------------------------------------
    // buttonPressed
    public void buttonPressed(ActionEvent actionEvent) {
	Button btn = (Button) actionEvent.getSource();
	String btnTxt = btn.getText();
	keypadMBox.send(new Msg(id, keypadMBox, Msg.Type.KP_KeyPressed, btnTxt));
    } // buttonPressed


    //------------------------------------------------------------
    // keyPressed
    public void keyPressed(KeyEvent keyEvent) {
	String keyCodeStr = keyEvent.getCode().toString();

	if (keyCodeStr.startsWith("DIGIT") || keyCodeStr.startsWith("NUMPAD")) {
	    char inputChar = keyCodeStr.charAt(keyCodeStr.length() - 1);
	    keypadMBox.send(new Msg(id, keypadMBox, Msg.Type.KP_KeyPressed, "" + inputChar));
	} else if (keyCodeStr.compareTo("DECIMAL") == 0) {
	    keypadMBox.send(new Msg(id, keypadMBox, Msg.Type.KP_KeyPressed, "."));
	} else if (keyCodeStr.compareTo("ENTER") == 0) {
	    keypadMBox.send(new Msg(id, keypadMBox, Msg.Type.KP_KeyPressed, "ENTER"));
	} else if (keyCodeStr.compareTo("ESCAPE") == 0) {
	    keypadMBox.send(new Msg(id, keypadMBox, Msg.Type.KP_KeyPressed, "CANCEL"));
	} else if (keyCodeStr.compareTo("BACK_SPACE") == 0) {
	    keypadMBox.send(new Msg(id, keypadMBox, Msg.Type.KP_KeyPressed, "ERASE"));
	} else {
	    log.finer(id + ": Key Pressed " + keyCodeStr);
	}
    } // keyPressed
} // KeypadEmulatorController
