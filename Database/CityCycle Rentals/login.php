<?php
// Start the session
session_start();

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

// Set the header to indicate the response is in JSON format
header('Content-Type: application/json');

// Get the POSTed email and password
$email = isset($_POST['email']) ? $_POST['email'] : '';
$password = isset($_POST['password']) ? $_POST['password'] : '';

// Validate the input
if (empty($email) || empty($password)) {
    echo json_encode(array('error' => 'Email and password are required'));
    exit();
}

// Prepare SQL query to prevent SQL injection
$sql = "SELECT * FROM users WHERE email = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("s", $email); // "s" means string (for email)
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows > 0) {
    // User found, fetch data
    $row = $result->fetch_assoc();
    
    // Check if the password matches directly (no hashing)
    if ($password == $row['password']) {  // Direct comparison for plain text password
        
        // Store user_id in session
        $_SESSION['user_id'] = $row['id'];  // Store user ID in session
        
        // Optionally store the username or other data in the session if needed
        $_SESSION['username'] = $row['username'];
        $_SESSION['role'] = $row['role']; // Admin or User
        
        // Prepare response data
        $user_data = array(
            'id' => $row['id'],
            'username' => $row['username'],
            'email' => $row['email'],
            'phone' => $row['phone_number'] ? $row['phone_number'] : null,  // Handle null phone
            'role' => $row['role'],  // "user" or "admin"
            'profile_picture' => $row['profile_picture'] ? $row['profile_picture'] : '' // Handle null profile picture
        );
        
        // Send response back as JSON
        echo json_encode($user_data);
    } else {
        // Invalid password
        echo json_encode(array('error' => 'Invalid credentials'));
    }
} else {
    // User not found
    echo json_encode(array('error' => 'Invalid credentials or role'));
}

$stmt->close();
$conn->close();
?>