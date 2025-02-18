<?php
// Include database connection
require_once('db_connect.php');

// Get user ID from GET request
$user_id = isset($_GET['user_id']) ? $_GET['user_id'] : '';

// Check if user ID is provided
if ($user_id != '') {
    // Query to fetch reservations for the user
    $query = "SELECT * FROM reservations WHERE user_id = '$user_id'";
    $result = mysqli_query($conn, $query);

    // Check if any reservations are found
    if (mysqli_num_rows($result) > 0) {
        $reservations = array();

        while ($row = mysqli_fetch_assoc($result)) {
            // Prepare reservation data
            $reservation = array(
                "reservation_id" => $row['reservation_id'],
                "bike_id" => $row['bike_id'],
                "name" => $row['name'],
                "contact_number" => $row['contact_number'],
                "nic" => $row['nic'],
                "start_date" => $row['start_date'],
                "end_date" => $row['end_date'],
                "total_price" => $row['total_price'],
                "status" => $row['status'],
                "payment_method" => $row['payment_method']
            );
            // Add reservation to the response array
            $reservations[] = $reservation;
        }

        // Return the response as JSON
        echo json_encode($reservations);
    } else {
        // No reservations found for the user
        echo json_encode(array("message" => "No reservations found."));
    }
} else {
    // If user ID is not provided
    echo json_encode(array("message" => "User ID is required."));
}

// Close the database connection
mysqli_close($conn);
?>
