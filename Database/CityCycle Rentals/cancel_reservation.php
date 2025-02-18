<?php
// Include database connection
include('db_connect.php');

// Get the reservation ID from the URL
$reservation_id = $_GET['reservation_id'];

// SQL query to update the reservation status
$query = "UPDATE reservations SET status = 'canceled' WHERE reservation_id = ?";
$stmt = $conn->prepare($query);
$stmt->bind_param('i', $reservation_id);

if ($stmt->execute()) {
    echo "success";  // Ensure there is no extra space or newlines
} else {
    echo "error";
}

$stmt->close();
$conn->close();
?>
