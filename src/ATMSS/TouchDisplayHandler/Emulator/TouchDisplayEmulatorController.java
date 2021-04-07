package ATMSS.TouchDisplayHandler.Emulator;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

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
    public Label blankAmountLabel;
    public Label menuLabel;
    public Label menuTopLabel;
    public HBox buttonHBox;
    public VBox vboxLeft;
    public VBox vboxRight;
    public Label confirmationLabel;
    public Label confirmationInformationLabel;
    public HBox confirmationHBox;

    public String[] funcAry = {"Cash Deposit", "Money Transfer", "Cash Withdrawal", "Account Balance Enquiry", "five", "six", "seven", "eight", "nine", "ten"};

    private final Integer startTime = 4;
    private Integer countDown = startTime;
    private static boolean loggedIn = false;
    private static StringBuilder blankAmountStringBuild = new StringBuilder();
    private static String operatingAcc = "";
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
            //not sending x-y coord anymore, we just send text
            switch (mouseEvent.getSource().getClass().getSimpleName()) {
                case "StackPane":
                    StackPane targetPane = (StackPane) mouseEvent.getSource();
                    Label targetLabel = (Label) targetPane.getChildren().get(0);
                    Pattern accPattern = Pattern.compile("\\d{3}-\\d{3}-\\d{3}");
                    Matcher accMatcher = accPattern.matcher(targetLabel.getText());
                    if (accMatcher.matches()) {
                        if (currentPage == 2) {
                            operatingAcc = targetLabel.getText();
                            touchDisplayMBox.send(new Msg(id, touchDisplayMBox, Msg.Type.TD_UpdateDisplay, "MainMenu"));
                        }
                        touchDisplayMBox.send(new Msg(id, touchDisplayMBox, Msg.Type.Selected_Acc, targetLabel.getText()));
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

    public static int getCurrentPage() {
        return currentPage;
    }

    public void welcomePage() {
        currentPage = 0;
        loggedIn = false;
        operatingAcc = "";
        eraseText();
        blankTopLabel.setText("Welcome to ATM system emulator");
        blankScreenLabel.setText("Please Insert ATM Card");
    }

    public void enterPINPage(boolean enterPIN) {
        currentPage = 1;
        log.warning(id + ": At this moment, the program will give a lot of errors or no respond after sending the PIN for validation");
        blankTopLabel.setText("Please Enter the PIN:");
        blankScreenLabel.setText("Please Press Enter Button after Entering PIN\n\nPlease Press Erase Button If You Type Wrong\n\nPlease Press Cancel If You Want to Cancel Transaction\n\n");
        if (enterPIN) {
            if (blankAmountStringBuild.length() < 9) {
                blankAmountStringBuild.append("*");
            }
            blankAmountLabel.setText(blankAmountStringBuild.toString());
        } else {
            eraseText();
        }
    }

    public void eraseText(){
        blankAmountStringBuild.delete(0, blankAmountStringBuild.length());
        blankAmountLabel.setText("");
    }

    public void mainMenuBox() {
        blankAmountStringBuild.delete(0, blankAmountStringBuild.length());
        currentPage = 3;
        menuLabel.setText("Welcome back, "+ operatingAcc +"\n\nPlease select ...");
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
        String[] accounts = acc.split("/");
        if (!transfer) {        //initial account select
            currentPage = 2;
            menuLabel.setText("Please select an account you want to operate");
        } else {                //money transfer account select
            menuTopLabel.setText("Operating Account Number: " + operatingAcc);
            currentPage = 5;
            menuLabel.setText("Please select an account you want to transfer to");
            String[] remainingAccounts = new String[accounts.length - 1];
            int j = 0;
            for (String account : accounts) {
                if (!operatingAcc.equals(account)) {
                    remainingAccounts[j] = account;
                    j++;
                }
            }
            accounts = remainingAccounts;
            //if accounts.length <= 0 pop error (or do the operating account elimination in ATMSS)
        }
        //need to remove or disable the button for the operating account in money transfer part
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
            } else if (remainingAcc <= 0) {
                break;
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
        confirmationLabel.setText("Operating Account Number: " + operatingAcc);
        int buttonNum = 2;
        Rectangle[] rectangles = new Rectangle[buttonNum];
        Label[] labels = new Label[buttonNum];
        StackPane[] stackPanes = new StackPane[buttonNum];
        for (int i = 0; i < buttonNum; i++) {
            rectangles[i] = rectangleInit(rectangles[i]);
            labels[i] = labelInit(labels[i]);
            if (i % 2 == 0) {
                labels[i].setText("Cancel");
            } else {
                labels[i].setText("Confirm Amount");
            }
            stackPanes[i] = new StackPane();
            stackPanes[i].getChildren().addAll(labels[i], rectangles[i]);
            stackPanes[i].setOnMousePressed(this::td_mouseClick);
            confirmationHBox.getChildren().add(stackPanes[i]);
        }
        if (amount.equals("")) {
            stackPanes[0].setVisible(false);
            stackPanes[0].setDisable(true);
            stackPanes[1].setVisible(false);
            stackPanes[1].setDisable(true);
            confirmationInformationLabel.setText("Please insert money in cash deposit collector");
        } else {
            stackPanes[0].setVisible(true);
            stackPanes[0].setDisable(false);
            stackPanes[1].setVisible(true);
            stackPanes[1].setDisable(false);
            String[] amounts = amount.split(" ");
            int total100 = Integer.parseInt(amounts[0]) * 100;
            int total500 = Integer.parseInt(amounts[1]) * 500;
            int total1000 = Integer.parseInt(amounts[2]) * 1000;
            confirmationInformationLabel.setText("Number of $100 money notes: " + amounts[0] + "= $100 x " + amounts[0] + " = " + "$" + total100 + "\n" + "Number of $500 money notes: " + amounts[1] + "= $500 x " + amounts[1] + " = " + "$" + total500 + "\n" + "Number of $1000 money notes: " + amounts[2] + "= $1000 x " + amounts[2] + " = " + "$" + total1000 + "\n\n" + "Total amount: $" + (total100 + total500 + total1000));
        }
    }

    protected void cashDepositFinish(String amount) {
        currentPage = 4;
        menuTopLabel.setText("Operating Account Number: " + operatingAcc);
        menuLabel.setText("Total amount deposit to " + operatingAcc + ": $" + amount);
        transactionFinalPage();
    }

    protected void moneyTransferPage(String transferAcc, String typed) {
        currentPage = 5;
        if (!typed.equals("")) {
            blankAmountStringBuild.append(typed);
            blankAmountLabel.setText(blankAmountStringBuild.toString());
        } else {
            eraseText();
        }
        blankTopLabel.setText("Operating Account Number: " + operatingAcc +"\n\n Selected Transfer Account Number: " + transferAcc);
        blankScreenLabel.setText("Please enter the amount you want to transfer\n\nPlease press Enter button after entering the amount\n\nPlease press Erase button if you type wrong");
    }

    protected void moneyTransferFinish(String details) {
        currentPage = 5;
        String[] detail = details.split("_");
        menuTopLabel.setText("Operating Account Number: " + operatingAcc);
        menuLabel.setText("Total amount transferred from " + operatingAcc + " to " +detail[0] + ": $" + detail[1]);
        transactionFinalPage();
    }

    protected void cashWithdrawalPage(String amount) {
        currentPage = 6;
        if (!amount.equals("")) {
            blankAmountStringBuild.append(amount);
            blankAmountLabel.setText(blankAmountStringBuild.toString());
        } else {
            eraseText();
        }
        blankTopLabel.setText("Operating Account Number: " + operatingAcc);
        blankScreenLabel.setText("Please enter the amount you want to withdraw\n\nPlease press Enter button after entering the amount\n\nPlease press Erase button if you type wrong");
    }

    protected void cashDispensePage(String amount) {
        currentPage = 6;
        menuTopLabel.setText("Operating Account Number: " + operatingAcc);
        menuLabel.setText("Total amount dispensed: $" + amount);
        transactionFinalPage();
    }

    protected void accountEnquiryMenu(String amount) {
        currentPage = 7;
        menuTopLabel.setText("Operating Account Number: " + operatingAcc);
        menuLabel.setText("Amount in this account: $" + amount);
        transactionFinalPage();
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
                if (leftLabel[j].getText().equals("")) {
                    stack[i].setDisable(true);
                }
                vboxLeft.getChildren().add(stack[i]);
            } else {
                stack[i].getChildren().addAll(rightLabel[j], rightRect[j]);
                if (rightLabel[j].getText().equals("")) {
                    stack[i].setDisable(true);
                }
                vboxRight.getChildren().add(stack[i]);
            }
            stack[i].setOnMousePressed(this::td_mouseClick);
        }
    }

    private void transactionFinalPage() {
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

    protected void errorPage(String errorMsg) {
        blankTopLabel.setText("Error");
        blankScreenLabel.setText(errorMsg);
        countDown(errorMsg);
    }

    private void countDown(String details) {
        Timeline timeline = new Timeline();
        timeline.setCycleCount(5);
        if (timeline != null) {
            timeline.stop();
        }
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(1), event -> {
            countDown--;
            blankAmountLabel.setText(countDown.toString());
            if (countDown <= 0) {
                timeline.stop();
                switch (currentPage) {
                    case 1:
                        //return to enter PIN or card retention
                        if (details.equals("Card Retained")) {
                            touchDisplayMBox.send(new Msg(id, touchDisplayMBox, Msg.Type.TD_UpdateDisplay, "Welcome"));
                        } else {
                            touchDisplayMBox.send(new Msg(id, touchDisplayMBox, Msg.Type.TD_UpdateDisplay, "PIN Required"));
                        }
                        break;

                    case 3:

                    case 4:

                    case 5:

                    case 6:

                    case 7:
                        //return to main menu
                        touchDisplayMBox.send(new Msg(id, touchDisplayMBox, Msg.Type.TD_UpdateDisplay, "MainMenu"));
                        break;

                    default:
                        //error not resolved
//                        touchDisplayMBox.send(new Msg(id, touchDisplayMBox, Msg.Type.Error, details));
                        break;
                }
            }
        });
        timeline.getKeyFrames().add(keyFrame);
        timeline.playFromStart();
    }
} // TouchDisplayEmulatorController
