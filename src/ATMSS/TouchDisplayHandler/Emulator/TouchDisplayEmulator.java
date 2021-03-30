package ATMSS.TouchDisplayHandler.Emulator;

import ATMSS.ATMSSStarter;
import ATMSS.TouchDisplayHandler.TouchDisplayHandler;
import AppKickstarter.misc.Msg;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;


//======================================================================
// TouchDisplayEmulator
public class TouchDisplayEmulator extends TouchDisplayHandler {
    private final int WIDTH = 680;
    private final int HEIGHT = 520;
    private ATMSSStarter atmssStarter;
    private String id;
    private Stage myStage;
    private TouchDisplayEmulatorController touchDisplayEmulatorController;

    //------------------------------------------------------------
    // TouchDisplayEmulator
    public TouchDisplayEmulator(String id, ATMSSStarter atmssStarter) throws Exception {
        super(id, atmssStarter);
        this.atmssStarter = atmssStarter;
        this.id = id;
    } // TouchDisplayEmulator


    //------------------------------------------------------------
    // start
    public void start() throws Exception {
        Parent root;
        myStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        String fxmlName = "TouchDisplayEmulator.fxml";
        loader.setLocation(TouchDisplayEmulator.class.getResource(fxmlName));
        root = loader.load();
        touchDisplayEmulatorController = (TouchDisplayEmulatorController) loader.getController();
        touchDisplayEmulatorController.initialize(id, atmssStarter, log, this);
        myStage.initStyle(StageStyle.DECORATED);
        myStage.setScene(new Scene(root, WIDTH, HEIGHT));
        myStage.setTitle("Touch Display");
        myStage.setResizable(false);
        touchDisplayEmulatorController.welcomePage();
        myStage.setOnCloseRequest((WindowEvent event) -> {
            atmssStarter.stopApp();
            Platform.exit();
        });
        myStage.show();
    } // TouchDisplayEmulator


    //------------------------------------------------------------
    // handleUpdateDisplay
    protected void handleUpdateDisplay(Msg msg) {
        log.info(id + ": update display -- " + msg.getDetails());

        switch (msg.getDetails()) {
            case "BlankScreen":

            case "Welcome":             //touchDisplayEmulatorController.welcomePage();

            case "PIN Required":            //touchDisplayEmulatorController.enterPINPage();
                reloadStage("TouchDisplayEmulator.fxml", msg.getDetails());
                break;

            case "MainMenu":
                reloadStage("TouchDisplayMainMenu.fxml", msg.getDetails());
                break;

//            case "Cash Deposit":
//
//            case "Money Transfer":
//
//            case "Cash Withdrawal":
//
//            case "Account Balance Enquiry":
//
//            case "Account selection":

            case "Confirmation":
                reloadStage("TouchDisplayConfirmation.fxml", msg.getDetails());
                break;

            case "enterPIN":
                touchDisplayEmulatorController.changePIN();
                break;

            case "erasePIN":
                touchDisplayEmulatorController.erasePIN();
                break;

            default:
                log.severe(id + ": update display with unknown display type -- " + msg.getDetails());
                break;
        }
    } // handleUpdateDisplay

    protected void handleLogin() {
        super.handleLogin();
        touchDisplayEmulatorController.setLoginTrue();
    }


    //------------------------------------------------------------
    // reloadStage
    private void reloadStage(String fxmlFName, String detail) {
        TouchDisplayEmulator touchDisplayEmulator = this;

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    log.info(id + ": loading fxml: " + fxmlFName);

                    Parent root;
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(TouchDisplayEmulator.class.getResource(fxmlFName));
                    root = loader.load();
                    touchDisplayEmulatorController = (TouchDisplayEmulatorController) loader.getController();
                    touchDisplayEmulatorController.initialize(id, atmssStarter, log, touchDisplayEmulator);
                    switch (detail) {
                        case "Welcome":
                            touchDisplayEmulatorController.welcomePage();
                            break;

                        case "PIN Required":
                            touchDisplayEmulatorController.enterPINPage();
                            break;

                        case "MainMenu":
                            touchDisplayEmulatorController.mainMenuBox();
                            break;

                        default:
                            break;
                    }
                    myStage.setScene(new Scene(root, WIDTH, HEIGHT));
                } catch (Exception e) {
                    log.severe(id + ": failed to load " + fxmlFName);
                    e.printStackTrace();
                }
            }
        });
    } // reloadStage
} // TouchDisplayEmulator