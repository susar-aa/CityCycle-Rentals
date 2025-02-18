<?php
include 'db_connect.php';

$reservation_id = $_POST['reservation_id'];

$sql = "DELETE FROM reservations WHERE reservation_id='$reservation_id'";

if ($conn->query($sql) === TRUE) {
    echo "Reservation deleted successfully";
} else {
    echo "Error deleting reservation: " . $conn->error;
}

$conn->close();
?>