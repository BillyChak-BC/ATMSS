package ATMSS.DepositSlotHandler;

import ATMSS.HWHandler.HWHandler;
import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.Msg;

public class DepositSlotHandler extends HWHandler {

    public DepositSlotHandler(String id, AppKickstarter appKickstarter) {
        super(id, appKickstarter);
    } // DepositSlotHandler

    protected void processMsg(Msg msg) {			//this processMsg is for that of DepositSlotMBox (mbox) <== inheritance property
    												//From EmulatorController send to MBox, process it here, then send to atmssMBox
        switch (msg.getType()) {                    //Sean: change msg types to ones i need (dont forget to add to enums type in Msg Class!!)
        	case Denom_sum:
        		atmss.send(new Msg(id, mbox, Msg.Type.Denom_sum, msg.getDetails()));
        		break;
        	case Deposit:
        		handleDeposit(msg.getDetails());
        		break;
//            case CR_CardInserted:
//                //atmss.send(new Msg(id, mbox, Msg.Type.CR_CardInserted, msg.getDetails()));
//                break;
//
//            case CR_EjectCard:
//                //handleCardEject();
//                break;
//
//            case CR_CardRemoved:
//                //handleCardRemove();
//                break;

            default:
                log.warning(id + ": unknown message type: [" + msg + "]");
        }
    } // processMsg

    //------------------------------------------------------------
    //slot open/close status field ; moneyreceived; moneyremoved status field and buttons;
    //

    // handleDepositCash
    protected void handleDepositCash() {
        log.info(id + ": cash deposited");
    } // handleCardInsert
    
    protected void handleDeposit(String msg) {
    	if (msg.equals("OpenSlot")) {
    		log.info(id + "Opening Deposit Slot");			//atmss sends open command
    	}else if (msg.equals("CloseSlot")) {
    		log.info(id + "Closing Deposit Slot");			//emulator or the hardware sends the close command
    	}
    }
    
    
//    //------------------------------------------------------------
//    // handleCardEject
//    protected void handleCardEject() {
//        log.info(id + ": card ejected");
//    } // handleCardEject
//
//
//    //------------------------------------------------------------
//    // handleCardRemove
//    protected void handleCardRemove() {
//        log.info(id + ": card removed");
//    } // handleCardRemove
}
