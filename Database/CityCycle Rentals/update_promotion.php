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
$promo_code = $_POST['promo_code'];
$discount_percentage = $_POST['discount_percentage'];
$start_date = $_POST['start_date'];
$end_date = $_POST['end_date'];

// Update promotion
$sql = "UPDATE promotions SET promo_code = ?, discount_percentage = ?, start_date = ?, end_date = ? WHERE promo_id = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("sddsi", $promo_code, $discount_percentage, $start_date, $end_date, $promo_id);

if ($stmt->execute()) {
    echo json_encode(array("success" => "Promotion updated successfully"));
} else {
    echo json_encode(array("error" => "Failed to update promotion"));
}

$stmt->close();
$conn->close();
?>