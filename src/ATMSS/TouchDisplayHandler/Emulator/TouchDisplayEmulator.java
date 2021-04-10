package ATMSS.TouchDisplayHandler.Emulator;

import ATMSS.ATMSSStarter;
import ATMSS.TouchDisplayHandler.TouchDisplayHandler;
import AppKickstarter.misc.Msg;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;


//======================================================================
// TouchDisplayEmulator
public class TouchDisplayEmulator extends TouchDisplayHandler {
    private final int WIDTH = 680;
    private final int HEIGHT = 520;
    private ATMSSStarter atmssStarter;
    private String id;
    private Stage myStage;
    private TouchDisplayEmulatorController touchDisplayEmulatorController;

    //------------------------------------------------------------
    // TouchDisplayEmulator
    public TouchDisplayEmulator(String id, ATMSSStarter atmssStarter) throws Exception {
        super(id, atmssStarter);
        this.atmssStarter = atmssStarter;
        this.id = id;
    } // TouchDisplayEmulator


    //------------------------------------------------------------
    // start
    public void start() throws Exception {
        Parent root;
        myStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        String fxmlName = "TouchDisplayEmulator.fxml";
        loader.setLocation(TouchDisplayEmulator.class.getResource(fxmlName));
        root = loader.load();
        touchDisplayEmulatorController = (TouchDisplayEmulatorController) loader.getController();
        touchDisplayEmulatorController.initialize(id, atmssStarter, log, this);
        myStage.initStyle(StageStyle.DECORATED);
        myStage.setScene(new Scene(root, WIDTH, HEIGHT));
        myStage.setTitle("Touch Display");
        myStage.setResizable(false);
        touchDisplayEmulatorController.welcomePage();
        myStage.setOnCloseRequest((WindowEvent event) -> {
            atmssStarter.stopApp();
            Platform.exit();
        });
        myStage.show();
    } // TouchDisplayEmulator


    //------------------------------------------------------------
    // handleUpdateDisplay
    protected void handleUpdateDisplay(Msg msg) {
        log.info(id + ": update display -- " + msg.getDetails());
        String[] msgDetails = msg.getDetails().split("_");

        switch (msgDetails[0]) {
            case "BlankScreen":

            case "PIN Required":

            case "enterPIN":

            case "Cash Withdrawal":

//            case "enterPIN":
//                touchDisplayEmulatorController.changePIN();
//                break;

            case "erasePIN":
                reloadStage("TouchDisplayEmulator.fxml", msgDetails[0]);
                break;

            case "Welcome":
                //reloadStage("TouchDisplayEmulator.fxml", "Welcome", "Welcome_" + denom100 + " " + denom500 + " " + denom1000)

            case "Money Transfer":
                //reloadStage("TouchDisplayEmulator.fxml", "Money Transfer", transfer account number)
                reloadStage("TouchDisplayEmulator.fxml", msgDetails[0], msgDetails[1]);
                break;

            case "MainMenu":
                reloadStage("TouchDisplayMainMenu.fxml", msgDetails[0]);
                break;

            case "Cash Deposit":

            case "Confirmation":
                reloadStage("TouchDisplayConfirmation.fxml", msgDetails[0]);
                break;

            default:
                log.severe(id + ": update display with unknown display type -- " + msg.getDetails());
                break;
        }
    } // handleUpdateDisplay

    protected void handleLogin() {
        super.handleLogin();
        touchDisplayEmulatorController.setLoginTrue();
    }

    protected void accountSelect(String acc) {
        super.accountSelect(acc);
        String[] details = acc.split("_");
        if (details[0].equals("")) {
            //account selection before main menu
            //reloadStage("TouchDisplayMainMenu.fxml", "Initial Account Select", list of accounts);
            reloadStage("TouchDisplayMainMenu.fxml", "Initial Account Select", details[1]);
        } else {
            //account select in money transfer
            //reloadStage("TouchDisplayMainMenu.fxml", "Money Transfer", list of accounts);
            reloadStage("TouchDisplayMainMenu.fxml", details[0], details[1]);
        }
    }

    protected void cashDeposit(String amount) {
        super.cashDeposit(amount);
        reloadStage("TouchDisplayConfirmation.fxml", "Cash Deposit with money", amount);
    }

    protected void DepositResult(String amount) {
        super.DepositResult(amount);
        reloadStage("TouchDisplayMainMenu.fxml", "Deposit Result", amount);
    }

    protected void moneyTransferResult(String details) {
        super.moneyTransferResult(details);
        //reloadStage("TouchDisplayMainMenu.fxml", "Money Transfer Finish", (transfer account number)_(amount transferred));
        reloadStage("TouchDisplayMainMenu.fxml", "Money Transfer Finish", details);
    }

    protected void cashDispense(String amount) {
        super.cashDispense(amount);
        reloadStage("TouchDisplayMainMenu.fxml", "Cash Dispense", amount);
    }

    protected void cashDispenseFinish(String amount) {
        super.cashDispenseFinish(amount);
        reloadStage("TouchDisplayMainMenu.fxml", "Cash Dispense Finish", amount);
    }

    protected void accountEnquiry(String amount) {
        super.accountEnquiry(amount);
        reloadStage("TouchDisplayMainMenu.fxml", "Account Enquiry", amount);
    }

