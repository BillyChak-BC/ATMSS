package ATMSS.ATMSS;

import ATMSS.TouchDisplayHandler.Emulator.TouchDisplayEmulatorController;
import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.*;
import AppKickstarter.timer.Timer;

import java.util.StringTokenizer;


//======================================================================
// ATMSS
public class ATMSS extends AppThread {
    private int pollingTime;
    private int atmssTimerID = -1;
    private int depositTimerID = -1;

    private boolean loggedIn = false;
    private String transaction = "";        //would it be better to store as a String compared to boolean?
    private static String cardNum = "";
    private static String selectedAcc = "";
    private static String transferAcc = "";
    private String pin = "";
    private String amountTyped = "";
    private boolean getPin = false;
    private boolean getAmount = false;
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
        atmssTimerID = Timer.setTimer(id, mbox, pollingTime);
        log.info(id + ": starting...");

        cardReaderMBox = appKickstarter.getThread("CardReaderHandler").getMBox();
        keypadMBox = appKickstarter.getThread("KeypadHandler").getMBox();
        touchDisplayMBox = appKickstarter.getThread("TouchDisplayHandler").getMBox();
        DepositSlotMBox = appKickstarter.getThread("DepositSlotHandler").getMBox();
        DispenserSlotMBox = appKickstarter.getThread("DispenserSlotHandler").getMBox();
        AdvicePrinterMBox = appKickstarter.getThread("AdvicePrinterHandler").getMBox();
        BuzzerMBox = appKickstarter.getThread("BuzzerHandler").getMBox();
        bamsThreadMBox = appKickstarter.getThread("BAMSThreadHandler").getMBox();

        for (boolean quit = false; !quit; ) {
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

                case CR_CardInserted:        //if receive card inserted from cardreader, do:
                    log.info("CardInserted: " + msg.getDetails());
                    cardNum = msg.getDetails();
                    getPin = true;        //if we are now looking for pin,
                    keypadMBox.send(new Msg(id, mbox, Msg.Type.Alert, ""));
                    touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "PIN Required"));
                    //if card inserted proceed to ask pin (send msg to ask for PIN)
                    break;

                case LoggedIn: //BAMSHandler send msg back and indicate success
                    if (msg.getDetails().equals("Success")) {
                        //if success login return some boolean variable that enable all methods that need login to be true to act
                        loggedIn = true;
                        getPin = false; //on login success, no need pin anymore
                        touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.LoggedIn, "Success")); //change screen to menu to select account
                        //send verification success notification to touchscreen display so that screen is changed
                    } else if (msg.getDetails().equals("Fail")) {
                        errorCount++;
                        if (errorCount >= 3) {
                            //instruct retain
                        } else {
                            pin = "";
                            keypadMBox.send(new Msg(id, mbox, Msg.Type.Alert, ""));
                            touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "PIN Required"));
                        }
                    }
                    break;

                case GetAccount:
                    bamsThreadMBox.send(new Msg(id, mbox, Msg.Type.GetAccount, cardNum));
