package ATMSS.CardReaderHandler.Emulator;

import ATMSS.ATMSSStarter;
import ATMSS.CardReaderHandler.CardReaderHandler;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;


//======================================================================
// CardReaderEmulator
public class CardReaderEmulator extends CardReaderHandler {
    private ATMSSStarter atmssStarter;
    private String id;
    private Stage myStage;
    private CardReaderEmulatorController cardReaderEmulatorController;

    //------------------------------------------------------------
    // CardReaderEmulator
    public CardReaderEmulator(String id, ATMSSStarter atmssStarter) {
	super(id, atmssStarter);
	this.atmssStarter = atmssStarter;
	this.id = id;
    } // CardReaderEmulator


    //------------------------------------------------------------
    // start
    public void start() throws Exception {
	Parent root;
	myStage = new Stage();
	FXMLLoader loader = new FXMLLoader();
	String fxmlName = "CardReaderEmulator.fxml";
	loader.setLocation(CardReaderEmulator.class.getResource(fxmlName));
	root = loader.load();
	cardReaderEmulatorController = (CardReaderEmulatorController) loader.getController();
	cardReaderEmulatorController.initialize(id, atmssStarter, log, this);
	myStage.initStyle(StageStyle.DECORATED);
	myStage.setScene(new Scene(root, 350, 470));
	myStage.setTitle("Card Reader");
	myStage.setResizable(false);
	myStage.setOnCloseRequest((WindowEvent event) -> {
	    atmssStarter.stopApp();
	    Platform.exit();
	});
	myStage.show();
    } // CardReaderEmulator


    //------------------------------------------------------------
    // handleCardInsert
    protected void handleCardInsert() {
        // fixme
	super.handleCardInsert();
	cardReaderEmulatorController.appendTextArea("Card Inserted");
	cardReaderEmulatorController.updateCardStatus("Card Inserted");
    } // handleCardInsert


    //------------------------------------------------------------
    // handleCardEject
    protected void handleCardEject() {
        // fixme
	super.handleCardEject();
	cardReaderEmulatorController.appendTextArea("Card Ejected");
	cardReaderEmulatorController.updateCardStatus("Card Ejected");
    } // handleCardEject


    //------------------------------------------------------------
    // handleCardRemove
    protected void handleCardRemove() {
	// fixme
	super.handleCardRemove();
	cardReaderEmulatorController.appendTextArea("Card Removed");
	cardReaderEmulatorController.updateCardStatus("Card Reader Empty");
    } // handleCardRemove
} // CardReaderEmulator
