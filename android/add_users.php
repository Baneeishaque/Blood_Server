<?php

include_once 'config.php';

$username = filter_input(INPUT_POST, 'username');
$passcode = filter_input(INPUT_POST, 'passcode');
$role = filter_input(INPUT_POST, 'role');

// $sql = "INSERT INTO `works`(`name`, `address`, `work_date`,`insertion_date_time`, `sales_person_id`) VALUES ('$work_name','$work_address','$work_date',CONVERT_TZ(NOW(),'-05:30','+00:00'),$sales_person_id)";
$sql="INSERT INTO `users`( `user_name`, `passcode`,'role') VALUES ('$username','$passcode',$role)";
//$bill_id_query = "SELECT MAX(id) AS id from works";
//$bill_no_result = $con->query($bill_id_query);
//
//$bill_no_row = mysqli_fetch_assoc($bill_no_result);
//if ($bill_no_row['id'] == '') {
//    $bill_no = 1;
//} else {
//    $bill_no = $bill_no_row['id'] + 1;
//}

if (!$con->query($sql)) {
    $arr = array('status' => "1", 'error' => $con->error);
} else {
    
    $arr = array('status' => "0");
}
echo json_encode($arr);
