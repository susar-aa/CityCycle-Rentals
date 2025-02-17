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

// Get the bike ID and availability from the request
$bike_id = $_GET['id'];
$availability = $_GET['availability'];

// Update the bike's availability in the database
$sql = "UPDATE bikes SET availability = ? WHERE id = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("ii", $availability, $bike_id);

if ($stmt->execute()) {
    echo json_encode(array("success" => "Bike availability updated successfully"));
} else {
    echo json_encode(array("error" => "Error: " . $stmt->error));
}

$stmt->close();
$conn->close();
?>