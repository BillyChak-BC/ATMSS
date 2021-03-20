package ATMSS.HWHandler;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.*;


//======================================================================
// HWHandler
public class HWHandler extends AppThread {
    protected MBox atmss = null;

    //------------------------------------------------------------
    // HWHandler
    public HWHandler(String id, AppKickstarter appKickstarter) {
        super(id, appKickstarter);
    } // HWHandler


    //------------------------------------------------------------
    // run
    public void run() {
        atmss = appKickstarter.getThread("ATMSS").getMBox();
        log.info(id + ": starting...");

        for (boolean quit = false; !quit;) {
            Msg msg = mbox.receive();		//for this specific HW's MBox receive //this is based on the inheritance property from appthread and Handlers

            log.fine(id + ": message received: [" + msg + "].");

            switch (msg.getType()) {
                case Poll:
                    atmss.send(new Msg(id, mbox, Msg.Type.PollAck, id + " is up!"));
                    break;

                case Terminate:
                    quit = true;
                    break;

                default:
                    processMsg(msg);
            }
        }

        // declaring our departure
        appKickstarter.unregThread(this);
        log.info(id + ": terminating...");
    } // run


    //------------------------------------------------------------
    // processMsg
    protected void processMsg(Msg msg) {
        log.warning(id + ": unknown message type: [" + msg + "]");
    } // processMsg
}
