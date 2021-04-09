package ATMSS.ATMSS;

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
    private int dispenseTimerID = -1;

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

                case LoggedIn: //BAMSHandler send msg back and indicate login success or fail
                    if (msg.getDetails().equals("Success")) {       //success
                        //if success login return some boolean variable that enable all methods that need login to be true to act
                        loggedIn = true;
                        getPin = false; //on login success, no need pin anymore
                        touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.LoggedIn, "Success")); //change screen to menu to select account
                        //send verification success notification to touchscreen display so that screen is changed
                    } else if (msg.getDetails().equals("Fail")) {   //fail
                        errorCount++;
                        if (errorCount >= 3) {      //if error PIN >= 3, retain card
                            log.info(id + ": enter wrong PIN three times, retain the card");
                            //jump to the page saying card is retained
                            //instruct card reader retain card
                            cardReaderMBox.send(new Msg(id, mbox, Msg.Type.CR_RetainCard, ""));
                            touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.Error, transaction + "_" + "Card Retained"));
                            allReset();
                        } else {        //situation that enter the wrong PIN for one or two times
                            //give error message
                            pin = "";
                            keypadMBox.send(new Msg(id, mbox, Msg.Type.Alert, ""));
                            touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.Error, transaction + "_" + "Wrong PIN\n\nPlease ensure you enter the right PIN"));
                        }
                    }
                    break;

                case GetAccount:        //send BAMSHandler msg and ask for the accounts info of the card
                    bamsThreadMBox.send(new Msg(id, mbox, Msg.Type.GetAccount, cardNum));
