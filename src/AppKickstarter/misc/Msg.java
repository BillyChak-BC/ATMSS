package AppKickstarter.misc;


//======================================================================
// Msg
public class Msg {
    private String sender;
    private MBox senderMBox;
    private Type type;
    private String details;

    //------------------------------------------------------------
    // Msg
    /**
     * Constructor for a msg.
     * @param sender id of the msg sender (String)
     * @param senderMBox mbox of the msg sender
     * @param type message type
     * @param details details of the msg (free format String)
     */
    public Msg(String sender, MBox senderMBox, Type type, String details) {
	this.sender = sender;
	this.senderMBox = senderMBox;
	this.type = type;
	this.details = details;
    } // Msg


    //------------------------------------------------------------
    // getSender
    /**
     * Returns the id of the msg sender
     * @return the id of the msg sender
     */
    public String getSender()     { return sender; }


    //------------------------------------------------------------
    // getSenderMBox
    /**
     * Returns the mbox of the msg sender
     * @return the mbox of the msg sender
     */
    public MBox   getSenderMBox() { return senderMBox; }


    //------------------------------------------------------------
    // getType
    /**
     * Returns the message type
     * @return the message type
     */
    public Type   getType()       { return type; }


    //------------------------------------------------------------
    // getDetails
    /**
     * Returns the details of the msg
     * @return the details of the msg
     */
    public String getDetails()    { return details; }


    //------------------------------------------------------------
    // toString
    /**
     * Returns the msg as a formatted String
     * @return the msg as a formatted String
     */
    public String toString() {
	return sender + " (" + type + ") -- " + details;
    } // toString


    //------------------------------------------------------------
    // Msg Types
    /**
     * Message Types used in Msg.
     * @see Msg
     */
    public enum Type {
        /** Terminate the running thread */	Terminate,
	/** Generic error msg */		Error,
        /** Redirect Page After Error */    ErrorRedirect,
	/** Set a timer */			SetTimer,
	/** Set a timer */			CancelTimer,
	/** Timer clock ticks */		Tick,
	/** Time's up for the timer */		TimesUp,
	/** Health poll */			Poll,
	/** Health poll acknowledgement */	PollAck,
	/** Update Display */			TD_UpdateDisplay,
	/** Mouse Clicked */			TD_MouseClicked,
        /**Select Account*/         TD_SelectAccount,
	/** Card inserted */			CR_CardInserted,
	/** Card removed */			CR_CardRemoved,
	/** Eject card */			CR_EjectCard,
        /** Retain card */      CR_RetainCard,
	/** Key pressed */			KP_KeyPressed,
    /** Sending Cash Denominations */ Denom_sum, 
    /**Start Deposit */                Deposit, 
    /**Start Dispense*/               Dispense, 
    /** Send Advice from ATMSS*/        Advice,
    /** Print out Advice onto GUI*/     Print, 
    /**Alert Buzzer*/                   Alert, 
    /** alert components that login is successful*/ LoggedIn,
    /**alert components that logout is successful*/ LoggedOut,
     /**Verify cardNum and pin*/   Verify,
        /**Get card accounts*/          GetAccount,
        /**Receive card accounts*/      ReceiveAccount,
        /**Informs what is selected Acc*/   Selected_Acc,
        /** Receive Cash Deposit Result*/   DepositResult,
        /** Specific Account Enquiry*/      AccountEnquiry,
        /** Receive Account Enquiry Result*/        EnquiryResult,
        /** Send Cash Withdraw Request*/        CashWithdraw,
        /** Typed text*/            TextTyped,
        /** Send Money Transfer Request*/       MoneyTransferRequest,
        /** Receive Money Transfer Result*/     MoneyTransferResult,
        /** Cash Dispense has been taken */     DispenseFinish,
        /** Update denoms inventory */          DenomsInventoryUpdate,
    } // Type
} // Msg