    protected void amountFieldChange(String typed) {
        super.amountFieldChange(typed);
        if (TouchDisplayEmulatorController.getCurrentPage() == 5) {
            reloadStage("TouchDisplayEmulator.fxml", "Money Transfer", typed);
        } else if (TouchDisplayEmulatorController.getCurrentPage() == 6) {
            reloadStage("TouchDisplayEmulator.fxml", "Cash Withdrawal", typed);
        }
//        touchDisplayEmulatorController.changeAmount(typed);
    }

    protected void handleErrorPage(String details) {
        super.handleErrorPage(details);
        reloadStage("TouchDisplayEmulator.fxml", "Error", details);
    }

    //------------------------------------------------------------
    // reloadStage
    private void reloadStage(String fxmlFName, String detail) {
        reloadStage(fxmlFName, detail, "");
    }

    private void reloadStage(String fxmlFName, String page, String detail) {
        TouchDisplayEmulator touchDisplayEmulator = this;

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    log.info(id + ": loading fxml: " + fxmlFName);

                    Parent root;
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(TouchDisplayEmulator.class.getResource(fxmlFName));
                    root = loader.load();
                    touchDisplayEmulatorController = (TouchDisplayEmulatorController) loader.getController();
                    touchDisplayEmulatorController.initialize(id, atmssStarter, log, touchDisplayEmulator);
                    switch (fxmlFName) {
                        case "TouchDisplayEmulator.fxml":
                            switch (page) {
                                case "Welcome":
                                    //touchDisplayEmulatorController.welcomePage(Denoms);
                                    touchDisplayEmulatorController.welcomePage(detail);
                                    break;

//                                case "eraseAmount":
                                case "erasePIN":

                                case "PIN Required":
                                    touchDisplayEmulatorController.enterPINPage(false);
                                    break;

                                case "enterPIN":
                                    //button is pressed to enter PIN
                                    touchDisplayEmulatorController.enterPINPage(true);
                                    break;

                                case "Cash Withdrawal":
                                    //touchDisplayEmulatorController.cashWithdrawalPage(amount withdraw);
                                    touchDisplayEmulatorController.cashWithdrawalPage(detail);
                                    break;

                                case "Money Transfer":
                                    String[] details = detail.split("_");
                                    if (details.length > 1) {
                                        //touchDisplayEmulatorController.moneyTransferPage(transfer account, typed);
                                        touchDisplayEmulatorController.moneyTransferPage(details[0], details[1]);
                                    } else {
                                        //just after transfer account selection
                                        //touchDisplayEmulatorController.moneyTransferPage(transfer account, "");
                                        touchDisplayEmulatorController.moneyTransferPage(details[0], "");
                                    }
                                    break;

                                case "Error":
                                    touchDisplayEmulatorController.errorPage(detail);
                                    break;

                                default:
                                    break;
                            }
                            break;

                        case "TouchDisplayMainMenu.fxml":
                            switch (page) {
                                case "MainMenu":
                                    touchDisplayEmulatorController.mainMenuBox();
                                    break;

                                case "Account Enquiry":
                                    //touchDisplayEmulatorController.accountEnquiryMenu(account amount);
                                    touchDisplayEmulatorController.accountEnquiryMenu(detail);
                                    break;

                                case "Initial Account Select":
                                    //touchDisplayEmulatorController.accountSelectGUI(list of accounts, not for money transfer);
                                    touchDisplayEmulatorController.accountSelectGUI(detail, false);
                                    break;

                                case "Money Transfer":
                                    //touchDisplayEmulatorController.accountSelectGUI(list of accounts, for money transfer);
                                    touchDisplayEmulatorController.accountSelectGUI(detail, true);
                                    break;

                                case "Money Transfer Finish":
                                    //touchDisplayEmulatorController.moneyTransferFinish((transfer account number)_(transfer amount));
                                    touchDisplayEmulatorController.moneyTransferFinish(detail);
                                    break;

                                case "Cash Dispense":
                                    //touchDisplayEmulatorController.cashDispensePage(total cash dispense, money is not taken);
                                    touchDisplayEmulatorController.cashDispensePage(detail, false);
                                    break;

                                case "Cash Dispense Finish":
                                    //touchDisplayEmulatorController.cashDispensePage(total cash dispense, money is taken);
                                    touchDisplayEmulatorController.cashDispensePage(detail, true);
                                    break;

                                case "Deposit Result":
                                    //touchDisplayEmulatorController.cashDepositFinish(total amount saved);
                                    touchDisplayEmulatorController.cashDepositFinish(detail);
                                    break;

                                default:
                                    break;
                            }
                            break;

                        case "TouchDisplayConfirmation.fxml":
                            switch (page) {
                                case "Cash Deposit":
                                    touchDisplayEmulatorController.cashDepositPage();
                                    break;

                                case "Cash Deposit with money":
                                    //touchDisplayEmulatorController.cashDepositPage((number of $100 deposit) (number of $500 deposit) (number of $1000 deposit);
                                    touchDisplayEmulatorController.cashDepositPage(detail);
                                    break;

                                default:
                                    break;
                            }
                            break;

                        default:
                            break;
                    }
                    myStage.setScene(new Scene(root, WIDTH, HEIGHT));
                } catch (Exception e) {
                    log.severe(id + ": failed to load " + fxmlFName);
                    e.printStackTrace();
                }
            }
        });
    } // reloadStage
} // TouchDisplayEmulator
