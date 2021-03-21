package ATMSS.BuzzerHandler;

import ATMSS.ATMSSStarter;
import ATMSS.HWHandler.HWHandler;
import AppKickstarter.misc.Msg;

public class BuzzerHandler extends HWHandler {
    public BuzzerHandler(String id, ATMSSStarter atmssStarter) {
        super(id, atmssStarter);
    }

    //------------------------------------------------------------
    // processMsg
    protected void processMsg(Msg msg) {
        switch (msg.getType()) {
            case Alert:
                alert(msg.getDetails());
                break;

            default:
                log.warning(id + ": unknown message type: [" + msg + "]");
        }
    } // processMsg

    protected void alert(String msg) {
        log.info(id + ": alert -- " + msg);
    }
}
