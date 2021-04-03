package ATMSS.TouchDisplayHandler.Emulator;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


//======================================================================
// TouchDisplayEmulatorController
public class TouchDisplayEmulatorController {
    private String id;
    private AppKickstarter appKickstarter;
    private Logger log;
    private TouchDisplayEmulator touchDisplayEmulator;
    private MBox touchDisplayMBox;
    public Label blankTopLabel;
    public Label blankScreenLabel;
    public PasswordField passwordField;
    public TextField blankAmountField;
    public Label menuLabel;
    public Label menuTopLabel;
    public HBox buttonHBox;
    public VBox vboxLeft;
    public VBox vboxRight;
    public Label confirmationLabel;
    public Label confirmationInformationLabel;
    public Rectangle noBtn;
    public Rectangle yesBtn;
    public Label noLabel;
    public Label yesLabel;
    public String[] funcAry = {"Cash Deposit", "Money Transfer", "Cash Withdrawal", "Account Balance Enquiry", "five", "six", "seven", "eight", "nine", "ten"};

    private static boolean loggedIn = false;
    private static String selectedAcc = "";
    private static int currentPage = 0;         //0: welcome, 1: enter PIN, 2: initial account select, 3: main menu, 4: cash deposit, 5: money transfer, 6: cash withdraw, 7: account enquiry


    //------------------------------------------------------------
    // initialize
    public void initialize(String id, AppKickstarter appKickstarter, Logger log, TouchDisplayEmulator touchDisplayEmulator) {
        this.id = id;
        this.appKickstarter = appKickstarter;
        this.log = log;
        this.touchDisplayEmulator = touchDisplayEmulator;
        this.touchDisplayMBox = appKickstarter.getThread("TouchDisplayHandler").getMBox();
    } // initialize


    //------------------------------------------------------------
    // td_mouseClick
    public void td_mouseClick(MouseEvent mouseEvent) {
        int x = (int) mouseEvent.getX();
        int y = (int) mouseEvent.getY();

        log.fine(id + ": mouse clicked: -- (" + x + ", " + y + ")");
        //touchDisplayMBox.send(new Msg(id, touchDisplayMBox, Msg.Type.TD_MouseClicked, x + " " + y));
        if (loggedIn) {
            //use Java Switch to send diff message types depending on x-y coord
            switch (mouseEvent.getSource().getClass().getSimpleName()) {
                case "StackPane":
                    StackPane targetPane = (StackPane) mouseEvent.getSource();
                    Label targetLabel = (Label) targetPane.getChildren().get(0);
                    Pattern accPattern = Pattern.compile("\\d{3}-\\d{3}-\\d{3}");
                    Matcher accMatcher = accPattern.matcher(targetLabel.getText());
                    if (accMatcher.matches()) {
                        selectedAcc = targetLabel.getText();
                        touchDisplayMBox.send(new Msg(id, touchDisplayMBox, Msg.Type.TD_UpdateDisplay, "MainMenu"));
                        touchDisplayMBox.send(new Msg(id, touchDisplayMBox, Msg.Type.Selected_Acc, selectedAcc));
                    } else {
                        touchDisplayMBox.send(new Msg(id, touchDisplayMBox, Msg.Type.TD_MouseClicked, targetLabel.getText()));
                    }
                    break;
                default:
                    touchDisplayMBox.send(new Msg(id, touchDisplayMBox, Msg.Type.TD_MouseClicked, x + " " + y + " Logged In: " + loggedIn));
                    break;
            }

        } else {
            touchDisplayMBox.send(new Msg(id, touchDisplayMBox, Msg.Type.TD_MouseClicked, x + " " + y + " Logged In: " + loggedIn));
        }
    } // td_mouseClick

    public boolean getLoggedIn() {
        return loggedIn;
    }

    public void setLoginTrue() {
        loggedIn = true;
        touchDisplayMBox.send(new Msg(id, touchDisplayMBox, Msg.Type.GetAccount, ""));
    }

    public void setLoginFalse() {
        loggedIn = false;
        //touchDisplayMBox.send(new Msg(id, touchDisplayMBox, Msg.Type.TD_UpdateDisplay, "BlankScreen"));
    }

    public void welcomePage() {
        currentPage = 0;
        passwordField.setVisible(false);
        passwordField.setText("");
        blankAmountField.setVisible(false);
        blankAmountField.setText("");
        blankScreenLabel.setText("Welcome to ATM system emulator\n\n\n\n\nPlease Insert ATM Card");
    }

    public void enterPINPage() {
        currentPage = 1;
        log.warning(id + ": At this moment, the program will give a lot of errors or no respond after sending the PIN for validation");
        blankTopLabel.setText("Please Enter the PIN:");
        blankScreenLabel.setText("Please Press Enter Button after Entering PIN\n\nPlease Press Erase Button If You Type Wrong\n\nPlease Press Cancel If You Want to Cancel Transaction\n\n");
        passwordField.setVisible(true);
    }

