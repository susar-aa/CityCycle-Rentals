<?php
header("Content-Type: application/json");

$servername = "localhost";
$username = "root";
$password = "";
$dbname = "citycyclerentals";

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);

// Check connection
if ($conn->connect_error) {
    die(json_encode(array("error" => "Connection failed: " . $conn->connect_error)));
}

$promo_id = $_POST['promo_id'];

// Delete promotion
$sql = "DELETE FROM promotions WHERE promo_id = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $promo_id);

if ($stmt->execute()) {
    echo json_encode(array("success" => "Promotion deleted successfully"));
} else {
    echo json_encode(array("error" => "Failed to delete promotion"));
}

$stmt->close();
$conn->close();
?>