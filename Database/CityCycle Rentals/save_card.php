<?php
// Database connection details
$servername = "localhost";
$username = "root"; // default username for XAMPP MySQL
$password = ""; // default password for XAMPP MySQL
$dbname = "citycyclerentals"; // Replace with your actual database name

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

// Get the data from the request
$userId = isset($_POST['userId']) ? $_POST['userId'] : '';
$cardNumber = isset($_POST['cardNumber']) ? $_POST['cardNumber'] : '';
$expiryDate = isset($_POST['expiryDate']) ? $_POST['expiryDate'] : '';
$cardHolder = isset($_POST['cardHolder']) ? $_POST['cardHolder'] : '';
$cvv = isset($_POST['cvv']) ? $_POST['cvv'] : '';

// Check if all required parameters are provided
if (empty($userId) || empty($cardNumber) || empty($expiryDate) || empty($cardHolder) || empty($cvv)) {
    echo "Error: Missing required fields!";
    exit();
}

// Sanitize inputs (to avoid any malicious code)
$userId = $conn->real_escape_string($userId);
$cardNumber = $conn->real_escape_string($cardNumber);
$expiryDate = $conn->real_escape_string($expiryDate);
$cardHolder = $conn->real_escape_string($cardHolder);
$cvv = $conn->real_escape_string($cvv);

// Validate that the user exists in the 'users' table
$userCheckQuery = "SELECT id FROM users WHERE id = ?";
$stmt = $conn->prepare($userCheckQuery);
$stmt->bind_param("i", $userId);  // 'i' denotes integer type
$stmt->execute();
$stmt->store_result();

if ($stmt->num_rows > 0) {
    // User exists, proceed with inserting the card information
    $stmt->close();  // Close the previous statement

    // Prepare an insert query with placeholders
    $insertQuery = "INSERT INTO cards (user_id, card_number, expiry_date, card_holder, cvv) 
                    VALUES (?, ?, ?, ?, ?)";
    $stmt = $conn->prepare($insertQuery);
    $stmt->bind_param("issss", $userId, $cardNumber, $expiryDate, $cardHolder, $cvv);  // 'i' for integer, 's' for string

    if ($stmt->execute()) {
        echo "Card information saved successfully!";
    } else {
        echo "Error: " . $stmt->error;
    }
} else {
    // User does not exist, return an error message
    echo "Error: User ID does not exist!";
}

// Close the connection
$stmt->close();
$conn->close();
?>
