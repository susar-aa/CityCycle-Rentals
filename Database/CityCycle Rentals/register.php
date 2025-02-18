<?php
// Enable error reporting for debugging
ini_set('display_errors', 1);
error_reporting(E_ALL);

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

// Get data from POST request
$username = $_POST['username'];
$email = $_POST['email'];
$phone = $_POST['phone'];
$password = $_POST['password'];
$role = "user";  // Set the role as "user" by default

// Check if email or phone already exists
$email_check = "SELECT * FROM users WHERE email = '$email'";
$phone_check = "SELECT * FROM users WHERE phone_number = '$phone'";

$email_result = $conn->query($email_check);
$phone_result = $conn->query($phone_check);

if ($email_result->num_rows > 0) {
    echo "error: Email already exists";
} elseif ($phone_result->num_rows > 0) {
    echo "error: Phone number already exists";
} else {
    // Insert the new user data into the database with the role
    $insert_query = "INSERT INTO users (username, email, phone_number, password, role) VALUES ('$username', '$email', '$phone', '$password', '$role')";

    if ($conn->query($insert_query) === TRUE) {
        echo "success";
    } else {
        echo "Registration failed: " . $conn->error;
    }
}

// Close the connection
$conn->close();
?>