//				touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_SelectAccount, msg.getDetails()));
                    break;

                case ReceiveAccount:
                    touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_SelectAccount, transaction + "_" + msg.getDetails()));
                    break;

                case Selected_Acc:
                    if (transaction.equals("")) {
                        selectedAcc = msg.getDetails();        //on logout please clear this value
                    } else {
                        if (!selectedAcc.equals("")) {
                            transferAcc = msg.getDetails();
                            getAmount = true;
                            //update touchdisplay
                            touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "Money Transfer_" + transferAcc));
                        }
                    }
                    break;

                case MoneyTransferResult:
                    log.info(id + ": Money Transfer from " + selectedAcc + " to " + transferAcc + ": $" + msg.getDetails());
                    touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.MoneyTransferResult, transferAcc + "_" + msg.getDetails()));
                    break;

                case Dispense:
                    log.info(id + ": Cash Dispense: $" + msg.getDetails());
                    DispenserSlotMBox.send(new Msg(id, mbox, Msg.Type.Dispense, msg.getDetails()));
                    touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.Dispense, msg.getDetails()));
                    break;

                case EnquiryResult:
                    touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.EnquiryResult, msg.getDetails()));
                    break;

                case Denom_sum:
                    String[] denom = msg.getDetails().split(" ");
                    amountTyped = (Integer.parseInt(denom[0]) * 100 + Integer.parseInt(denom[1]) * 500 + Integer.parseInt(denom[2]) * 1000) + "";
                    log.info("CashDeposit Denominations: " + amountTyped);
                    Timer.cancelTimer(id, mbox, depositTimerID);//remove deposit slot timer
                    touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.Denom_sum, msg.getDetails()));
                    break;

                case DepositResult:
                    touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.DepositResult, msg.getDetails()));
                    break;

                case TimesUp:
                    StringTokenizer tokens = new StringTokenizer(msg.getDetails());
                    String msgtype = tokens.nextToken();
                    int timerID = Integer.parseInt(tokens.nextToken());

                    if (timerID == atmssTimerID) {
                        atmssTimerID = Timer.setTimer(id, mbox, pollingTime);
                        log.info("Poll: " + msg.getDetails());
                        cardReaderMBox.send(new Msg(id, mbox, Msg.Type.Poll, ""));
                        keypadMBox.send(new Msg(id, mbox, Msg.Type.Poll, ""));
                        touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.Poll, ""));
                        DepositSlotMBox.send(new Msg(id, mbox, Msg.Type.Poll, ""));
                        DispenserSlotMBox.send(new Msg(id, mbox, Msg.Type.Poll, ""));
                        AdvicePrinterMBox.send(new Msg(id, mbox, Msg.Type.Poll, ""));
                        BuzzerMBox.send(new Msg(id, mbox, Msg.Type.Poll, ""));
                    } else if (timerID == depositTimerID) {
                        depositTimerID = -1;
                        DepositSlotMBox.send(new Msg(id, mbox, Msg.Type.Deposit, "CloseSlot"));
                        //touchdisplay update
                        touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.Denom_sum, "0 0 0"));
                    }

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
        log.info(id + ": " + msg.getDetails() + " is pressed");
        if (msg.getDetails().compareToIgnoreCase("Cancel") == 0) {
            cardReaderMBox.send(new Msg(id, mbox, Msg.Type.CR_EjectCard, ""));
            touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "erasePIN"));
            //should be a screen showing thank you first
            allReset();        //if transaction canceled, reset pin variable
            touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "Welcome"));
        } else if (getPin) {
            keypadMBox.send(new Msg(id, mbox, Msg.Type.Alert, ""));
            // Set maximum password length to 9
            if (msg.getDetails().compareToIgnoreCase("Erase") == 0) {
                pin = "";        //if transaction canceled, reset pin variable
                touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "erasePIN"));
            } else if (msg.getDetails().compareToIgnoreCase("Enter") == 0) {
                // Prevent entering "00" at the end
                if (pin.length() > 9) {
                    pin = pin.substring(0, 9);
                }

                log.info(id + " : verifying cardnum and pin");
                bamsThreadMBox.send(new Msg(id, mbox, Msg.Type.Verify, cardNum + " " + pin));

                log.info("pin: " + pin);
                //send variables cardNum and pin to BAMS for login
            } else {
                if (pin.length() < 9) {
                    switch (msg.getDetails()) {
                        case "1":
                        case "2":
                        case "3":
                        case "4":
                        case "5":
                        case "6":
                        case "7":
                        case "8":
                        case "9":
                        case "00":
                        case "0":
                            pin += msg.getDetails();
                            break;
                        default:
                            break;
                    }
                    if (msg.getDetails().equals("00")) {
                        // Run two times if entering "00"
                        touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "enterPIN"));
                    }
                    touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "enterPIN"));
                }
            }
        } else if (getAmount) {
            keypadMBox.send(new Msg(id, mbox, Msg.Type.Alert, ""));
            if (msg.getDetails().compareToIgnoreCase("Erase") == 0) {
                amountTyped = "";
                if (transaction.equals("Cash Withdrawal")) {
                    touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, transaction));
                } else if (transaction.equals("Money Transfer")) {
                    touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, transaction + "_" + transferAcc));
                }
            } else if (msg.getDetails().compareToIgnoreCase("Enter") == 0) {
                //send amountTyped to BAMS
                if (amountTyped.equals("")) {
                    amountTyped = "0";
                }
                if (transaction.equals("Cash Withdrawal")) {
                    bamsThreadMBox.send(new Msg(id, mbox, Msg.Type.CashWithdraw, cardNum + " " + selectedAcc + " " + amountTyped));
                } else if (transaction.equals("Money Transfer")) {
                    bamsThreadMBox.send(new Msg(id, mbox, Msg.Type.MoneyTransferRequest, cardNum + " " + selectedAcc + " " + transferAcc + " " + amountTyped));
                }
            } else {
                switch (msg.getDetails()) {
                    case "1":
                    case "2":
                    case "3":
                    case "4":
                    case "5":
                    case "6":
                    case "7":
                    case "8":
                    case "9":
                    case "00":
                    case "0":
                        amountTyped += msg.getDetails();
                        break;
                    default:
                        break;
                }
                if (transaction.equals("Money Transfer")) {
                    touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TextTyped, transferAcc + "_" + msg.getDetails()));
                } else {
                    touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TextTyped, msg.getDetails()));
                }
            }
        }


