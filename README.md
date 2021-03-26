# README.md for Group11 Database
TABLE:
```
Account
cardNo   VARCHAR(255) NOT NULL
amount   DOUBLE           
username VARCHAR(255)
accNo    VARCHAR(255) NOT NULL PRIMARY_KEY
pin_code VARCHAR(255)
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
```