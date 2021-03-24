package ATMSS;

import ATMSS.AdvicePrinterHandler.Emulator.AdvicePrinterEmulator;
import ATMSS.DepositSlotHandler.Emulator.DepositSlotEmulator;
import ATMSS.DispenserSlotHandler.Emulator.DispenserSlotEmulator;
import ATMSS.CardReaderHandler.Emulator.CardReaderEmulator;
import ATMSS.TouchDisplayHandler.Emulator.TouchDisplayEmulator;
import ATMSS.BuzzerHandler.Emulator.BuzzerEmulator;

import AppKickstarter.timer.Timer;
import ATMSS.ATMSS.ATMSS;

import ATMSS.KeypadHandler.KeypadHandler;
import ATMSS.CardReaderHandler.CardReaderHandler;
import ATMSS.KeypadHandler.Emulator.KeypadEmulator;
import ATMSS.TouchDisplayHandler.TouchDisplayHandler;
import ATMSS.DepositSlotHandler.DepositSlotHandler;
import ATMSS.DispenserSlotHandler.DispenserSlotHandler;
import ATMSS.AdvicePrinterHandler.AdvicePrinterHandler;
import ATMSS.BuzzerHandler.BuzzerHandler;
import ATMSS.BAMSHandler.bamsThreadHandler;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

//======================================================================
// ATMSSEmulatorStarter
public class ATMSSEmulatorStarter extends ATMSSStarter {
    //------------------------------------------------------------
    // main
    public static void main(String [] args) {
	new ATMSSEmulatorStarter().startApp();
    } // main


    //------------------------------------------------------------
    // startHandlers
    @Override
    protected void startHandlers() {
        Emulators.atmssEmulatorStarter = this;
        new Emulators().start();
    } // startHandlers


    //------------------------------------------------------------
    // Emulators
    public static class Emulators extends Application {
        private static ATMSSEmulatorStarter atmssEmulatorStarter;

	//----------------------------------------
	// start
        public void start() {
            launch();
	} // start

	//----------------------------------------
	// start
        public void start(Stage primaryStage) {
			Timer timer = null;
			ATMSS atmss = null;
			CardReaderEmulator cardReaderEmulator = null;
			KeypadEmulator keypadEmulator = null;
			TouchDisplayEmulator touchDisplayEmulator = null;
			DepositSlotEmulator DepositSlotEmulator = null;
			DispenserSlotEmulator DispenserSlotEmulator = null;
			AdvicePrinterEmulator AdvicePrinterEmulator = null;
			BuzzerEmulator BuzzerEmulator = null;

			bamsThreadHandler bamsThreadHandler =null;

			// create emulators
			try {
				timer = new Timer("timer", atmssEmulatorStarter);
				atmss = new ATMSS("ATMSS", atmssEmulatorStarter);
				cardReaderEmulator = new CardReaderEmulator("CardReaderHandler", atmssEmulatorStarter);
				keypadEmulator = new KeypadEmulator("KeypadHandler", atmssEmulatorStarter);
				touchDisplayEmulator = new TouchDisplayEmulator("TouchDisplayHandler", atmssEmulatorStarter);
				DepositSlotEmulator = new DepositSlotEmulator("DepositSlotHandler", atmssEmulatorStarter);
				DispenserSlotEmulator = new DispenserSlotEmulator("DispenserSlotHandler", atmssEmulatorStarter);
				AdvicePrinterEmulator = new AdvicePrinterEmulator("AdvicePrinterHandler", atmssEmulatorStarter);
				BuzzerEmulator = new BuzzerEmulator("BuzzerHandler", atmssEmulatorStarter);
				bamsThreadHandler = new bamsThreadHandler("BAMSThreadHandler", atmssEmulatorStarter);

				// start emulator GUIs
				keypadEmulator.start();
				cardReaderEmulator.start();
				touchDisplayEmulator.start();
				DepositSlotEmulator.start();
				DispenserSlotEmulator.start();
				AdvicePrinterEmulator.start();
				BuzzerEmulator.start();
			} catch (Exception e) {
				System.out.println("Emulators: start failed");
				e.printStackTrace();
				Platform.exit();
			}

			atmssEmulatorStarter.setTimer(timer);
			atmssEmulatorStarter.setATMSS(atmss);
			atmssEmulatorStarter.setCardReaderHandler(cardReaderEmulator);
			atmssEmulatorStarter.setKeypadHandler(keypadEmulator);
			atmssEmulatorStarter.setTouchDisplayHandler(touchDisplayEmulator);
			atmssEmulatorStarter.setDepositSlotHandler(DepositSlotEmulator);
			atmssEmulatorStarter.setDispenserSlotHandler(DispenserSlotEmulator);
			atmssEmulatorStarter.setAdvicePrinterHandler(AdvicePrinterEmulator);
			atmssEmulatorStarter.setBuzzerHandler(BuzzerEmulator);
			atmssEmulatorStarter.setBAMSThreadHandler(bamsThreadHandler);

	    // start threads
			new Thread(timer).start();
			new Thread(atmss).start();
			new Thread(cardReaderEmulator).start();
			new Thread(keypadEmulator).start();
			new Thread(touchDisplayEmulator).start();
			new Thread(DepositSlotEmulator).start();
			new Thread(DispenserSlotEmulator).start();
			new Thread(AdvicePrinterEmulator).start();
			new Thread(BuzzerEmulator).start();
			new Thread(bamsThreadHandler).start();
		} // start
    } // Emulators


    //------------------------------------------------------------
    //  setters
    private void setTimer(Timer timer) {
        this.timer = timer;
    }
    private void setATMSS(ATMSS atmss) {
        this.atmss = atmss;
    }
    private void setCardReaderHandler(CardReaderHandler cardReaderHandler) {
        this.cardReaderHandler = cardReaderHandler;
    }
    private void setKeypadHandler(KeypadHandler keypadHandler) {
        this.keypadHandler = keypadHandler;
    }
    private void setTouchDisplayHandler(TouchDisplayHandler touchDisplayHandler) {
        this.touchDisplayHandler = touchDisplayHandler;
    }

    private void setDepositSlotHandler(DepositSlotHandler depositSlotHandler){
    	this.DepositSlotHandler = depositSlotHandler;
	}

	private void setDispenserSlotHandler(DispenserSlotHandler dispenserSlotHandler){
    	this.DispenserSlotHandler = dispenserSlotHandler;
	}

	private void setAdvicePrinterHandler(AdvicePrinterHandler advicePrinterHandler){
    	this.AdvicePrinterHandler = advicePrinterHandler;
	}
	private void setBuzzerHandler(BuzzerHandler buzzerHandler){
    	this.BuzzerHandler = buzzerHandler;
	}

	private void setBAMSThreadHandler(bamsThreadHandler bamsThreadHandler){
    	this.bamsThreadHandler = bamsThreadHandler;
	}
} // ATMSSEmulatorStarter
