<?php
// Database connection
require_once 'db_connect.php';

header('Content-Type: application/json');

// Get the bike_id from the request
$bike_id = isset($_GET['bike_id']) ? $_GET['bike_id'] : die();

// SQL Query to fetch reserved times for the given bike_id
$query = "SELECT start_date, end_date, status FROM reservations WHERE bike_id = ?";
$stmt = $conn->prepare($query);
$stmt->bind_param("i", $bike_id);
$stmt->execute();
$result = $stmt->get_result();

// Check if any data exists
if ($result->num_rows > 0) {
    $reservations = array();

    // Fetch reserved times and push to the array
    while ($row = $result->fetch_assoc()) {
        // If status is null, set it to a default value (e.g., "pending")
        $status = $row['status'] ?? 'pending';  // Default value is "pending" if status is null

        $reservations[] = array(
            'start_date' => $row['start_date'],
            'end_date' => $row['end_date'],
            'status' => $status  // Use the default value if null
        );
    }

    echo json_encode($reservations);
} else {
    echo json_encode([]);
}

$stmt->close();
$conn->close();
?>
