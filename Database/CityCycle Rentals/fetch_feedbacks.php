<?php
header('Content-Type: application/json');

// Database connection
$servername = "localhost";
$username = "root";
$password = "";
$dbname = "CityCycleRentals";

$conn = new mysqli($servername, $username, $password, $dbname);

if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

$bike_id = isset($_GET['bike_id']) ? intval($_GET['bike_id']) : 0;

if ($bike_id > 0) {
    $sql = "SELECT user, feedback FROM feedbacks WHERE bike_id = ?";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("i", $bike_id);
    $stmt->execute();
    $result = $stmt->get_result();

    $feedbacks = array();
    while ($row = $result->fetch_assoc()) {
        $feedbacks[] = $row;
    }

    echo json_encode($feedbacks);
} else {
    echo json_encode(array("error" => "Invalid bike ID"));
}

$conn->close();
?>