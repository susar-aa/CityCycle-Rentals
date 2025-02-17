<?php
header('Content-Type: application/json');

$servername = "localhost";
$username = "root";
$password = "";
$dbname = "citycyclerentals";

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);

// Check connection
if ($conn->connect_error) {
    $response = array("error" => "Connection failed: " . $conn->connect_error);
    echo json_encode($response);
    exit();
}

$sql = "SELECT id, name, latitude, longitude FROM stations";
$result = $conn->query($sql);

if ($result === FALSE) {
    $response = array("error" => "Error executing query: " . $conn->error);
    echo json_encode($response);
    $conn->close();
    exit();
}

$stations = array();
if ($result->num_rows > 0) {
    while($row = $result->fetch_assoc()) {
        $stations[] = $row;
    }
} else {
    $response = array("error" => "No stations found");
    echo json_encode($response);
    $conn->close();
    exit();
}

echo json_encode($stations);
$conn->close();
?>