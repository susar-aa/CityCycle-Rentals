<?php
include 'db_connect.php';

$reservation_id = $_POST['reservation_id'];
$name = $_POST['name'];
$start_date = $_POST['start_date'];
$end_date = $_POST['end_date'];
$total_price = $_POST['total_price'];
$status = $_POST['status'];

$sql = "UPDATE reservations SET name='$name', start_date='$start_date', end_date='$end_date', total_price='$total_price', status='$status' WHERE reservation_id=$reservation_id";

if ($conn->query($sql) === TRUE) {
    echo "Record updated successfully";
} else {
    echo "Error updating record: " . $conn->error;
}

$conn->close();
?>