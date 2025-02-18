<?php
// get_user_details.php
include 'db_connect.php';

$user_id = $_POST['user_id'];

// Fetch user details from the database
$query = "SELECT username, email, phone_number FROM users WHERE user_id = '$user_id'";
$result = $conn->query($query);

if ($result->num_rows > 0) {
    $row = $result->fetch_assoc();
    echo json_encode($row); // Return user data as JSON
} else {
    echo "error: User not found";
}

$conn->close();
?>
