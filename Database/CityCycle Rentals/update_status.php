<?php
require_once 'db_connect.php';

if (isset($_POST['reservation_id']) && isset($_POST['status'])) {
    $reservation_id = $_POST['reservation_id'];
    $status = $_POST['status'];

    $query = "UPDATE reservations SET status = '$status' WHERE reservation_id = $reservation_id";
    if (mysqli_query($conn, $query)) {
        echo "Status updated successfully.";
    } else {
        echo "Failed to update status.";
    }
}
?>
