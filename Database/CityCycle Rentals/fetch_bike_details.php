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

// Get bike ID from request
$bike_id = $_GET['bike_id'];

// Fetch bike details along with station details
$sql = "SELECT bikes.*, stations.name as station_name, stations.latitude, stations.longitude 
        FROM bikes 
        JOIN stations ON bikes.station_id = stations.id 
        WHERE bikes.id = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $bike_id);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows > 0) {
    $bike = $result->fetch_assoc();
    echo json_encode($bike);
} else {
    echo json_encode(array("error" => "No bike found with the given ID"));
}

$stmt->close();
$conn->close();
?>