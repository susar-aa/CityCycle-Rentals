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

$id = $_GET['id'];

$sql = "SELECT id, username, email, phone_number, profile_picture FROM users WHERE id=$id";
$result = $conn->query($sql);

$user = array();
if ($result->num_rows > 0) {
    $user = $result->fetch_assoc();
}

echo json_encode($user);

$conn->close();
?>