package ATMSS.AdvicePrinterHandler.Emulator;

import ATMSS.AdvicePrinterHandler.Emulator.AdvicePrinterEmulator;
import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class AdvicePrinterEmulatorController {
	
	private String id;
    private AppKickstarter appKickstarter;
    private Logger log;
    private AdvicePrinterEmulator AdvicePrinterEmulator;
    private MBox AdvicePrinterMBox;
    private String Advice = "";

    public TextArea AdvicePrinterTextArea;
    
    public void initialize(String id, AppKickstarter appKickstarter, Logger log, AdvicePrinterEmulator AdvicePrinterEmulator) {
        this.id = id;
        this.appKickstarter = appKickstarter;
        this.log = log;
        this.AdvicePrinterEmulator = AdvicePrinterEmulator;
        this.AdvicePrinterMBox = appKickstarter.getThread("AdvicePrinterHandler").getMBox();
    } // initialize
    
    public void buttonPressed(ActionEvent actionEvent) {
    	Button btn = (Button) actionEvent.getSource();

    	switch (btn.getText()) {
	    	 case "Take Advice":
	    		 AdvicePrinterTextArea.setText("");
	 	    break;
//    	    case "Insert Card":
//    		if (cardNumField.getText().length() != 0) {
//    		    DepositSlotMBox.send(new Msg(id, DepositSlotMBox, Msg.Type.CR_CardInserted, cardNumField.getText()));
//    		    cardReaderTextArea.appendText("Sending " + cardNumField.getText()+"\n");
//    		    cardStatusField.setText("Card Inserted");
//    		}
//    		break;

//    	    case "Remove Card":
//    	        if (cardStatusField.getText().compareTo("Card Ejected") == 0) {
//    		    cardReaderTextArea.appendText("Removing card\n");
//    		    DepositSlotMBox.send(new Msg(id, DepositSlotMBox, Msg.Type.CR_CardRemoved, cardNumField.getText()));
//    		}
//    		break;

    	    default:
    	        log.warning(id + ": unknown button: [" + btn.getText() + "]");
    		break;
    	}
    } // buttonPressed
    
    public void storeAdvice(String advice) {
    	if (Advice.equals("")) {
    		Advice = advice+"\n";
    	}else {
    		Advice = Advice+" "+advice+"\n";
    	}
    }
    
    public void clearAdvice() {
    	Advice = "";
    }
    
    //------------------------------------------------------------
    // appendTextArea
    public void appendTextArea(String status) {
    	AdvicePrinterTextArea.appendText(status+"\n");
    } // appendTextArea
    

}
