<?php
$servername = "localhost";
$username = "root";
$password = "";
$dbname = "citycyclerentals";

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

// Get card_number from the request
$card_number = isset($_GET['card_number']) ? $_GET['card_number'] : '';

// SQL query to delete card based on card_number
$sql = "DELETE FROM cards WHERE card_number = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("s", $card_number);
$stmt->execute();

if ($stmt->affected_rows > 0) {
    echo "success";
} else {
    echo "error";
}

$stmt->close();
$conn->close();
?>