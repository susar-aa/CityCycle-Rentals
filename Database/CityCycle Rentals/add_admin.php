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

// Sanitize input to prevent SQL injection
$username = $conn->real_escape_string($_POST['username']);
$email = $conn->real_escape_string($_POST['email']);
$phone_number = $conn->real_escape_string($_POST['phone_number']);
$password = $conn->real_escape_string($_POST['password']);
$role = 'admin';

$sql = "INSERT INTO users (username, email, phone_number, password, role) VALUES ('$username', '$email', '$phone_number', '$password', '$role')";

if ($conn->query($sql) === TRUE) {
    echo "New user created successfully";
} else {
    echo "Error: " . $sql . "<br>" . $conn->error;
}

$conn->close();
?>