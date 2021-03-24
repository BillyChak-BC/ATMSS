package ATMSS.ATMSS;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.*;
import AppKickstarter.timer.Timer;


//======================================================================
// ATMSS
public class ATMSS extends AppThread {
    private int pollingTime;
    private boolean loggedIn = false;
    private String cardNum ="";
    private String pin = "";
    private boolean getPin = false;

    private MBox cardReaderMBox;
    private MBox keypadMBox;
    private MBox touchDisplayMBox;
    private MBox DepositSlotMBox;
    private MBox DispenserSlotMBox;
    private MBox AdvicePrinterMBox;
    private MBox BuzzerMBox;

    //------------------------------------------------------------
    // ATMSS
    public ATMSS(String id, AppKickstarter appKickstarter) throws Exception {
	super(id, appKickstarter);
	pollingTime = Integer.parseInt(appKickstarter.getProperty("ATMSS.PollingTime"));
    } // ATMSS


    //------------------------------------------------------------
    // run
    public void run() {
	Timer.setTimer(id, mbox, pollingTime);
	log.info(id + ": starting...");

	cardReaderMBox = appKickstarter.getThread("CardReaderHandler").getMBox();
	keypadMBox = appKickstarter.getThread("KeypadHandler").getMBox();
	touchDisplayMBox = appKickstarter.getThread("TouchDisplayHandler").getMBox();
	DepositSlotMBox = appKickstarter.getThread("DepositSlotHandler").getMBox();
	DispenserSlotMBox = appKickstarter.getThread("DispenserSlotHandler").getMBox();
	AdvicePrinterMBox = appKickstarter.getThread("AdvicePrinterHandler").getMBox();
	BuzzerMBox = appKickstarter.getThread("BuzzerHandler").getMBox();

	for (boolean quit = false; !quit;) {
	    Msg msg = mbox.receive();

	    log.fine(id + ": message received: [" + msg + "].");

	    switch (msg.getType()) {
		case TD_MouseClicked:
		    log.info("MouseCLicked: " + msg.getDetails());
		    processMouseClicked(msg);
			//after processing click depending on x-y AND loggedin is true, change to different screen for deposit/withdraw/transfer
		    break;

		case KP_KeyPressed:
		    log.info("KeyPressed: " + msg.getDetails());
		    processKeyPressed(msg);
		    break;

			case CR_CardInserted:		//if receive card inserted from cardreader, do:
				log.info("CardInserted: " + msg.getDetails());
				cardNum = msg.getDetails();
				getPin = true;		//if we are now looking for pin,
				keypadMBox.send(new Msg(id, mbox, Msg.Type.Alert, ""));
		    //if card inserted proceed to ask pin (send msg to ask for PIN)
		    break;
			case LoggedIn: //BAMSHandler send msg back and indicate success
			//if success login return some boolean variable that enable all methods that need login to be true to act
			loggedIn = true;
			getPin = false; //on login success, no need pin anymore
			touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.LoggedIn, "Success")); //change screen to main menu to select transaction
			//ignore the password validation temporarily
			//send verification success notification to touchscreen display so that screen is changed
			break;
		case Denom_sum:
			log.info("CashDeposit Denominations: " + msg.getDetails());
			break;

		case TimesUp:
		    Timer.setTimer(id, mbox, pollingTime);
		    log.info("Poll: " + msg.getDetails());
		    cardReaderMBox.send(new Msg(id, mbox, Msg.Type.Poll, ""));
		    keypadMBox.send(new Msg(id, mbox, Msg.Type.Poll, ""));
		    touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.Poll, ""));
		    DepositSlotMBox.send(new Msg(id, mbox, Msg.Type.Poll, ""));
		    DispenserSlotMBox.send(new Msg(id, mbox, Msg.Type.Poll, ""));
		    AdvicePrinterMBox.send(new Msg(id, mbox, Msg.Type.Poll, ""));
		    BuzzerMBox.send(new Msg(id, mbox, Msg.Type.Poll, ""));
		    break;

		case PollAck:
		    log.info("PollAck: " + msg.getDetails());
		    break;

		case Terminate:
		    quit = true;
		    break;

		default:
		    log.warning(id + ": unknown message type: [" + msg + "]");
	    }
	}

	// declaring our departure
	appKickstarter.unregThread(this);
	log.info(id + ": terminating...");
    } // run


    //------------------------------------------------------------
    // processKeyPressed
    private void processKeyPressed(Msg msg) {
        // *** The following is an example only!! ***
        if (msg.getDetails().compareToIgnoreCase("Cancel") == 0) {
			cardReaderMBox.send(new Msg(id, mbox, Msg.Type.CR_EjectCard, ""));
			pin="";		//if transaction canceled, reset pin variable
		} else if (msg.getDetails().compareToIgnoreCase("Erase") == 0){
			pin="";		//if transaction canceled, reset pin variable
		}else if (getPin && (msg.getDetails().compareToIgnoreCase("Enter") == 0)){
        	//send variables cardNum and pin to BAMS for login
		}else if (msg.getDetails().compareToIgnoreCase("1") == 0) {
			touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "BlankScreen"));
		} else if (msg.getDetails().compareToIgnoreCase("2") == 0) {
			touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "MainMenu"));
		} else if (msg.getDetails().compareToIgnoreCase("3") == 0) {
			touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "Confirmation"));
		}
    } // processKeyPressed


    //------------------------------------------------------------
    // processMouseClicked
    private void processMouseClicked(Msg msg) {
	// *** process mouse click here!!! ***
    } // processMouseClicked
} // CardReaderHandler