    public void eraseText(){
        passwordField.setText("");
        blankAmountField.setText("");
    }

    public void changePIN() {
        //it is not a matter what text it is going to append on the touchscreen
        //the pin will be all masked
        if (passwordField.getText().length() < 9) {
            passwordField.appendText("0");
        }
    }

    public void changeAmount(String typed) {
        if (blankAmountField.getText().equals("0")) {
            blankAmountField.setText(typed);
        } else {
            blankAmountField.appendText(typed);
        }
    }

    public void mainMenuBox() {
        currentPage = 3;
        menuLabel.setText("Welcome back, "+ selectedAcc +"\n\nPlease select ...");
        mainMenuReset();
        int rectEachSide = 3;
        Rectangle[] rectLeft = new Rectangle[rectEachSide];
        Rectangle[] rectRight = new Rectangle[rectEachSide];
        Label[] leftLabel = new Label[rectEachSide];
        Label[] rightLabel = new Label[rectEachSide];
        //if less than 6 functions on one menu page, those no function part will be blank
        //if more than 6 functions on one menu page, rightLabel[2] will be for the next page
        //if only 6 functions on one menu page, rightLabel[2] will be normal
        int notSetFunc = funcAry.length;
        int menuPageNum = 0;
        int funcPerPage = rectEachSide * 2 - 1;
        for (int i = 0; i < rectEachSide; i++) {
            rectLeft[i] = rectangleInit(rectLeft[i]);
            rectRight[i] = rectangleInit(rectRight[i]);
            leftLabel[i] = labelInit(leftLabel[i]);
            rightLabel[i] = labelInit(rightLabel[i]);
            //this does not include more than 6 cases
            if (notSetFunc > 2 && i == rectEachSide - 1) {      //if more than 6, not finish
                leftLabel[i].setText(funcAry[i * 2 + menuPageNum * funcPerPage]);
                rightLabel[i].setText("Next Page");
                menuPageNum++;      //may change later
            } else if (notSetFunc > 1) {            //normal
                leftLabel[i].setText(funcAry[i * 2 + menuPageNum * funcPerPage]);
                rightLabel[i].setText(funcAry[i * 2 + 1 + menuPageNum * funcPerPage]);
                notSetFunc -= 2;
            } else if (notSetFunc > 0) {            //less than 6
                leftLabel[i].setText(funcAry[i * 2]);
                notSetFunc--;
            }
        }
        menuStackPaneSetting(rectLeft, rectRight, leftLabel, rightLabel, rectEachSide * 2);
    }

    public void accountSelectGUI(String acc, boolean transfer) {
        if (!transfer) {
            currentPage = 2;
            menuLabel.setText("Please select an account you want to operate");
        } else {
            currentPage = 5;
            menuLabel.setText("Please select an account you want to transfer to");
        }
        String[] accounts = acc.split("/");
        int accNum = accounts.length;
        int remainingAcc = accNum;
        int maxAccNumEachSide = 2;
        Rectangle[] leftAcc = new Rectangle[maxAccNumEachSide];
        Rectangle[] rightAcc = new Rectangle[maxAccNumEachSide];
        Label[] leftLabel = new Label[maxAccNumEachSide];
        Label[] rightLabel = new Label[maxAccNumEachSide];
        for (int i = 0; i < maxAccNumEachSide; i++) {
            leftAcc[i] = rectangleInit(leftAcc[i]);
            rightAcc[i] = rectangleInit(rightAcc[i]);
            leftLabel[i] = labelInit(leftLabel[i]);
            rightLabel[i] = labelInit(rightLabel[i]);
            if (remainingAcc == 1) {
                leftLabel[i].setText(accounts[i * 2]);
                remainingAcc--;
            } else {
                leftLabel[i].setText(accounts[i * 2]);
                rightLabel[i].setText(accounts[i * 2 + 1]);
                remainingAcc -= 2;
            }
        }
        menuFourButtons(leftAcc, rightAcc, leftLabel, rightLabel);
    }

    public void cashDepositPage() {
        cashDepositPage("");
    }

