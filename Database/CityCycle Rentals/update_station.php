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

// Get POST data
$station_id = $_POST['id'];
$name = $_POST['name'];
$latitude = $_POST['latitude'];
$longitude = $_POST['longitude'];

// Update station data in the database
$sql = "UPDATE stations SET name = ?, latitude = ?, longitude = ? WHERE id = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("sddi", $name, $latitude, $longitude, $station_id);

if ($stmt->execute()) {
    echo json_encode(array("success" => "Station updated successfully"));
} else {
    echo json_encode(array("error" => "Error: " . $stmt->error));
}

$stmt->close();
$conn->close();
?>