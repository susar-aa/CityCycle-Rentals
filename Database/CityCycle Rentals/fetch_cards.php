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

// Get user_id from the request
$user_id = isset($_GET['user_id']) ? $_GET['user_id'] : 0;

// SQL query to fetch cards based on user_id
$sql = "SELECT card_number, expiry_date, card_holder FROM cards WHERE user_id = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $user_id);
$stmt->execute();
$result = $stmt->get_result();

// Check if any records found
if ($result->num_rows > 0) {
    $cards = array();
    while($row = $result->fetch_assoc()) {
        $cards[] = $row;
    }
    echo json_encode($cards);
} else {
    echo json_encode([]);
}

$stmt->close();
$conn->close();
?>