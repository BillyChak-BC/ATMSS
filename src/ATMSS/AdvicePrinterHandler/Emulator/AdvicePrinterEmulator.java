package ATMSS.AdvicePrinterHandler.Emulator;

import ATMSS.ATMSSStarter;
import ATMSS.AdvicePrinterHandler.AdvicePrinterHandler;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

public class AdvicePrinterEmulator extends AdvicePrinterHandler {
	private ATMSSStarter atmssStarter;
    private String id;
    private Stage myStage;
    private AdvicePrinterEmulatorController AdvicePrinterEmulatorController;
    
    public AdvicePrinterEmulator(String id, ATMSSStarter atmssStarter) {
        super(id, atmssStarter);
        this.atmssStarter = atmssStarter;
        this.id = id;
    } // CardReaderEmulator
    
    public void start() throws Exception {
        Parent root;
        myStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        String fxmlName = "AdvicePrinterEmulator.fxml";        //Sean:      create a DepositSlotEmulator.fxml
        loader.setLocation(AdvicePrinterEmulator.class.getResource(fxmlName));
        root = loader.load();       //error?
        AdvicePrinterEmulatorController = (AdvicePrinterEmulatorController) loader.getController();
        AdvicePrinterEmulatorController.initialize(id, atmssStarter, log, this);
        myStage.initStyle(StageStyle.DECORATED);
        myStage.setScene(new Scene(root, 350, 470));
        myStage.setTitle("Advice Printer");
        myStage.setResizable(false);
        myStage.setOnCloseRequest((WindowEvent event) -> {
            atmssStarter.stopApp();
            Platform.exit();
        });
        myStage.show();
    } // CardReaderEmulator

    //------------------------------------------------------------
    // handleCardInsert
    protected void handleAdvicePrint(String msg) {
        // fixme
        super.handleAdvicePrint(msg);			//do i read the DepositSlotMBox in super and give the details of msg or read in emulator?
        AdvicePrinterEmulatorController.appendTextArea("Printing Advice...");
        AdvicePrinterEmulatorController.appendTextArea(msg);
    } // handleDepositCash
    
}
