package ATMSS.BAMSHandler;

public class BAMSMsg extends java.lang.Object{
    String msgType;
    BAMSMsg req;
    String url;

    public BAMSMsg(String msgType) {
        this.msgType = msgType;
    }

    public String getMsgType(){
        return this.msgType;
    }

    public String mkReq(String url,
                                 BAMSMsg req,
                                 Logger log) throws java.io.IOException{
        this.url = url;
        this.req = req;

        return;
    }
}

public class DepositReply extends BAMSMsg{
    String cardNo;
    String accNo;
    String cred;
    String amount;
    String depAmount;

    public DepositReply(String cardNo,
                        String accNo,
                        String cred,
                        String amount, String depAmount) {
        this.cardNo = cardNo;
        this.accNo = accNo;
        this.cred = cred;
        this.amount = amount;
        this.depAmount = depAmount;
    }

    public String getCardNo()

    public String getAccNo()

    public String getCred()

    public String getAmount()

    public String getDepAmount()

}

public class DepositReq extends BAMSMsg{
    String cardNo;
    String accNo;
    String cred;
    String amount;

    public DepositReq(String cardNo,
                      String accNo,
                      String cred,
                      String amount) {
        this.cardNo = cardNo;
        this.accNo = accNo;
        this.cred = cred;
        this.amount = amount;
    }

    public DepositReply mkReq(String url,
                              Logger log)
            throws BAMSInvalidReplyException,
            java.io.IOException{

    }

}

public class EnquiryReply extends BAMSMsg{
    String cardNo;
    String accNo;
    String cred;
    String amount;

    public EnquiryReply(String cardNo,
                      String accNo,
                      String cred,
                      String amount) {
        this.cardNo = cardNo;
        this.accNo = accNo;
        this.cred = cred;
        this.amount = amount;
    }

    public String getCardNo()

    public String getAccNo()

    public String getCred()

    public String getAmount()
}

public class EnquiryReq extends BAMSMsg{
    String cardNo;
    String accNo;
    String cred;

    public EnquiryReq(String cardNo,
                      String accNo,
                      String cred){
        this.cardNo = cardNo;
        this.accNo = accNo;
        this.cred = cred;
    }

    public EnquiryReply mkReq(String url,
                              Logger log)
            throws BAMSInvalidReplyException,
            java.io.IOException{

    }
}

public class GetAccReply extends BAMSMsg{
    String cardNo;
    String cred;
    String accounts;

    public GetAccReply(String cardNo,
                       String cred,
                       String accounts){
        this.cardNo = cardNo;
        this.accounts = accounts;
        this.cred = cred;
    }

    public String getCardNo()

    public String getCred()

    public String getAccounts()

}

public class GetAccReq extends BAMSMsg{
    String cardNo;
    String cred;

    public GetAccReq(String cardNo,
                     String cred){
        this.cardNo = cardNo;
        this.cred = cred;
    }

    public GetAccReply mkReq(String url,
                             Logger log)
            throws BAMSInvalidReplyException,
            java.io.IOException{}

}

public class LoginReply extends BAMSMsg{
    String cardNo;
    String cred;
    String pin;

    public LoginReply(String cardNo,
                      String pin,
                      String cred){
        this.cardNo = cardNo;
        this.cred = cred;
        this.pin = pin;
    }

    public String getCardNo()

    public String getPin()

    public String getCred()

}

public class LoginReq extends BAMSMsg{
    String cardNo;
    String pin;

    public LoginReq(String cardNo,
                    String pin){
        this.cardNo = cardNo;
        this.pin = pin;
    }

    public LoginReply mkReq(String url,
                            Logger log)
            throws BAMSInvalidReplyException,
            java.io.IOException{}

}

public class TransferReply extends BAMSMsg{
    String cardNo;
    String cred;
    String fromAcc;
    String toAcc;
    String amount;
    String transAmount;

    public TransferReply(String cardNo,
                         String cred,
                         String fromAcc,
                         String toAcc,
                         String amount,
                         String transAmount){
        this.cardNo = cardNo;
        this.cred = cred;
        this.fromAcc = fromAcc;
        this.toAcc = toAcc;
        this.amount = amount;
        this.transAmount = transAmount;
    }

    public String getCardNo()

    public String getCred()

    public String getFromAcc()

    public String getToAcc()

    public String getAmount()

    public String getTransAmount()

}

public class TransferReq extends BAMSMsg{
    String cardNo;
    String cred;
    String fromAcc;
    String toAcc;
    String amount;

    public TransferReq(String cardNo,
                       String cred,
                       String fromAcc,
                       String toAcc,
                       String amount){
        this.cardNo = cardNo;
        this.cred = cred;
        this.fromAcc = fromAcc;
        this.toAcc = toAcc;
        this.amount = amount;
    }

    public TransferReply mkReq(String url,
                               Logger log)
            throws BAMSInvalidReplyException,
            java.io.IOException{}
}

public class WithdrawReply extends BAMSMsg{
    String cardNo;
    String accNo;
    String cred;
    String amount;
    String outAmount;

    public WithdrawReply(String cardNo,
                         String accNo,
                         String cred,
                         String amount,
                         String outAmount){
        this.cardNo = cardNo;
        this.accNo = accNo;
        this.cred = cred;
        this.amount = amount;
        this.outAmount = outAmount;
    }

    public String getCardNo()

    public String getAccNo()

    public String getCred()

    public String getAmount()

    public String getOutAmount()

}

public class WithdrawReq extends BAMSMsg{
    String cardNo;
    String accNo;
    String cred;
    String amount;

    public WithdrawReq(String cardNo,
                       String accNo,
                       String cred,
                       String amount){
        this.cardNo = cardNo;
        this.accNo = accNo;
        this.cred = cred;
        this.amount = amount;
    }

    public WithdrawReply mkReq(String url,
                               Logger log)
            throws BAMSInvalidReplyException,
            java.io.IOException{}

}




