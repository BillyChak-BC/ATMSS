# README.md for Group11
Inside ATMStarter, there will be the src, out, etc, and .idea directories.
Besides this, there is .classpath file, .project file, realBuzzerSound.mp3,
ATMStarter.iml, as well as this README file.

Inside src, there is the AppKickstarter provided by Dr Yau, and is untouched
besides the Msg Type modifications permitted; and there is ATMSS.

Inside ATMSS, there is the .java files ATMSSEmulatorStarter and ATMSSStarter.
We then have the directories:
```
AdvicePrinterHandler,
ATMSS,
BAMSHandler,
BuzzerHandler,
CardReaderHandler,
DepositSlotHandler,
HWHandler,
KeypadHandler,
TouchDisplayHandler.
```
The Handler directories (besides HWHandler) follow the structure that inside,
there is the .java file for handler, and an Emulator directory that contains
the respective emulator, emulatorcontroller and .fxml file(s). Inside ATMSS
directory is ATMSS.java file.

Instructions:
```
click Build (next to Refactor and Run) and Build Project (Ctrl+F9). After that,
Run ATMSSEmulatorStarter.java. After all the GUIs pop up, you can insert a
card. This will prompt TouchDisplay to change screen to prompt for PIN as well
as Keypad to come to front (this won't happen if you minimized the GUIs).
Input the correct PIN, and the touchdisplay will login and allow you to
choose which account to do transactions on.

Choose an account, and TouchDisplay will go to the transaction main menu.
The only implemented functions are :
Deposit, Withdraw, Transfer and Account Balance Enquiry. Click on a function to
test it, after completing the transaction, it will prompt whether to:
continue transaction, or end transaction(also has options for printing advice).

Once done with testing the functions, click to end transaction. TouchDisplay
will return to main page, and the cardReader will eject card. After removing,
you can click the X button to terminate ATMSS system.
```

# README.md for Group11 Database
TABLE:
```
Account
cardNo   VARCHAR(255) NOT NULL
amount   DOUBLE
username VARCHAR(255)
accNo    VARCHAR(255) NOT NULL PRIMARY_KEY
FK: fk_Card_cardNo cardNo REFERENCES Card(cardNo)
/*need to add chkAmount constraint: check that the amount before insert or update is NOT less than 0*/

Card
cardNo   VARCHAR(255) NOT NULL PRIMARY_KEY
pin_code VARCHAR(255) NOT NULL
```
Trigger:
```
trigger1

| trigger1 | INSERT | ACCOUNT | begin if exists
(select * from Account where cardNo = New.cardNo having count(*) > 3)
then signal sqlstate '45000'
set message_text 'each card can only have 4 accounts';
end if; end | BEFORE | NULL | STRICT_TRANS_TABLES, NO_ENGINE_SUBSTITUTION |
comp4107_grp11@% | utf8 | utf8_general_ci | utf8_general_ci |

Description: Can only insert the record with same card number (cardNo) 4 times only,
will show error in the fifth time.

trigger2

| trigger2 | UPDATE | Account | begin if new.amount < 0 then signal sqlstate '45000' 
set message_text = 'not enough money to withdraw!!'; end if; end | BEFORE | NULL | 
STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION | comp4107_grp11@% | utf8 | utf8_general_ci | utf8_general_ci |

Description: Can't update the amount less than 0, otherwise will show error.
```
