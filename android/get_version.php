<?php

include_once 'config.php';

$status_sql = "SELECT `system_status`, `version_code`, `version_name` FROM `configuration`";
$status_result = $con->query($status_sql);
$status_row = mysqli_fetch_assoc($status_result);

$emptyarray = array();

if ($status_row['system_status'] == "1") {
    
//    echo $status_row['system_status'];
//    $emptyarray[] = array('system_status' => "1");

//    $flavour = filter_input(INPUT_POST, 'flavour');
//
//    if ($flavour == "master") {
//        $sql = "SELECT `version_code`, `version_name` FROM `admin_configuration`";
//    } else if ($flavour == "pos") {
//        $sql = "SELECT `version_code`, `version_name` FROM `client_configuration`";
//    }

//    $sql = "SELECT `version_code`, `version_name` FROM `configuration`";
//    $result = $con->query($sql);
//
//    while ($row = mysqli_fetch_assoc($result)) {
//        $emptyarray[] = $row;
//    }
    
    $emptyarray[] = $status_row;
    
} else {
    $emptyarray[] = array('system_status' => "0");
}

echo json_encode($emptyarray);


