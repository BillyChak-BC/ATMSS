package ATMSS.TouchDisplayHandler.Emulator;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextBoundsType;

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
    public Label blankScreenLabel;
    public Label confirmationLabel;
    public Label menuLabel;
    public Rectangle noBtn;
    public Rectangle yesBtn;
    public PasswordField passwordField;
    public FlowPane menuListPane;
    public VBox vboxLeft;
    public VBox vboxRight;
    public String[] funcAry = {"Cash Deposit", "Money Transfer", "Cash Withdrawal", "Account Balance Enquiry", "five", "six", "seven", "eight", "nine", "ten"};

    private static boolean loggedIn = false;
    private static String selectedAcc = "";


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
                        touchDisplayMBox.send(new Msg(id, touchDisplayMBox, Msg.Type.TD_UpdateDisplay, targetLabel.getText()));
                    }
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
//        touchDisplayMBox.send(new Msg(id, touchDisplayMBox, Msg.Type.TD_UpdateDisplay, "MainMenu"));
        touchDisplayMBox.send(new Msg(id, touchDisplayMBox, Msg.Type.GetAccount, ""));
    }

    public void setLoginFalse() {
        loggedIn = false;
        //touchDisplayMBox.send(new Msg(id, touchDisplayMBox, Msg.Type.TD_UpdateDisplay, "BlankScreen"));
    }

    //regard welcomePage as a default layout
    public void welcomePage() {
        passwordField.setVisible(false);
        passwordField.setText("");
        blankScreenLabel.setText("Welcome to ATM system emulator\n\n\n\n\nPlease Insert ATM Card");
    }

    public void enterPINPage() {
        log.warning(id + ": At this moment, the program will give a lot of errors or no respond after sending the PIN for validation");
        blankScreenLabel.setText("Please Enter the PIN: \n\nPlease Press Enter Button after Entering PIN\n\nPlease Press Erase Button If You Type Wrong\n\nPlease Press Cancel If You Want to Cancel Transaction\n\n");
        passwordField.setVisible(true);
    }

    public void erasePIN(){
        passwordField.setText("");
    }

    public void changePIN() {
        //it is not a matter what text it is going to append on the touchscreen
        //the pin will be all masked
        if (passwordField.getText().length() < 9) {
            passwordField.appendText("0");
        }
    }

    public void mainMenuBox() {
        menuLabel.setText("Welcome back, "+ selectedAcc +"\n\nPlease select ...");
        int rectEachSide = 3;
        Rectangle[] rectLeft = new Rectangle[rectEachSide];
        Rectangle[] rectRight = new Rectangle[rectEachSide];
        for (int i = 0; i < rectEachSide; i++) {
            rectLeft[i] = rectangleInit(rectLeft[i]);
            rectRight[i] = rectangleInit(rectRight[i]);
        }
        Label[] leftLabel = new Label[rectEachSide];
        Label[] rightLabel = new Label[rectEachSide];
        //if less than 6 functions on one menu page, those no function part will be blank
        //if more than 6 functions on one menu page, rightLabel[2] will be for the next page
        //if only 6 functions on one menu page, rightLabel[2] will be normal
        int notSetFunc = funcAry.length;
        int menuPageNum = 0;
        int funcPerPage = rectEachSide * 2 - 1;
        for (int i = 0; i < rectEachSide; i++) {
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
//            leftLabel[i].setText("Number" + i * 2);
//            rightLabel[i].setText("Number" + (i * 2 + 1));
//            leftLabel[i].setOnMousePressed(this::td_mouseClick);
//            rightLabel[i].setOnMousePressed(this::td_mouseClick);
        }
        StackPane[] stack = new StackPane[rectEachSide * 2];
        for (int i = 0; i < stack.length; i++) {
            stack[i] = new StackPane();
            int j = i / 2;
            if (i % 2 == 0) {
                stack[i].getChildren().addAll(leftLabel[j], rectLeft[j]);
                vboxLeft.getChildren().add(stack[i]);
            } else {
                stack[i].getChildren().addAll(rightLabel[j], rectRight[j]);
                vboxRight.getChildren().add(stack[i]);
            }
            stack[i].setOnMousePressed(this::td_mouseClick);
        }
    }

    public void accountSelectGUI(String acc) {
        menuLabel.setText("Please select an account you want to operate");
        int maxAccNumEachSide = 2;
        String[] accounts = acc.split("/");
        int accNum = accounts.length;
        int remainingAcc = accNum;
        Rectangle[] leftAcc = new Rectangle[maxAccNumEachSide];
        Rectangle[] rightAcc = new Rectangle[maxAccNumEachSide];
        for (int i = 0; i < maxAccNumEachSide; i++) {
            leftAcc[i] = rectangleInit(leftAcc[i]);
            rightAcc[i] = rectangleInit(rightAcc[i]);
        }
        Label[] leftLabel = new Label[maxAccNumEachSide];
        Label[] rightLabel = new Label[maxAccNumEachSide];
        for (int i = 0; i < maxAccNumEachSide; i++) {
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
        StackPane[] stack = new StackPane[maxAccNumEachSide * 2];
        for (int i = 0; i < stack.length; i++) {
            stack[i] = new StackPane();
            int j = i / 2;
            if (i % 2 == 0) {
                stack[i].getChildren().addAll(leftLabel[j], leftAcc[j]);
                vboxLeft.getChildren().add(stack[i]);
            } else {
                stack[i].getChildren().addAll(rightLabel[j], rightAcc[j]);
                vboxRight.getChildren().add(stack[i]);
            }
            stack[i].setOnMousePressed(this::td_mouseClick);
        }
    }

    //    public void mainMenu() {
//        int numOfFun = 6;
//        Rectangle[] rectangles = new Rectangle[numOfFun];
//        for (int i = 0; i < rectangles.length; i++) {
//            rectangles[i] = rectangleInit(rectangles[i]);
//        }
//        Label[] labels = new Label[numOfFun];
//        for (int i = 0; i < labels.length; i++) {
//            labels[i] = labelInit(labels[i]);
//            labels[i].setText("Number " + i);
//        }
//        StackPane[] stack = new StackPane[numOfFun];
//        for (int i = 0; i < stack.length; i++) {
//            stack[i] = new StackPane();
//            stack[i].getChildren().addAll(rectangles[i], labels[i]);
//        }
//        menuListPane.getChildren().addAll(stack);
//    }
//
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

} // TouchDisplayEmulatorController