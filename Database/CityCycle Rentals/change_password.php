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

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    $id = $conn->real_escape_string($_POST['id']);
    $new_password = $conn->real_escape_string($_POST['new_password']);

    $sql = "UPDATE users SET password='$new_password' WHERE id=$id";

    if ($conn->query($sql) === TRUE) {
        echo "Password changed successfully";
    } else {
        echo "Error changing password: " . $conn->error;
    }
}

$conn->close();
?>