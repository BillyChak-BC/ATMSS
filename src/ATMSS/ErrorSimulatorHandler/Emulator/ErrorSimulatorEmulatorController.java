package ATMSS.ErrorSimulatorHandler.Emulator;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import java.util.logging.Logger;

public class ErrorSimulatorEmulatorController {
    private String id;
    private AppKickstarter appKickstarter;
    private Logger log;
    private ErrorSimulatorEmulator ErrorSimulatorEmulator;
    private MBox ErrorSimulatorMBox;

    public TextArea ErrorSimulatorTextArea;

    public void initialize(String id, AppKickstarter appKickstarter, Logger log, ErrorSimulatorEmulator ErrorSimulatorEmulator) {
        this.id = id;
        this.appKickstarter = appKickstarter;
        this.log = log;
        this.ErrorSimulatorEmulator = ErrorSimulatorEmulator;
        this.ErrorSimulatorMBox = appKickstarter.getThread("ErrorSimulatorHandler").getMBox();
    } // initialize

    public void buttonPressed(ActionEvent actionEvent) {
        Button btn = (Button) actionEvent.getSource();

        switch (btn.getText()) {
            case "Submit":
                if (!ErrorSimulatorTextArea.equals("")){
                    ErrorSimulatorMBox.send(new Msg(id, ErrorSimulatorMBox, Msg.Type.Error,ErrorSimulatorTextArea.getText()));//send msg to handler to continue processing
                    clearText();//then clear
                }
                break;

            default:
                log.warning(id + ": unknown button: [" + btn.getText() + "]");
                break;
        }
    } // buttonPressed

    public void clearText(){
        ErrorSimulatorTextArea.setText("");
    }
}