//        else if (msg.getDetails().compareToIgnoreCase("1") == 0) {
//			touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "BlankScreen"));
//		} else if (msg.getDetails().compareToIgnoreCase("2") == 0) {
//			touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "MainMenu"));
//		} else if (msg.getDetails().compareToIgnoreCase("3") == 0) {
//			touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "Confirmation"));
//		}
    } // processKeyPressed


    //------------------------------------------------------------
    // processMouseClicked
    private void processMouseClicked(Msg msg) {
        // *** process mouse click here!!! ***
        if (loggedIn) {
            switch (transaction) {
                case "":
                    transaction = msg.getDetails();

                    switch (transaction) {
                        case "Cash Deposit":  //deposit
                            //set transaction to true
                            depositTimerID = Timer.setTimer(id, mbox, 15000);
                            //change touch screen display to ask how much to deposit
                            touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, transaction));
                            DepositSlotMBox.send(new Msg(id, mbox, Msg.Type.Alert, ""));  //alert deposit slot

                            DepositSlotMBox.send(new Msg(id, mbox, Msg.Type.Deposit, "OpenSlot")); //open deposit slot

                            break;
                        case "Money Transfer":
                            //set transaction to true
                            //change touch screen display to choose which acc to transfer from
                            //choose which acc to transfer to
                            //send msg to bams to transfer
                            bamsThreadMBox.send(new Msg(id, mbox, Msg.Type.GetAccount, cardNum));
                            break;
                        case "Cash Withdrawal":
                            //set transaction to true
                            //set timer
                            //change touch screen display to ask how much to withdraw
                            //alert keypad
                            getAmount = true;
                            touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, transaction));
                            break;
                        case "Account Balance Enquiry":
                            //set transaction to true
                            //check balance
                            String enquiryDetails = cardNum + " " + selectedAcc;
                            bamsThreadMBox.send(new Msg(id, mbox, Msg.Type.AccountEnquiry, enquiryDetails));
                            touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, transaction));
                            break;
                        default:
                            transaction = "";
                            break;
                    }
                    break;

                case "Cash Deposit":
                    switch (msg.getDetails()) {
                        case "Confirm Amount":
                            bamsThreadMBox.send(new Msg(id, mbox, Msg.Type.Deposit, cardNum + " " + selectedAcc + " " + amountTyped));
                            break;

                        case "Cancel":
                            //set transaction to true
                            amountTyped = "";
                            depositTimerID = Timer.setTimer(id, mbox, 15000);
                            //change touch screen display to ask how much to deposit
                            touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, transaction));
                            DepositSlotMBox.send(new Msg(id, mbox, Msg.Type.Alert, ""));  //alert deposit slot

                            DepositSlotMBox.send(new Msg(id, mbox, Msg.Type.Deposit, "OpenSlot")); //open deposit slot
                            break;

                        default:
                            break;
                    }

                case "Money Transfer":

                case "Cash Withdrawal":

                case "Account Balance Enquiry":

                default:
                    switch (msg.getDetails()) {
                        case "Continue Transaction and Print Advice":
                            //print advice
                            //reset the things and back to main menu
                            AdvicePrinterMBox.send(new Msg(id, mbox, Msg.Type.Print, selectedAcc + "_" + transaction + "_" + transferAcc + "_" +amountTyped + "_" + "Success"));
                            halfRest();
                            touchDisplayMBox.send(new Msg(id, touchDisplayMBox, Msg.Type.TD_UpdateDisplay, "MainMenu"));
                            break;

                        case "Continue Transaction":
                            //reset the things and back to main menu
                            halfRest();
                            touchDisplayMBox.send(new Msg(id, touchDisplayMBox, Msg.Type.TD_UpdateDisplay, "MainMenu"));
                            break;

                        case "End Transaction and Print Advice":
                            //print advice
                            //eject card
                            AdvicePrinterMBox.send(new Msg(id, mbox, Msg.Type.Print, selectedAcc + "_" + transaction + "_" + transferAcc + "_" +amountTyped + "_" + "Success"));
                            cardReaderMBox.send(new Msg(id, mbox, Msg.Type.CR_EjectCard, ""));
                            //should be a screen showing thank you first
                            allReset();        //if transaction canceled, reset pin variable
                            touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "Welcome"));
                            break;

                        case "End Transaction":
                            //eject card
                            cardReaderMBox.send(new Msg(id, mbox, Msg.Type.CR_EjectCard, ""));
                            //should be a screen showing thank you first
                            allReset();        //if transaction canceled, reset pin variable
                            touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "Welcome"));
                            break;

                        default:
                            break;
                    }
                    break;
            }

        }
    } // processMouseClicked

    private void allReset() {
        loggedIn = false;
        cardNum = "";
        pin = "";
        errorCount = 0;
        selectedAcc = "";
        transaction = "";
        transferAcc = "";
        amountTyped = "";
        getPin = false;
        getAmount = false;
    }

    private void halfRest() {
        transaction = "";
        transferAcc = "";
        amountTyped = "";
        getPin = false;
        getAmount = false;
    }
} // CardReaderHandler
