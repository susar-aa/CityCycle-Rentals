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

$promo_code = $_POST['promo_code'];
$discount_percentage = $_POST['discount_percentage'];
$start_date = $_POST['start_date'];
$end_date = $_POST['end_date'];

// Insert promotion
$sql = "INSERT INTO promotions (promo_code, discount_percentage, start_date, end_date) VALUES (?, ?, ?, ?)";
$stmt = $conn->prepare($sql);
$stmt->bind_param("sdds", $promo_code, $discount_percentage, $start_date, $end_date);

if ($stmt->execute()) {
    echo json_encode(array("success" => "Promotion added successfully"));
} else {
    echo json_encode(array("error" => "Failed to add promotion"));
}

$stmt->close();
$conn->close();
?>