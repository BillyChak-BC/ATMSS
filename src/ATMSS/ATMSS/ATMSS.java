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
    private int errorCount = 0;

    private MBox cardReaderMBox;
    private MBox keypadMBox;
    private MBox touchDisplayMBox;
    private MBox DepositSlotMBox;
    private MBox DispenserSlotMBox;
    private MBox AdvicePrinterMBox;
    private MBox BuzzerMBox;
    private MBox bamsThreadMBox;

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
	bamsThreadMBox = appKickstarter.getThread("BAMSThreadHandler").getMBox();

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
				touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "PIN Required"));
		    //if card inserted proceed to ask pin (send msg to ask for PIN)
		    break;
			case LoggedIn: //BAMSHandler send msg back and indicate success
				if (msg.getDetails().equals("Success")){
					//if success login return some boolean variable that enable all methods that need login to be true to act
					loggedIn = true;
					getPin = false; //on login success, no need pin anymore
					touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.LoggedIn, "Success")); //change screen to main menu to select transaction
					//ignore the password validation temporarily
					//send verification success notification to touchscreen display so that screen is changed
				}else if (msg.getDetails().equals("Fail")){
					errorCount++;
					if (errorCount >=3){
						//instruct retain
					}

				}

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
            //should be a screen showing thank you first
			touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "Welcome"));
		} else if (msg.getDetails().compareToIgnoreCase("Erase") == 0){
			pin="";		//if transaction canceled, reset pin variable
			touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "erasePIN"));
		}else if (getPin && (msg.getDetails().compareToIgnoreCase("Enter") == 0)){
        	// Set maximum 9
			if (pin.length() > 9) {
				pin = pin.substring(0,9);
			}
            log.info(id+" : verifying cardnum and pin");
        	bamsThreadMBox.send(new Msg(id, mbox, Msg.Type.Verify, cardNum+" "+pin));

        	log.info("pin: " + pin);
        	//send variables cardNum and pin to BAMS for login
		}else if (getPin){
        	//set this up after checking how long is the pin
//        	if (pin.length() < 6) {
//
//			}
        	switch(msg.getDetails()){
				case "1":
				case "2":
				case "3":
				case "4":
				case "5":
				case "6":
				case "7":
				case "8":
				case "9":
				case "0":
					pin += msg.getDetails();
					break;
				default:
					break;
			}
			touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "enterPIN"));
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
