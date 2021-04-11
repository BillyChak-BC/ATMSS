package ATMSS.ErrorSimulatorHandler.Emulator;

import ATMSS.ATMSSStarter;
import ATMSS.ErrorSimulatorHandler.ErrorSimulatorHandler;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

public class ErrorSimulatorEmulator extends ErrorSimulatorHandler {
    private ATMSSStarter atmssStarter;
    private String id;
    private Stage myStage;
    private ErrorSimulatorEmulatorController ErrorSimulatorEmulatorController;

    public ErrorSimulatorEmulator(String id, ATMSSStarter atmssStarter) throws Exception {
        super(id, atmssStarter);
        this.atmssStarter = atmssStarter;
        this.id = id;
    }

    public void start() throws Exception {
        Parent root;
        myStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        String fxmlName = "ErrorSimulatorEmulator.fxml";
        loader.setLocation(ErrorSimulatorEmulator.class.getResource(fxmlName));
        root = loader.load();
        ErrorSimulatorEmulatorController = (ErrorSimulatorEmulatorController) loader.getController();
        ErrorSimulatorEmulatorController.initialize(id, atmssStarter, log, this);
        myStage.initStyle(StageStyle.DECORATED);
        myStage.setScene(new Scene(root, 300, 300));
        myStage.setTitle("Simulator");
        myStage.setResizable(false);
        myStage.setOnCloseRequest((WindowEvent event) -> {
            atmssStarter.stopApp();
            Platform.exit();
        });
        myStage.show();
    }

//    protected void alert(String msg) {
//        super.alert(msg);
//        Platform.runLater(new Runnable() {
//            @Override
//            public void run() {
//                myStage.toFront();//move the stage to the front
//                //shake the stage
////				for (int i = 0; i < 10; i++) {
////					myStage.setX(myStage.getX()+10);
////					myStage.setX(myStage.getX()-10);
////					myStage.setX(myStage.getX()-10);
////					myStage.setX(myStage.getX()+10);
////				}
//            }
//        });
//
//
//    }
}
