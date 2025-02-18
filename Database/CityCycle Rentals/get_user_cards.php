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

$user_id = $_POST['user_id'];

$sql = "SELECT card_number FROM cards WHERE user_id = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $user_id);
$stmt->execute();
$result = $stmt->get_result();

$cards = array();

while ($row = $result->fetch_assoc()) {
    $cards[] = $row;
}

echo json_encode($cards);

$stmt->close();
$conn->close();
?>
