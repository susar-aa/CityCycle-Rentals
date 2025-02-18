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

// Fetch all promotions
$sql = "SELECT * FROM promotions";
$result = $conn->query($sql);

$promotions = array();
while($row = $result->fetch_assoc()) {
    $promotions[] = $row;
}

echo json_encode($promotions);

$conn->close();
?>