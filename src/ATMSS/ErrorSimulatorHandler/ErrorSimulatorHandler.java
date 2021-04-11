package ATMSS.ErrorSimulatorHandler;

import ATMSS.ATMSSStarter;
import ATMSS.HWHandler.HWHandler;
import AppKickstarter.misc.Msg;

public class ErrorSimulatorHandler extends HWHandler {
    public ErrorSimulatorHandler(String id, ATMSSStarter atmssStarter) {
        super(id, atmssStarter);
    }

    protected void processMsg(Msg msg) {
        switch (msg.getType()) {
            case Error:
                processError(msg.getDetails());
                break;

            default:
                log.warning(id + ": unknown message type: [" + msg + "]");
        }
    } // processMsg

    public void processError(String msg){
        String[] stringToken = msg.split(" ");

        if (stringToken.length < 3){
            log.info(id + ": incorrect simulation length" + msg);
            return;
        }

        switch(stringToken[0]){
            case "Error":
                switch(stringToken[1]){
                    case "AdvicePrinterHandler":
                        break;
                    case "BuzzerHandler":
                        break;
                    case "BAMSThreadHandler":
                        break;
                    case "DepositSlotHandler":
                        break;
                    case "DispenserSlotHandler":
                        break;
                    case "KeypadHandler":
                        break;
                    case "CardReaderHandler":
                        break;
                    case "TouchDisplayHandler":
                        break;
                    default:
                        log.info(id+ " :Error Simulator does not recognize second keyword!");
                }
                break;
            default:
                log.info(id+ " : Error Simulator does not recognize first keyword!");
        }
    }

//    protected void alert(String msg) {
//        log.info(id + ": alert -- " + msg);
//    }
}
