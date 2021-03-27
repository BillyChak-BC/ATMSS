package ATMSS.CardReaderHandler.Emulator;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;

import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;


//======================================================================
// CardReaderEmulatorController
public class CardReaderEmulatorController {
    private String id;
    private AppKickstarter appKickstarter;
    private Logger log;
    private CardReaderEmulator cardReaderEmulator;
    private MBox cardReaderMBox;
	public TextField cardNumField1;
	public TextField cardNumField2;
    public TextField cardStatusField;
    public TextArea cardReaderTextArea;
	public Button insertBtn;
	public Button removeBtn;


    //------------------------------------------------------------
    // initialize
    public void initialize(String id, AppKickstarter appKickstarter, Logger log, CardReaderEmulator cardReaderEmulator) {
        this.id = id;
        this.appKickstarter = appKickstarter;
		this.log = log;
		this.cardReaderEmulator = cardReaderEmulator;
		this.cardReaderMBox = appKickstarter.getThread("CardReaderHandler").getMBox();
    } // initialize


	//------------------------------------------------------------
	// textFieldPressed
	public void textFieldPress1(KeyEvent keyEvent) {
		// Auto Focus
		TextField tf = (TextField) keyEvent.getSource();
		if (tf.getLength() == 4) {
			cardNumField2.requestFocus();
		}
	}

	public void textFieldPress2(KeyEvent keyEvent) {
    	// Limit 4
		TextField tf = (TextField) keyEvent.getSource();
		if (tf.getLength() == 4) {
		}
		log.info(tf.getText());
	}


	//------------------------------------------------------------
    // buttonPressed
    public void buttonPressed(ActionEvent actionEvent) {
	Button btn = (Button) actionEvent.getSource();

	switch (btn.getText()) {
	    case "Card 1":
			String cardNum1 = appKickstarter.getProperty("CardReader.Card1");
			cardNumField1.setText(cardNum1.substring(0,4));
			cardNumField2.setText(cardNum1.substring(5,9));
	        break;

	    case "Card 2":
			String cardNum2 = appKickstarter.getProperty("CardReader.Card2");
			cardNumField1.setText(cardNum2.substring(0,4));
			cardNumField2.setText(cardNum2.substring(5,9));
		break;

	    case "Card 3":
			String cardNum3 = appKickstarter.getProperty("CardReader.Card3");
			cardNumField1.setText(cardNum3.substring(0,4));
			cardNumField2.setText(cardNum3.substring(5,9));
		break;

	    case "Reset":
			cardNumField1.setText("");
			cardNumField2.setText("");
		break;

		case "Insert Card":
			// if (cardNumField.getText().length() != 0) {
			String cardText;
			cardText = cardNumField1.getText() + "-" + cardNumField2.getText();
			cardReaderMBox.send(new Msg(id, cardReaderMBox, Msg.Type.CR_CardInserted, cardText));
			cardReaderTextArea.appendText("Sending " + cardText+"\n");
//		    cardStatusField.setText("Card Inserted");
			// }
			break;

		case "Remove Card":
			if (cardStatusField.getText().compareTo("Card Ejected") == 0) {
				cardText = cardNumField1.getText() + "-" + cardNumField2.getText();

				cardReaderTextArea.appendText("Removing card\n");
				cardReaderMBox.send(new Msg(id, cardReaderMBox, Msg.Type.CR_CardRemoved, cardText));
			}
			break;


		default:
	        log.warning(id + ": unknown button: [" + btn.getText() + "]");
		break;
	}
    } // buttonPressed


    //------------------------------------------------------------
    // updateCardStatus
	public void updateCardStatus(String status) {
		cardStatusField.setText(status);
		if (status.compareTo("Card Inserted") == 0) {
			insertBtn.setDisable(true);
		} else if (status.compareTo("Card Ejected") == 0) {
			removeBtn.setDisable(false);
		} else if (status.compareTo("Card Reader Empty") == 0) {
			insertBtn.setDisable(false);
			removeBtn.setDisable(true);
		}
	} // updateCardStatus

	public void clearCardNum(){
    	cardNumField.setText("");
	}


    //------------------------------------------------------------
    // appendTextArea
    public void appendTextArea(String status) {
	cardReaderTextArea.appendText(status+"\n");
    } // appendTextArea
} // CardReaderEmulatorController
