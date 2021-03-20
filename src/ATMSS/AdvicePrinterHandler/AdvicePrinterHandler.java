package ATMSS.AdvicePrinterHandler;

import ATMSS.HWHandler.HWHandler;
import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.Msg;

public class AdvicePrinterHandler extends HWHandler {
	
	 public AdvicePrinterHandler(String id, AppKickstarter appKickstarter) {
		 super(id, appKickstarter);
	 } // DepositSlotHandler
	 
	 protected void processMsg(Msg msg) {			//this processMsg is for that of DepositSlotMBox (mbox) <== inheritance property
			//From EmulatorController send to MBox, process it here, then send to atmssMBox
		switch (msg.getType()) {                    //Sean: change msg types to ones i need (dont forget to add to enums type in Msg Class!!)
			case Advice:
			handleAdvicePrint(msg.getDetails());
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
	 } // processMsg
	 
    // handleDepositCash
    protected void handleAdvicePrint(String msg) {
        log.info(id + ": advice printed");
    } // handleCardInsert
	    
}
