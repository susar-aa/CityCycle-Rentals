<?php
$servername = "localhost";
$username = "root";
$password = "";
$dbname = "citycyclerentals";

$conn = new mysqli($servername, $username, $password, $dbname);

$bike_id = $_GET['bike_id']; // Get bike_id from URL parameters
// Connect to the database


if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

// Query to get bike details by ID
$query = "SELECT * FROM bikes WHERE id = $bike_id";
$result = $conn->query($query);

if ($result->num_rows > 0) {
    $bike = $result->fetch_assoc();
    echo json_encode([
        "image_url" => $bike['image_url'],
        "name" => $bike['name'],
        // You can return other details like type, price, etc.
    ]);
} else {
    echo json_encode(["error" => "Bike not found"]);
}

$conn->close();
?>
