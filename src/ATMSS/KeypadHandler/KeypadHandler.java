package ATMSS.KeypadHandler;

import ATMSS.HWHandler.HWHandler;
import ATMSS.ATMSSStarter;
import AppKickstarter.misc.*;


//======================================================================
// KeypadHandler
public class KeypadHandler extends HWHandler {
    //------------------------------------------------------------
    // KeypadHandler
    public KeypadHandler(String id, ATMSSStarter atmssStarter) {
	super(id, atmssStarter);
    } // KeypadHandler


    //------------------------------------------------------------
    // processMsg
    protected void processMsg(Msg msg) {
        switch (msg.getType()) {
            case KP_KeyPressed:
                atmss.send(new Msg(id, mbox, Msg.Type.KP_KeyPressed, msg.getDetails()));
                break;

            default:
                log.warning(id + ": unknown message type: [" + msg + "]");
        }
    } // processMsg
} // KeypadHandler
