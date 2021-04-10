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
        String fxmlName = "DispenserSlotEmulator.fxml";
        loader.setLocation(DispenserSlotEmulator.class.getResource(fxmlName));
        root = loader.load();
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
        super.handleDispenseCash(denoms);
        DispenserSlotEmulatorController.updateDenomsInventory(denoms, false);
    } // handleDepositCash

    protected void handleDispense(String msg) {
        if (msg.equals("OpenSlot") && !DispenserSlotEmulatorController.getTransactionStatus()) {
            log.info(id + ": Opening Dispenser Slot");            //atmss sends open command
            DispenserSlotEmulatorController.setTransactionStatus();
            DispenserSlotEmulatorController.updateCardStatus("Dispenser slot is open");
        } else if (msg.equals("CloseSlot") && DispenserSlotEmulatorController.getTransactionStatus()) {
            log.info(id + ": Closing Dispenser Slot");            //emulator or the hardware sends the close command
            DispenserSlotEmulatorController.updateAmtField("");
            DispenserSlotEmulatorController.setTransactionStatus();
            DispenserSlotEmulatorController.updateCardStatus("Dispenser slot is closed");
        }
    }

//    //------------------------------------------------------------


    protected void handleDenomsUpdate(String details) {
        super.handleDenomsUpdate(details);
        DispenserSlotEmulatorController.updateDenomsInventory(details, true);
    }

    protected void handleDenomsInventoryCheck() {
        super.handleDenomsInventoryCheck();
        DispenserSlotEmulatorController.checkDenomsInventory();
    }
}
