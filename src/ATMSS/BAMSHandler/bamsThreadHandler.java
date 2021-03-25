package ATMSS.BAMSHandler;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.*;

import java.io.IOException;
import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.logging.*;

public class bamsThreadHandler extends AppThread{
    protected MBox atmss = null;
    private BAMSHandler bams = null;
    private final String urlPrefix = "http://cslinux0.comp.hkbu.edu.hk/~comp4107/test/";
    private String credential = "";

    public bamsThreadHandler(String id, AppKickstarter appKickstarter) {
        super(id, appKickstarter);
        bams = new BAMSHandler(urlPrefix, initLogger());
    }

    public void run() {
        atmss = appKickstarter.getThread("ATMSS").getMBox();
        log.info(id + ": starting...");

        for (boolean quit = false; !quit;) {
            Msg msg = mbox.receive();		//for this specific HW's MBox receive //this is based on the inheritance property from appthread and Handlers

            log.fine(id + ": message received: [" + msg + "].");

            switch (msg.getType()) {
                //not needed because atmss does not poll bams
//                case Poll:
//                    atmss.send(new Msg(id, mbox, Msg.Type.PollAck, id + " is up!"));
//                    break;
                case Verify:
                    try {
                        testLogin(bams, msg.getDetails());
                    } catch (BAMSInvalidReplyException e) {

                    } catch (IOException e) {

                    }
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

    //------------------------------------------------------------
    // testLogin
    protected void testLogin(BAMSHandler bams, String msg) throws BAMSInvalidReplyException, IOException {
        System.out.println(msg);
        StringTokenizer tokens = new StringTokenizer(msg);
        String cardnum = tokens.nextToken();
        String pin = tokens.nextToken();

        System.out.println("Login:");
        String cred = bams.login("12345678-0", "456123789");
        //String cred = bams.login(cardnum, pin);    //login returns string
        System.out.println("cred: " + cred);
        System.out.println();
        if (cred.equals("ERROR")){
            log.info(id+" : incorrect cardnum or pin!");
            atmss.send(new Msg(id, mbox, Msg.Type.LoggedIn, "Fail"));
        }else{
            log.info(id+" : successful login!");
            atmss.send(new Msg(id, mbox, Msg.Type.LoggedIn, "Success"));
        }
    } // testLogin


    //------------------------------------------------------------
    // testGetAcc
    static void testGetAcc(BAMSHandler bams) throws BAMSInvalidReplyException, IOException {
        System.out.println("GetAcc:");
        String accounts = bams.getAccounts("12345678-1", "cred-1");
        System.out.println("accounts: " + accounts);
        System.out.println();
    } // testGetAcc


    //------------------------------------------------------------
    // testWithdraw
    static void testWithdraw(BAMSHandler bams) throws BAMSInvalidReplyException, IOException {
        System.out.println("Withdraw:");
        int outAmount = bams.withdraw("12345678-2", "111-222-332","cred-2", "109702");
        System.out.println("outAmount: " + outAmount);
        System.out.println();
    } // testWithdraw


    //------------------------------------------------------------
    // testDeposit
    static void testDeposit(BAMSHandler bams) throws BAMSInvalidReplyException, IOException {
        System.out.println("Deposit:");
        double depAmount = bams.deposit("12345678-3", "111-222-333","cred-3", "109703");
        System.out.println("depAmount: " + depAmount);
        System.out.println();
    } // testDeposit


    //------------------------------------------------------------
    // testEnquiry
    static void testEnquiry(BAMSHandler bams) throws BAMSInvalidReplyException, IOException {
        System.out.println("Enquiry:");
        double amount = bams.enquiry("12345678-4", "111-222-334","cred-4");
        System.out.println("amount: " + amount);
        System.out.println();
    } // testEnquiry


    //------------------------------------------------------------
    // testTransfer
    static void testTransfer(BAMSHandler bams) throws BAMSInvalidReplyException, IOException {
        System.out.println("Transfer:");
        double transAmount = bams.transfer("12345678-5", "cred-5","111-222-335", "11-222-336", "109705");
        System.out.println("transAmount: " + transAmount);
        System.out.println();
    } // testTransfer


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
