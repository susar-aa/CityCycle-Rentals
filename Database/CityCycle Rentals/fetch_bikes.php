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

// Fetch bikes and their associated station names
$sql = "SELECT bikes.*, stations.name AS station_name FROM bikes LEFT JOIN stations ON bikes.station_id = stations.id";
$result = $conn->query($sql);

$bikes = [];
if ($result->num_rows > 0) {
    while($row = $result->fetch_assoc()) {
        $bikes[] = $row;
    }
}

$conn->close();

header('Content-Type: application/json');
echo json_encode($bikes);
?>