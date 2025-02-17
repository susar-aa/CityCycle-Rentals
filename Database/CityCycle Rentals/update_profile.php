<?php
// Include the database connection file
include('db_connect.php');

// Set the header to indicate the response is in JSON format
header('Content-Type: application/json');

// Check if the request method is POST and the required parameters are provided
if ($_SERVER['REQUEST_METHOD'] == 'POST' && isset($_POST['id'], $_POST['username'], $_POST['email'], $_POST['phone_number'])) {

    // Get the values from the POST request
    $userId = $_POST['id'];
    $username = $_POST['username'];
    $email = $_POST['email'];
    $phone = $_POST['phone_number']; // Corrected key name
    $profile_picture = isset($_POST['profile_picture']) ? $_POST['profile_picture'] : '';

    // Validate input (basic validation for email and phone number)
    if (empty($username) || empty($email) || empty($phone)) {
        echo json_encode(["success" => false, "error" => "All fields are required"]);
        exit();
    }

    if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
        echo json_encode(["success" => false, "error" => "Invalid email format"]);
        exit();
    }

    if (!preg_match("/^[0-9]{10}$/", $phone)) {
        echo json_encode(["success" => false, "error" => "Invalid phone number"]);
        exit();
    }

    // SQL query to update the profile (replacing 'user_id' with 'id')
    $sql = "UPDATE users SET username = ?, email = ?, phone_number = ?, profile_picture = ? WHERE id = ?";
    $stmt = $conn->prepare($sql);

    // Check if the SQL query preparation was successful
    if ($stmt === false) {
        echo json_encode(["success" => false, "error" => "Failed to prepare SQL statement: " . $conn->error]);
        exit();
    }

    // Bind parameters to the SQL query
    $stmt->bind_param("ssssi", $username, $email, $phone, $profile_picture, $userId);

    // Execute the query
    if ($stmt->execute()) {
        // Success response
        echo json_encode(["success" => true]);
    } else {
        // Failure response with error message
        echo json_encode(["success" => false, "error" => "Failed to update profile: " . $stmt->error]);
    }

    // Close the prepared statement and connection
    $stmt->close();
    $conn->close();
} else {
    // Return failure response if required parameters are missing
    echo json_encode(["success" => false, "error" => "Missing required parameters"]);
}
?>