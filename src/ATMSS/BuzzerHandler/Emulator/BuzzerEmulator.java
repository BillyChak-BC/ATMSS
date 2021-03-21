package ATMSS.BuzzerHandler.Emulator;

import ATMSS.ATMSSStarter;
import ATMSS.BuzzerHandler.BuzzerHandler;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

public class BuzzerEmulator extends BuzzerHandler {
    private ATMSSStarter atmssStarter;
    private String id;
    private Stage myStage;
    private BuzzerEmulatorController buzzerEmulatorController;

    public BuzzerEmulator(String id, ATMSSStarter atmssStarter) throws Exception {
        super(id, atmssStarter);
        this.atmssStarter = atmssStarter;
        this.id = id;
    }

    public void start() throws Exception {
        Parent root;
        myStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        String fxmlName = "BuzzerEmulator.fxml";
        loader.setLocation(BuzzerEmulator.class.getResource(fxmlName));
        root = loader.load();
        buzzerEmulatorController = (BuzzerEmulatorController) loader.getController();
        buzzerEmulatorController.initialize(id, atmssStarter, log, this);
        myStage.initStyle(StageStyle.DECORATED);
        myStage.setScene(new Scene(root, 300, 300));
        myStage.setTitle("Buzzer");
        myStage.setResizable(false);
        myStage.setOnCloseRequest((WindowEvent event) -> {
            atmssStarter.stopApp();
            Platform.exit();
        });
        myStage.show();
    }

    protected void alert(String msg) {
        super.alert(msg);
        myStage.toFront();//move the stage to the front
        //shake the stage
        for (int i = 0; i < 10; i++) {
            myStage.setX(myStage.getX()+10);
            myStage.setX(myStage.getX()-10);
            myStage.setX(myStage.getX()-10);
            myStage.setX(myStage.getX()+10);
        }
        buzzerEmulatorController.appendTextArea(msg);
    }
}
