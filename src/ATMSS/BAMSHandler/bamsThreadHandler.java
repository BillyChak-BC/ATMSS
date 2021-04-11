package ATMSS.BAMSHandler;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.*;
import AppKickstarter.timer.Timer;

import java.io.IOException;
import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.logging.*;

public class bamsThreadHandler extends AppThread {
    protected MBox atmss = null;
    private BAMSHandler bams = null;
    private final String urlPrefix = "http://cslinux0.comp.hkbu.edu.hk/comp4107_20-21_grp11/";    //http://cslinux0.comp.hkbu.edu.hk/~comp4107/test/
    private static String credential = "";
    private int BAMSTimerID = -1;

    public bamsThreadHandler(String id, AppKickstarter appKickstarter) {
        super(id, appKickstarter);
        bams = new BAMSHandler(urlPrefix, initLogger());
    }

    public void run() {
        BAMSTimerID = Timer.setTimer(id, mbox, 15000);
        atmss = appKickstarter.getThread("ATMSS").getMBox();
        log.info(id + ": starting...");

        for (boolean quit = false; !quit; ) {
            Msg msg = mbox.receive();        //for this specific HW's MBox receive //this is based on the inheritance property from appthread and Handlers

            log.fine(id + ": message received: [" + msg + "].");
            if (!msg.getType().equals(Msg.Type.TimesUp)) {
                Timer.cancelTimer(id, mbox, BAMSTimerID);
                BAMSTimerID = Timer.setTimer(id, mbox, BAMSTimerID, 15000);
            }

            switch (msg.getType()) {
                case Verify:
                    try {
                        testLogin(bams, msg.getDetails());
                    } catch (BAMSInvalidReplyException e) {
                        log.severe(id + ": " + msg.getType() + ": " + e.getMessage());
                    } catch (IOException e) {
                        log.severe(id + ": Network connection problem occurs");
                        atmss.send(new Msg(id, mbox, Msg.Type.Error, "Network connection problem"));
                    }
                    break;

                case GetAccount:
                    try {
                        getAcc(msg.getDetails());
                    } catch (BAMSInvalidReplyException e) {
                        log.severe(id + ": " + msg.getType() + ": " + e.getMessage());
                    } catch (IOException e) {
                        log.severe(id + ": Network connection problem occurs");
                        atmss.send(new Msg(id, mbox, Msg.Type.Error, "Network connection problem"));
                    }
                    break;

                case Deposit:
                    try {
                        cashDeposit(msg.getDetails());
                    } catch (BAMSInvalidReplyException e) {
                        log.severe(id + ": " + msg.getType() + ": " + e.getMessage());
                    } catch (IOException e) {
                        log.severe(id + ": Network connection problem occurs");
                        atmss.send(new Msg(id, mbox, Msg.Type.Error, "Network connection problem"));
                    }
                    break;

                case CashWithdraw:
                    try {
                        cashWithdraw(msg.getDetails());
                    } catch (BAMSInvalidReplyException e) {
                        log.severe(id + ": " + msg.getType() + ": " + e.getMessage());
                    } catch (IOException e) {
                        log.severe(id + ": Network connection problem occurs");
                        atmss.send(new Msg(id, mbox, Msg.Type.Error, "Network connection problem"));
                    }
                    break;

                case MoneyTransferRequest:
                    try {
                        moneyTransfer(msg.getDetails());
                    } catch (BAMSInvalidReplyException e) {
                        log.severe(id + ": " + msg.getType() + ": " + e.getMessage());
                    } catch (IOException e) {
                        log.severe(id + ": Network connection problem occurs");
                        atmss.send(new Msg(id, mbox, Msg.Type.Error, "Network connection problem"));
                    }
                    break;

                case AccountEnquiry:
                    try {
                        accEnquiry(msg.getDetails());
                    } catch (BAMSInvalidReplyException e) {
                        log.severe(id + ": " + msg.getType() + ": " + e.getMessage());
                    } catch (IOException e) {
                        log.severe(id + ": Network connection problem occurs");
                        atmss.send(new Msg(id, mbox, Msg.Type.Error, "Network connection problem"));
                    }
                    break;

                case TimesUp:
                    StringTokenizer tokens = new StringTokenizer(msg.getDetails());
                    String msgtype = tokens.nextToken();
                    int timerID = Integer.parseInt(tokens.nextToken());
                    if (timerID == BAMSTimerID) {
                        BAMSTimerID = Timer.setTimer(id, mbox, 15000);
                        try {
                            networkPoll(bams);
                            atmss.send(new Msg(id, mbox, Msg.Type.BAMSAck, id + " is up!"));
                        } catch (BAMSInvalidReplyException e) {
                            //a network poll don't care what it replies
                            break;
                        } catch (IOException e) {
                            //only care whether it replies or not
                            log.severe(id + ": Network connection problem occurs");
                            atmss.send(new Msg(id, mbox, Msg.Type.Error, "Network connection problem"));
                        }
                    }
                    break;

                case Terminate:
                    quit = true;
                    break;

                default:
                    processMsg(msg);
                    break;
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

    //------------------------------------------------------------
    // testLogin
    protected void testLogin(BAMSHandler bams, String msg) throws BAMSInvalidReplyException, IOException {
        System.out.println(msg);
        StringTokenizer tokens = new StringTokenizer(msg);
        String cardnum = tokens.nextToken();
        String pin = tokens.nextToken();

        System.out.println("Login:");
        //String cred = bams.login("12345678-0", "456123789");
        credential = bams.login(cardnum, pin);    //login returns string //456123789
        System.out.println("cred: " + credential);
        System.out.println();
        if (credential.equals("ERROR")) {
            log.info(id + " : incorrect cardnum or pin!");
            atmss.send(new Msg(id, mbox, Msg.Type.LoggedIn, "Fail"));
        } else {
            log.info(id + " : successful login!");
            atmss.send(new Msg(id, mbox, Msg.Type.LoggedIn, "Success"));
        }
    } // testLogin

    protected void networkPoll(BAMSHandler bams) throws IOException, BAMSInvalidReplyException {
        String result = bams.login("0000-1000", "012387937");
    }


    //------------------------------------------------------------
    // testGetAcc
    static String testGetAcc(BAMSHandler bams, String cardNum) throws BAMSInvalidReplyException, IOException {

//        System.out.println("GetAcc:");
//        String accounts = bams.getAccounts("12345678-1", "cred-1");
//        System.out.println("accounts: " + accounts);
//        System.out.println();
        return bams.getAccounts(cardNum, credential);
    } // testGetAcc

    private void getAcc(String cardNum) throws IOException, BAMSInvalidReplyException {
        String result = testGetAcc(bams, cardNum);
        //error
        atmss.send(new Msg(id, mbox, Msg.Type.ReceiveAccount, result));
    }

    //------------------------------------------------------------
    // testWithdraw
    static int testWithdraw(BAMSHandler bams, String cardNo, String accNo, String amount) throws BAMSInvalidReplyException, IOException {
        return bams.withdraw(cardNo, accNo, credential, amount);
//        int outAmount = bams.withdraw("12345678-2", "111-222-332","cred-2", "109702");
//        System.out.println("outAmount: " + outAmount);
//        System.out.println();
    } // testWithdraw

    private void cashWithdraw(String details) throws IOException, BAMSInvalidReplyException {
        String[] info = details.split(" ");
        int outAmount = testWithdraw(bams, info[0], info[1], info[2]);
        atmss.send(new Msg(id, mbox, Msg.Type.Dispense, outAmount + ""));
    }

    //------------------------------------------------------------
    // testDeposit
    static double testDeposit(BAMSHandler bams, String cardNo, String accNo, String amount) throws BAMSInvalidReplyException, IOException {
        return bams.deposit(cardNo, accNo, credential, amount);
//        System.out.println("Deposit:");
//        double depAmount = bams.deposit("12345678-3", "111-222-333","cred-3", "109703");
//        System.out.println("depAmount: " + depAmount);
//        System.out.println();
    } // testDeposit

    private void cashDeposit(String details) throws IOException, BAMSInvalidReplyException {
        String[] info = details.split(" ");
        double result = testDeposit(bams, info[0], info[1], info[2]);
        atmss.send(new Msg(id, mbox, Msg.Type.DepositResult, result + ""));
    }


    //------------------------------------------------------------
    // testEnquiry
    static double testEnquiry(BAMSHandler bams, String cardNo, String accNo) throws BAMSInvalidReplyException, IOException {
        return bams.enquiry(cardNo, accNo, credential);
    } // testEnquiry

    private void accEnquiry(String details) throws IOException, BAMSInvalidReplyException {
        String[] info = details.split(" ");
        double result = testEnquiry(bams, info[0], info[1]);
        atmss.send(new Msg(id, mbox, Msg.Type.EnquiryResult, result + ""));
    }

    //------------------------------------------------------------
    // testTransfer
    static double testTransfer(BAMSHandler bams, String cardNo, String fromAcc, String toAcc, String amount) throws BAMSInvalidReplyException, IOException {
        return bams.transfer(cardNo, credential, fromAcc, toAcc, amount);
//        System.out.println("Transfer:");
//        double transAmount = bams.transfer("12345678-5", "cred-5","111-222-335", "11-222-336", "109705");
//        System.out.println("transAmount: " + transAmount);
//        System.out.println();
    } // testTransfer

    private void moneyTransfer(String details) throws IOException, BAMSInvalidReplyException {
        String[] info = details.split(" ");
        double result = testTransfer(bams, info[0], info[1], info[2], info[3]);
        atmss.send(new Msg(id, mbox, Msg.Type.MoneyTransferResult, result + ""));
    }


    //------------------------------------------------------------
    // initLogger
    static Logger initLogger() {
        // init our logger
        ConsoleHandler logConHdr = new ConsoleHandler();
        logConHdr.setFormatter(new TestBAMSHandler.LogFormatter());
        Logger log = Logger.getLogger("TestBAMSHandler");
        log.setUseParentHandlers(false);
        log.setLevel(Level.ALL);
        log.addHandler(logConHdr);
        logConHdr.setLevel(Level.ALL);
        return log;
    } // initLogger


    static class LogFormatter extends Formatter {
        //------------------------------------------------------------
        // format
        public String format(LogRecord rec) {
            Calendar cal = Calendar.getInstance();
            String str = "";

            // get date
            cal.setTimeInMillis(rec.getMillis());
            str += String.format("%02d%02d%02d-%02d:%02d:%02d ",
                    cal.get(Calendar.YEAR) - 2000,
                    cal.get(Calendar.MONTH) + 1,
                    cal.get(Calendar.DAY_OF_MONTH),
                    cal.get(Calendar.HOUR_OF_DAY),
                    cal.get(Calendar.MINUTE),
                    cal.get(Calendar.SECOND));

            // level of the log
            str += "[" + rec.getLevel() + "] -- ";

            // message of the log
            str += rec.getMessage();
            return str + "\n";
        } // format
    } // LogFormatter
}
