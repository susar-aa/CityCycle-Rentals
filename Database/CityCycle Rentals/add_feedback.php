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

$bike_id = isset($_POST['bike_id']) ? intval($_POST['bike_id']) : 0;
$feedback = isset($_POST['feedback']) ? $_POST['feedback'] : '';

if ($bike_id > 0 && !empty($feedback)) {
    $sql = "INSERT INTO feedbacks (bike_id, user, feedback) VALUES (?, 'Anonymous', ?)";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("is", $bike_id, $feedback);
    if ($stmt->execute()) {
        echo json_encode(array("success" => "Feedback added successfully"));
    } else {
        echo json_encode(array("error" => "Failed to add feedback"));
    }
} else {
    echo json_encode(array("error" => "Invalid bike ID or feedback"));
}

$conn->close();
?>