<?php
header('Content-Type: application/json');

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

// Query to fetch all promotions
$sql = "SELECT promo_code, discount_percentage, start_date, end_date FROM promotions";
$result = $conn->query($sql);

// Check if we have results
if ($result->num_rows > 0) {
    $promotions = array();

    while($row = $result->fetch_assoc()) {
        $promotions[] = $row;  // Add each row of data to the promotions array
    }

    // Return the promotions data as JSON
    echo json_encode($promotions);
} else {
    echo json_encode(array("message" => "No promotions found"));
}

// Close connection
$conn->close();
?>