    protected void cashDepositPage(String amount) {
        currentPage = 4;
        confirmationLabel.setText("Operating Account Number: " + selectedAcc);
        yesLabel.setPrefSize(yesBtn.getWidth(), yesBtn.getHeight());
        noLabel.setPrefSize(noBtn.getWidth(), noBtn.getHeight());
        if (amount.equals("")) {
            yesBtn.setVisible(false);
            noBtn.setVisible(false);
            yesLabel.setVisible(false);
            noLabel.setVisible(false);
            confirmationInformationLabel.setText("Please insert money in cash deposit collector");
        } else {
            yesLabel.setVisible(true);
            noLabel.setVisible(true);
            yesBtn.setVisible(true);
            noBtn.setVisible(true);
            yesLabel.setText("Confirm Amount");     //after deposit success, jump to deposit success page (main menu template with four buttons)
            noLabel.setText("Cancel");
            String[] amounts = amount.split(" ");
            int total100 = Integer.parseInt(amounts[0]) * 100;
            int total500 = Integer.parseInt(amounts[1]) * 500;
            int total1000 = Integer.parseInt(amounts[2]) * 1000;
            confirmationInformationLabel.setText("Number of $100 money notes: " + amounts[0] + "= $100 x " + amounts[0] + " = " + "$" + total100 + "\n" + "Number of $500 money notes: " + amounts[1] + "= $500 x " + amounts[1] + " = " + "$" + total500 + "\n" + "Number of $1000 money notes: " + amounts[2] + "= $1000 x " + amounts[2] + " = " + "$" + total1000 + "\n\n" + "Total amount: $" + (total100 + total500 + total1000));
        }
    }

    protected void cashWithdrawalPage() {
        currentPage = 6;
        passwordField.setVisible(false);
        passwordField.setText("");
        blankAmountField.setVisible(true);
        blankAmountField.setText("0");
        blankTopLabel.setText("Operating Account Number: " + selectedAcc);
        blankScreenLabel.setText("Please enter the amount you want to withdraw\n\nPlease press Enter button after entering the amount\n\nPlease press Erase button if you type wrong");
    }

    protected void accountEnquiryMenu(String amount) {
        currentPage = 7;
        menuTopLabel.setText("Operating Account Number: " + selectedAcc);
        menuLabel.setText("Amount in this account: $" + amount);
        String[] labelText = {"Continue Transaction and Print Advice", "End Transaction and Print Advice", "Continue Transaction", "End Transaction"};
        int maxEachSide = 2;
        Rectangle[] leftButtons = new Rectangle[maxEachSide];
        Rectangle[] rightButtons = new Rectangle[maxEachSide];
        Label[] leftLabels = new Label[maxEachSide];
        Label[] rightLabels = new Label[maxEachSide];
        for (int i = 0; i < maxEachSide; i++) {
            leftButtons[i] = rectangleInit(leftButtons[i]);
            rightButtons[i] = rectangleInit(rightButtons[i]);
            leftLabels[i] = labelInit(leftLabels[i]);
            rightLabels[i] = labelInit(rightLabels[i]);
            leftLabels[i].setText(labelText[i * 2]);
            rightLabels[i].setText(labelText[i * 2 + 1]);
        }
        menuFourButtons(leftButtons, rightButtons, leftLabels, rightLabels);
    }

    private Rectangle rectangleInit(Rectangle target) {
        target = new Rectangle();
        target.setStroke(Color.BLACK);
        target.setFill(Color.TRANSPARENT);
        target.setArcWidth(5);
        target.setArcHeight(5);
        target.setWidth(320);
        target.setHeight(100);
        return target;
    }

    private Label labelInit(Label target) {
        target = new Label();
        target.setWrapText(true);
        target.setAlignment(Pos.CENTER);
        target.setMaxSize(320, 100);
        target.setTextAlignment(TextAlignment.CENTER);
        target.setPrefSize(320, 100);
        target.setFont(new Font(20));
        return target;
    }

    private void mainMenuReset() {
        buttonHBox.setPrefHeight(300);
        menuLabel.setPrefHeight(130);
        menuLabel.setAlignment(Pos.TOP_CENTER);
    }

    private void menuFourButtons(Rectangle[] leftRect, Rectangle[] rightRect, Label[] leftLabel, Label[] rightLabel) {
        menuLabel.setAlignment(Pos.CENTER);
        menuLabel.setPrefHeight(230);
        buttonHBox.setPrefHeight(200);
        menuStackPaneSetting(leftRect, rightRect, leftLabel, rightLabel, 4);
    }

    private void menuStackPaneSetting(Rectangle[] leftRect, Rectangle[] rightRect, Label[] leftLabel, Label[] rightLabel, int numButton) {
        StackPane[] stack = new StackPane[numButton];
        for (int i = 0; i < stack.length; i++) {
            stack[i] = new StackPane();
            int j = i / 2;
            if (i % 2 == 0) {
                stack[i].getChildren().addAll(leftLabel[j], leftRect[j]);
                vboxLeft.getChildren().add(stack[i]);
            } else {
                stack[i].getChildren().addAll(rightLabel[j], rightRect[j]);
                vboxRight.getChildren().add(stack[i]);
            }
            stack[i].setOnMousePressed(this::td_mouseClick);
        }
    }

} // TouchDisplayEmulatorController