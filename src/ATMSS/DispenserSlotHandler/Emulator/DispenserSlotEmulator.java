package ATMSS.DispenserSlotHandler.Emulator;

import java.util.StringTokenizer;

import ATMSS.ATMSSStarter;
import ATMSS.DepositSlotHandler.Emulator.DepositSlotEmulator;
import ATMSS.DepositSlotHandler.Emulator.DepositSlotEmulatorController;
import ATMSS.DispenserSlotHandler.DispenserSlotHandler;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

public class DispenserSlotEmulator extends DispenserSlotHandler {
	private ATMSSStarter atmssStarter;
    private String id;
    private Stage myStage;
    private DispenserSlotEmulatorController DispenserSlotEmulatorController;
    
    public DispenserSlotEmulator(String id, ATMSSStarter atmssStarter) {
        super(id, atmssStarter);
        this.atmssStarter = atmssStarter;
        this.id = id;
    } // CardReaderEmulator
    
    public void start() throws Exception {
        Parent root;
        myStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        String fxmlName = "DispenserSlotEmulator.fxml";        //Sean:      create a DepositSlotEmulator.fxml
        loader.setLocation(DispenserSlotEmulator.class.getResource(fxmlName));
        root = loader.load();       //error?
        DispenserSlotEmulatorController = (DispenserSlotEmulatorController) loader.getController();
        DispenserSlotEmulatorController.initialize(id, atmssStarter, log, this);
        myStage.initStyle(StageStyle.DECORATED);
        myStage.setScene(new Scene(root, 350, 470));
        myStage.setTitle("Dispenser Slot");
        myStage.setResizable(false);
        myStage.setOnCloseRequest((WindowEvent event) -> {
            atmssStarter.stopApp();
            Platform.exit();
        });
        myStage.show();
    } // CardReaderEmulator
    
  //------------------------------------------------------------
    // handleCardInsert
    protected void handleDispenseCash(String denoms) {
        // fixme
        super.handleDispenseCash(denoms);
        DispenserSlotEmulatorController.appendTextArea("Cash Dispensed");
        StringTokenizer tokens = new StringTokenizer(denoms);
        int amtSum = 0;
        int count = 0;
        
        while (tokens.hasMoreTokens()) {
        	String token = tokens.nextToken();
        	if (count % 3 == 0) {
        		amtSum = amtSum + Integer.parseInt(token)*100;
        		DispenserSlotEmulatorController.appendTextArea(token+" $100 note(s) dispensed.");
        	}else if (count % 3 == 1) {
        		amtSum = amtSum + Integer.parseInt(token)*500;
        		DispenserSlotEmulatorController.appendTextArea(token+" $500 note(s) dispensed.");
        	}else if (count % 3 == 2) {
        		amtSum = amtSum + Integer.parseInt(token)*1000;
        		DispenserSlotEmulatorController.appendTextArea(token+" $1000 note(s) dispensed.");
        	}
        	count++;
        }
        
        DispenserSlotEmulatorController.updateAmtField(""+amtSum);
    } // handleDepositCash

    protected void handleDispense(String msg) {
    	if (DispenserSlotEmulatorController.setTransactionStatus()) { 		//if is set to true, means deposit slot open
    		super.handleDispense(msg);
    		DispenserSlotEmulatorController.updateCardStatus("Dispenser Slot is open");
    	}else if (!DispenserSlotEmulatorController.setTransactionStatus()) {
    		super.handleDispense(msg);
    		DispenserSlotEmulatorController.updateCardStatus("Dispenser Slot is closed");
    	}
    }

//    //------------------------------------------------------------

}
