<?php
include 'db_connect.php';

$reservation_id = $_POST['reservation_id'];
$name = $_POST['name'];
$contact_number = $_POST['contact_number'];
$nic = $_POST['nic'];
$start_date = $_POST['start_date'];
$end_date = $_POST['end_date'];
$total_price = $_POST['total_price'];
$discount = $_POST['discount'];
$status = $_POST['status'];
$payment_method = $_POST['payment_method'];

$sql = "UPDATE reservations SET 
    name='$name', 
    contact_number='$contact_number', 
    nic='$nic', 
    start_date='$start_date', 
    end_date='$end_date', 
    total_price='$total_price', 
    discount='$discount', 
    status='$status', 
    payment_method='$payment_method' 
    WHERE reservation_id='$reservation_id'";

if ($conn->query($sql) === TRUE) {
    echo "Reservation updated successfully";
} else {
    echo "Error updating reservation: " . $conn->error;
}

$conn->close();
?>