<?php
// Include database connection file
include('db_connect.php');

// Get station_id from the request
$station_id = $_GET['station_id'];

// Prepare SQL query to fetch the station name
$sql = "SELECT name FROM stations WHERE id = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("s", $id);  // "s" means the station_id is a string
$stmt->execute();
$result = $stmt->get_result();

// Check if the station is found
if ($result->num_rows > 0) {
    $station = $result->fetch_assoc();
    // Return the station name as a JSON response
    echo json_encode([['name' => $station['name']]]);
} else {
    // If no station is found, return an empty response or an error message
    echo json_encode([]);
}

$stmt->close();
$conn->close();
?>
