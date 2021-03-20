package ATMSS.DispenserSlotHandler;

import ATMSS.HWHandler.HWHandler;
import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.Msg;

public class DispenserSlotHandler extends HWHandler {
	
	public DispenserSlotHandler(String id, AppKickstarter appKickstarter) {
        super(id, appKickstarter);
    } // DepositSlotHandler
	
	protected void processMsg(Msg msg) {			//this processMsg is for that of DepositSlotMBox (mbox) <== inheritance property
		//From EmulatorController send to MBox, process it here, then send to atmssMBox
		switch (msg.getType()) {
		case Denom_sum:			//use this to represent the denominations for dispenser to dispense
		handleDispenseCash(msg.getDetails());
//		atmss.send(new Msg(id, mbox, Msg.Type.Denom_sum, msg.getDetails()));
		break;
		case Dispense:   //msg type to open or close relevant slot (indicates start or end of withdraw transaction)
		handleDispense(msg.getDetails());
		break;
		
		//case CR_CardInserted:
		////atmss.send(new Msg(id, mbox, Msg.Type.CR_CardInserted, msg.getDetails()));
		//break;
		//
		//case CR_EjectCard:
		////handleCardEject();
		//break;
		//
		//case CR_CardRemoved:
		////handleCardRemove();
		//break;
		
		default:
		log.warning(id + ": unknown message type: [" + msg + "]");
		}
	}
	
	protected void handleDispenseCash(String denoms) {
        log.info(id + ": cash dispensed");
    } // handleCardInsert
    
    protected void handleDispense(String msg) {
    	if (msg.equals("OpenSlot")) {
    		log.info(id + "Opening Dispenser Slot");			//atmss sends open command
    	}else if (msg.equals("CloseSlot")) {
    		log.info(id + "Closing Dispenser Slot");			//emulator or the hardware sends the close command
    	}
    }
	
}
