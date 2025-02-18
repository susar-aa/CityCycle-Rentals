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

// Get the station ID from the request
$station_id = $_GET['id'];

// Delete the station from the database
$sql = "DELETE FROM stations WHERE id = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $station_id);

if ($stmt->execute()) {
    echo json_encode(array("success" => "Station deleted successfully"));
} else {
    echo json_encode(array("error" => "Error: " . $stmt->error));
}

$stmt->close();
$conn->close();
?>