package ATMSS.DispenserSlotHandler.Emulator;

import ATMSS.DispenserSlotHandler.Emulator.DispenserSlotEmulator;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;


public class DispenserSlotEmulatorController {
	
	private String id;
    private AppKickstarter appKickstarter;
    private Logger log;
    private DispenserSlotEmulator DispenserSlotEmulator;
    private MBox DispenserSlotMBox;
    
    private boolean dispense = false;
//    private int denom100 = 0;					//this might not be necessary, since the denominations are being sent to dispenser to pr
//    private int denom500 = 0;					//we do not need to keep track or modify the values
//    private int denom1000 = 0;

    public TextField amtField;
    public TextField DispenserSlotStatusField;
    public TextArea DispenserSlotTextArea;
    
    //------------------------------------------------------------
    // initialize
    public void initialize(String id, AppKickstarter appKickstarter, Logger log, DispenserSlotEmulator DispenserSlotEmulator) {
        this.id = id;
        this.appKickstarter = appKickstarter;
        this.log = log;
        this.DispenserSlotEmulator = DispenserSlotEmulator;
        this.DispenserSlotMBox = appKickstarter.getThread("DispenserSlotHandler").getMBox();
    } // initialize
    
    public void buttonPressed(ActionEvent actionEvent) {
    	Button btn = (Button) actionEvent.getSource();

    	switch (btn.getText()) {

    	    case "Withdraw":
    	    	if (dispense && (amtField.getText().length() != 0)) {
    	    		DispenserSlotTextArea.appendText("Withdrawing " + amtField.getText()+"\n");
        	    	amtField.setText("");
        	    	DispenserSlotMBox.send(new Msg(id, DispenserSlotMBox, Msg.Type.Deposit, ("CloseSlot")));		//when finish withdraw close the slot
        	    	//denom100 = denom500 = denom1000 = 0;
    	    	}else {
    	    		DispenserSlotTextArea.appendText("Slot not open! Unable to withdraw!\n");
    	    	}
    	    	
    	    	//DispenserSlotMBox.send(new Msg(id, DispenserSlotMBox, Msg.Type.CR_CardInserted, amtField.getText()));
    		    //DispenserSlotTextArea.appendText("Sending " + amtField.getText()+"\n");
    		    //DispenserSlotStatusField.setText("Slot Opened");
    		break;

    	    default:
    	        log.warning(id + ": unknown button: [" + btn.getText() + "]");
    		break;
    	}
    } // buttonPressed
    
    //------------------------------------------------------------
    // updateCardStatus
    public void updateCardStatus(String status) {
    	DispenserSlotStatusField.setText(status);
    } // updateCardStatus
    
    public void updateAmtField(String amt) {			//upon receive msg from atmss regarding denominations to dispense (update amtfield of dispenser slot)
    	amtField.setText(amt);
    }
    
    //------------------------------------------------------------
    // appendTextArea
    public void appendTextArea(String status) {
    	DispenserSlotTextArea.appendText(status+"\n");
    } // appendTextArea
    
    public boolean setTransactionStatus() {
    	if(dispense) {
    		dispense = false;
    	}else {
    		dispense = true;
    	}
    	
    	return dispense;
    }

}
