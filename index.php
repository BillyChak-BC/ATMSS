<?php
$req = json_decode($_POST["BAMSReq"], false); //$_POST passes BAMSReq variable and is json_decoded into req variable.
$servername = "cslinux0.comp.hkbu.edu.hk";
$dbname = "comp4107_grp11";
$accname = "comp4107_grp11";
$password = "646586";

//create connection
$conn = new mysqli($servername, $accname, $password, $dbname);  //server, user, pw, db

if ($conn->connect_error){
  die("Connection failed: " . $conn->connect_error);
}
if (!mysqli_select_db($conn, $dbname)) {
  die("Uh oh, couldn't select database $dbname");
}

if (strcmp($req->msgType, "LoginReq") === 0) {  //complete
  $cardNo = $conn -> real_escape_string($req->cardNo);
  $pin = $conn -> real_escape_string($req->pin);
  $query = "SELECT * FROM Card WHERE cardNo='$cardNo' AND pin_code = '$pin'";//
  $result = $conn->query($query);

  $reply->msgType = "LoginReply";
  $reply->cardNo = $req->cardNo;
  $reply->pin = $req->pin;

  if ($result->num_rows > 0){
    $reply->cred = substr(base64_encode(mt_rand()), 0, 15); //unique random string generator. source: https://stackoverflow.com/a/6826161
  }else{
    $reply->cred = "ERROR";
  }

} else if (strcmp($req->msgType, "GetAccReq") === 0) {  //complete
  $cardNo = $conn -> real_escape_string($req->cardNo);
  $cred = $conn -> real_escape_string($req->cred);
  $query = "SELECT accNo FROM Account WHERE cardNo='$cardNo'";//
  $result = $conn->query($query);

  $reply->msgType = "GetAccReply";
  $reply->cardNo = $req->cardNo;
  $reply->cred = $req->cred;

  if(strcmp($cred, "ERROR") != 0){
    $value = "";
    $current = 1;
    while($row = $result -> fetch_assoc()){
      if ($current === 1){
        $value .= $row['accNo'];
      }else{
        $value .= "/" . $row['accNo'];
      }
      $current++;
    }
    $reply->accounts = $value;  //"111-222-333/111-222-334/111-222-335/111-222-336"
  }

} else if (strcmp($req->msgType, "WithdrawReq") === 0) {  //complete-ish
  $cardNo = $conn -> real_escape_string($req->cardNo);
  $accNo = $conn -> real_escape_string($req->accNo);
  $cred = $conn -> real_escape_string($req->cred);
  $amount = $conn -> real_escape_string($req->amount);

  $reply->msgType = "WithdrawReply";
  $reply->cardNo = $req->cardNo;
  $reply->accNo = $req->accNo;
  $reply->cred = $req->cred;
  $reply->amount = $req->amount;

  if (strcmp($cred, "ERROR") != 0){
    $sql = "UPDATE Account SET amount = amount - '$amount' WHERE cardNo='$cardNo' AND accNo='$accNo'";
    if ($conn->query(sql) === TRUE){
      $reply->outAmount = $req->amount;
    }else{    //if sql withdraw error (ie negative balance return an error);
      $reply->outAmount = -1;
    }
  }else{//if credential error, but somehow a user can access the transactions return an error
    $reply->outAmount = -1;
  }

} else if (strcmp($req->msgType, "DepositReq") === 0) {   //complete-ish
  $cardNo = $conn -> real_escape_string($req->cardNo);
  $accNo = $conn -> real_escape_string($req->accNo);
  $cred = $conn -> real_escape_string($req->cred);
  $amount = $conn -> real_escape_string($req->amount);

  $reply->msgType = "DepositReply";
  $reply->cardNo = $cardNo;
  $reply->accNo = $accNo;
  $reply->amount = $amount;
  $reply->cred = $cred;

  if(strcmp($cred, "ERROR") != 0){
    $sql = "UPDATE Account SET amount = amount + '$amount' WHERE cardNo='$cardNo' AND accNo='$accNo'";
    /*update amount query*/
    if ($conn->query($sql) === TRUE){
      $reply->depAmount = $amount;
    }else{
      $reply->depAmount =-1;
    }
  }else{
    $reply->depAmount = -1;
  }
} else if (strcmp($req->msgType, "EnquiryReq") === 0) {//work in progress
  $cardNo = $conn -> real_escape_string($req->cardNo);
  $accNo = $conn -> real_escape_string($req->accNo);
  $cred = $conn -> real_escape_string($req->cred);

  $reply->msgType = "EnquiryReply";
  $reply->cardNo = $req->cardNo;
  $reply->accNo = $req->accNo;
  $reply->cred = $req->cred;

  if(strcmp($cred, "ERROR") != 0){
    $sql = "SELECT * FROM Account WHERE cardNo='$cardNo' AND accNo='$accNo'";
    $result = $conn->query($sql);
    if ($result->num_rows > 0){
      $row = $result -> fetch_assoc();
      $reply->amount = $row['amount'];
    }else{
      $reply->amount = -1;
    }
  }else{
    $reply->amount = -1;
  }


} else if (strcmp($req->msgType, "TransferReq") === 0) {//work in progress
  $cardNo = $conn -> real_escape_string($req->cardNo);
  $fromAcc = $conn -> real_escape_string($req->fromAcc);
  $toAcc = $conn -> real_escape_string($req->toAcc);
  $cred = $conn -> real_escape_string($req->cred);
  $amount = $conn -> real_escape_string($req->amount);

  $reply->msgType = "TransferReply";
  $reply->cardNo = $req->cardNo;
  $reply->cred = $req->cred;
  $reply->fromAcc = $req->fromAcc;
  $reply->toAcc = $req->toAcc;
  $reply->amount = $req->amount;

  if(strcmp($cred, "ERROR") != 0){
    $sql = "UPDATE Account SET amount=amount-'$amount' WHERE cardNo='$cardNo' AND accNo='$fromAcc';";
    $sql .= "UPDATE Account SET amount=amount+'$amount' WHERE cardNo='$cardNo' AND accNo='$toAcc'";

    if($conn->multi_query($sql)){
      $reply->transAmount = $req->amount;
    }else{
      $reply->transAmount = -1;
    }
  }else{
    $reply->transAmount = -1;
  }
}

//debugging
// $query = "SHOW TABLES";
// $result = $conn ->query($query);
// echo $result;
// $message  = 'Invalid query: ' . mysql_error() . "\n";
// $message .= 'Whole query: ' . $query;
// die($message);

echo json_encode($reply);

$conn->close();
?>
