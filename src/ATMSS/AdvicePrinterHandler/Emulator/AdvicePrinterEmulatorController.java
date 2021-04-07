package ATMSS.AdvicePrinterHandler.Emulator;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

public class AdvicePrinterEmulatorController {

    private String id;
    private AppKickstarter appKickstarter;
    private Logger log;
    private AdvicePrinterEmulator AdvicePrinterEmulator;
    private MBox AdvicePrinterMBox;
    private String Advice = "";

    public TextArea DispenserSlotTextArea;
    private static int paperInventory = 100;
    //is this ok to use own computer time?
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
    LocalDateTime timeNow = LocalDateTime.now();

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
                if (paperInventory > 0) {
                    DispenserSlotTextArea.setText("");
                    paperInventory--;
                    if (paperInventory <= 0) {
                        //send msg to ATMSS that advice printer run out of paper
                        AdvicePrinterMBox.send(new Msg(id, AdvicePrinterMBox, Msg.Type.Error, "Advice Printer run out of paper"));
                    }
                }
                break;

            default:
                log.warning(id + ": unknown button: [" + btn.getText() + "]");
                break;
        }
    } // buttonPressed

    public void storeAdvice(String advice) {
        if (Advice.equals("")) {
            Advice = advice + "\n";
        } else {
            Advice = Advice + " " + advice + "\n";
        }
    }

    public void clearAdvice() {
        Advice = "";
    }

    //run out of paper

    //------------------------------------------------------------
    // appendTextArea
    public void appendTextArea(String status) {
        //things to print: date, operating acc, transaction type (e.g. cash deposit, money transaction), transaction acc (if any), amount (if any), transaction status(success, failure)
        String[] details = status.split("_");
        if (details.length == 1) {
            DispenserSlotTextArea.appendText(status + "\n");
        } else {

            DispenserSlotTextArea.appendText("Date and time: " + dateTimeFormatter.format(timeNow) + "\n");
            for (int i = 0; i < details.length; i++) {
                switch (i) {
                    case 0:
                        DispenserSlotTextArea.appendText("Account Number: " + details[i] + "\n");
                        break;

                    case 1:
                        DispenserSlotTextArea.appendText("Transaction Type: " + details[i] + "\n");
                        break;

                    case 2:
                        if (!details[i].equals("")) {
                            DispenserSlotTextArea.appendText("Transfer Account Number: " + details[i] + "\n");
                        }
                        break;

                    case 3:
                        DispenserSlotTextArea.appendText("Amount: " + details[i] + "\n");
                        break;

                    case 4:
                        DispenserSlotTextArea.appendText("Status: " + details[i] + "\n");
                        break;

                    default:
                        break;
                }
            }
        }
    } // appendTextArea
}
