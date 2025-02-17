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
$name = $_POST['name'];
$latitude = $_POST['latitude'];
$longitude = $_POST['longitude'];

// Insert station data into the database
$sql = "INSERT INTO stations (name, latitude, longitude) VALUES (?, ?, ?)";
$stmt = $conn->prepare($sql);
$stmt->bind_param("sdd", $name, $latitude, $longitude);

if ($stmt->execute()) {
    echo json_encode(array("success" => "Station added successfully"));
} else {
    echo json_encode(array("error" => "Error: " . $stmt->error));
}

$stmt->close();
$conn->close();
?>