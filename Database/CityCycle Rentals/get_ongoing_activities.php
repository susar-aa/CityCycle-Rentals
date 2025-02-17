<?php
header('Content-Type: application/json');
error_reporting(E_ALL);
ini_set('display_errors', 1);

// Database connection
$servername = "localhost";
$username = "root";
$password = "";
$dbname = "citycyclerentals";

$conn = new mysqli($servername, $username, $password, $dbname);

if ($conn->connect_error) {
    error_log("Connection failed: " . $conn->connect_error);
    die("Connection failed: " . $conn->connect_error);
}

$user_id = isset($_GET['user_id']) ? intval($_GET['user_id']) : 0;

$sql = "SELECT reservations.name, reservations.status, bikes.image_url AS bikeImageUrl, bikes.name AS bikeName, reservations.end_date AS endDate 
        FROM reservations 
        JOIN bikes ON reservations.bike_id = bikes.id 
        WHERE reservations.user_id = ? AND reservations.status IN ('pending', 'confirmed', 'paid')";
$stmt = $conn->prepare($sql);
if (!$stmt) {
    error_log("Prepare failed: (" . $conn->errno . ") " . $conn->error);
    die("Prepare failed: (" . $conn->errno . ") " . $conn->error);
}
$stmt->bind_param("i", $user_id);
$stmt->execute();
$result = $stmt->get_result();

$reservations = array();
while ($row = $result->fetch_assoc()) {
    $reservations[] = $row;
}

echo json_encode($reservations, JSON_PRETTY_PRINT);

$conn->close();
?>