//				touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_SelectAccount, msg.getDetails()));
                    break;

                case ReceiveAccount:    //receive accounts info of specific card from BAMS
                    //if !operating account = "" && msg.getDetails() has no "/", return error
                    if (!selectedAcc.equals("") && !msg.getDetails().contains("/")) {       //only for money transfer at this moment
                        //this card has only one account and cannot do money transfer
                        touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.Error, transaction + "_" + "This card has only one account\n\nCannot do money transfer"));
                    } else {        //initial account selection at this moment
                        touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_SelectAccount, transaction + "_" + msg.getDetails()));
                    }
                    break;

                case Selected_Acc:      //receive input of selected account
                    if (transaction.equals("")) {       //choose which account to operate
                        selectedAcc = msg.getDetails();        //on logout please clear this value
                    } else {            //choose account to transfer money
                        if (!selectedAcc.equals("")) {
                            transferAcc = msg.getDetails();
                            getAmount = true;
                            //update touchdisplay
                            touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "Money Transfer_" + transferAcc));
                        }
                    }
                    break;

                case MoneyTransferResult:           //Receive result about money transfer from BAMS
                    log.info(id + ": Money Transfer from " + selectedAcc + " to " + transferAcc + ": $" + msg.getDetails());
                    amountTyped = msg.getDetails();
                    touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.MoneyTransferResult, transferAcc + "_" + msg.getDetails()));
                    break;

                case Dispense:          //Receive msg from BAMS that allow withdraw and dispense money
                    //may not have enough money in the account
                    log.info(id + ": Cash Dispense: $" + msg.getDetails());
                    amountTyped = msg.getDetails();
                    String amountDispense = denomDispenseCalculate(msg.getDetails());
                    DispenserSlotMBox.send(new Msg(id, mbox, Msg.Type.Denom_sum, amountDispense));        //process the notes to dispense
                    BuzzerMBox.send(new Msg(id, mbox, Msg.Type.Alert, "Dispenser Slot Opening!"));
                    dispenseTimerID = Timer.setTimer(id, mbox, 15000);
                    DispenserSlotMBox.send(new Msg(id, mbox, Msg.Type.Dispense, "OpenSlot")); //this is supposed to open slot
                    touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.Dispense, msg.getDetails()));
                    break;

                case DispenseFinish:
                    //stop dispense slot timer
                    Timer.cancelTimer(id, mbox, dispenseTimerID);
                    //update touch display
                    touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.DispenseFinish, msg.getDetails()));
                    break;

                case EnquiryResult:     //Account enquiry result from BAMS
                    amountTyped = msg.getDetails();
                    touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.EnquiryResult, msg.getDetails()));
                    break;

                case Denom_sum:         //receive cash from deposit slot
                    //receive money notes, update the money notes inventory
                    String[] denom = msg.getDetails().split(" ");
                    amountTyped = (Integer.parseInt(denom[0]) * 100 + Integer.parseInt(denom[1]) * 500 + Integer.parseInt(denom[2]) * 1000) + "";
                    log.info("CashDeposit Denominations: " + amountTyped);
                    Timer.cancelTimer(id, mbox, depositTimerID);//remove deposit slot timer
                    touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.Denom_sum, msg.getDetails()));
                    break;

                case DepositResult:     //receive deposit result from BAMS
                    amountTyped = msg.getDetails();
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
                    }else if (timerID ==dispenseTimerID){
                        dispenseTimerID = -1;
                        DepositSlotMBox.send(new Msg(id, mbox, Msg.Type.Deposit, "CloseSlot"));
                    }

                    break;

                case PollAck:
                    log.info("PollAck: " + msg.getDetails());
                    break;

                case Terminate:
                    quit = true;
                    break;

                case Error:     //receive error that cannot fix by itself
                    log.severe(id + ": " + msg);
                    break;

                case ErrorRedirect:         //redirect error page to another page
                    switch (transaction) {
                        case "":            //In enter PIN page
                            if (errorCount >= 3) {
                                touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "Welcome"));
                                allReset();
                            } else {
                                touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "PIN Required"));
                            }
                            break;

                        case "Cash Deposit":

                        case "Money Transfer":

                        case "Cash Withdrawal":

                        case "Account Balance Enquiry":
                            touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "MainMenu"));
                            halfRest();
                            break;
                    }
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
        if (msg.getDetails().compareToIgnoreCase("Cancel") == 0) {      //terminate whole transaction and eject card
            cardReaderMBox.send(new Msg(id, mbox, Msg.Type.CR_EjectCard, ""));
            touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "erasePIN"));
            //should be a screen showing thank you first
            allReset();        //if transaction canceled, reset pin variable
            touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "Welcome"));
        } else if (getPin) {        //stage of accepting PIN
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
                //"00" is not allowed in enter PIN
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
                        case "0":
                            pin += msg.getDetails();
                            touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "enterPIN"));
                            break;
                        default:
                            break;
                    }
                }
            }
        } else if (getAmount) {     //stage of accepting amount input, e.g. cash withdraw and money transfer
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
                if (amountTyped.equals("")) {       //prevent enter nothing
                    amountTyped = "0";
                }
                //look at which transaction it is
                if (transaction.equals("Cash Withdrawal")) {
                    //only amount that is divisible by 100 can be withdrawn
                    if (Integer.parseInt(amountTyped) % 100 == 0 && Integer.parseInt(amountTyped) > 0) {    //check if amount is divisible by 100 and larger than 0
                        //send bams withdraw request
                        bamsThreadMBox.send(new Msg(id, mbox, Msg.Type.CashWithdraw, cardNum + " " + selectedAcc + " " + amountTyped));
                    } else {
                        //return error and reject withdraw request
                        touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.Error, transaction + "_" + "Withdraw Amount should be divisible by 100"));
                    }
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
                //identify which transaction it is
                if (transaction.equals("Money Transfer")) {     //it is money transfer
                    touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TextTyped, transferAcc + "_" + msg.getDetails()));
                } else {        //it is cash withdrawal
                    touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TextTyped, msg.getDetails()));
                }
            }
        }
    } // processKeyPressed


    //------------------------------------------------------------
    // processMouseClicked
    private void processMouseClicked(Msg msg) {
        // *** process mouse click here!!! ***
        if (loggedIn) {
            switch (transaction) {
                case "":        //main menu page
                    transaction = msg.getDetails();

                    switch (transaction) {
                        case "Cash Deposit":  //deposit
                            //set transaction to true
                            depositTimerID = Timer.setTimer(id, mbox, 15000);
                            //change touch screen display to ask how much to deposit
                            touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, transaction));
                            DepositSlotMBox.send(new Msg(id, mbox, Msg.Type.Alert, ""));  //alert deposit slot

                            DepositSlotMBox.send(new Msg(id, mbox, Msg.Type.Deposit, "OpenSlot")); //open deposit slot
                            BuzzerMBox.send(new Msg(id, mbox, Msg.Type.Alert, "Deposit Slot Opened!"));

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
                            keypadMBox.send(new Msg(id, mbox, Msg.Type.Alert, ""));
                            break;
                        case "Account Balance Enquiry":
                            //set transaction to true
                            //check balance from BAMS
                            String enquiryDetails = cardNum + " " + selectedAcc;
                            bamsThreadMBox.send(new Msg(id, mbox, Msg.Type.AccountEnquiry, enquiryDetails));
                            break;
                        default:
                            //do nothing
                            transaction = "";
                            break;
                    }
                    break;

                case "Cash Deposit":
                    switch (msg.getDetails()) {
                        case "Confirm Amount":
                            //confirm the amount input and send bams deposit request
                            bamsThreadMBox.send(new Msg(id, mbox, Msg.Type.Deposit, cardNum + " " + selectedAcc + " " + amountTyped));
                            break;

                        case "Cancel":          //reinput amount, need to eject the money?
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
                    String status = "";
                    //see the resulting amount success or fail
                    if (amountTyped.equals("-1") || amountTyped.equals("")) {
                        status = "Fail";
                    } else {
                        status = "Success";
                    }
                    switch (msg.getDetails()) {
                        case "Continue Transaction and Print Advice":
                            //print advice
                            //reset the things and back to main menu
                            AdvicePrinterMBox.send(new Msg(id, mbox, Msg.Type.Print, selectedAcc + "_" + transaction + "_" + transferAcc + "_" + amountTyped + "_" + status));
                            BuzzerMBox.send(new Msg(id, mbox, Msg.Type.Alert, "Printed advice can be collected!"));

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
                            AdvicePrinterMBox.send(new Msg(id, mbox, Msg.Type.Print, selectedAcc + "_" + transaction + "_" + transferAcc + "_" + amountTyped + "_" + status));
                            BuzzerMBox.send(new Msg(id, mbox, Msg.Type.Alert, "Printed advice can be collected!"));
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

    //for end transaction use
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

    //for continue transaction and return to main menu use
    private void halfRest() {
        transaction = "";
        transferAcc = "";
        amountTyped = "";
        getPin = false;
        getAmount = false;
    }

    private String denomDispenseCalculate(String amount) {
        String denom100 = "";
        String denom500 = "";
        String denom1000 = "";
        int amountWithdraw = Integer.parseInt(amount);
        denom1000 = (amountWithdraw / 1000) + "";
        amountWithdraw -= Integer.parseInt(denom1000) * 1000;
        denom500 = (amountWithdraw / 500) + "";
        amountWithdraw -= Integer.parseInt(denom500) * 500;
        denom100 = (amountWithdraw / 100) + "";
        amountWithdraw -= Integer.parseInt(denom100) * 100;
        return "" + denom100 + " " + denom500 + " " + denom1000;
    }
} // CardReaderHandler
