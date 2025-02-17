<?php
// Include database connection
require_once('db_connect.php');

// Get bike ID from GET request
$bike_id = isset($_GET['bike_id']) ? $_GET['bike_id'] : '';

// Check if bike ID is provided
if ($bike_id != '') {
    // Query to fetch bike details based on the bike ID
    $query = "SELECT * FROM bikes WHERE bike_id = '$bike_id'";
    $result = mysqli_query($conn, $query);

    // Check if any bike is found
    if (mysqli_num_rows($result) > 0) {
        $bike = mysqli_fetch_assoc($result);

        // Prepare bike data
        $bike_details = array(
            "bike_id" => $bike['bike_id'],
            "name" => $bike['name'],
            "image_url" => $bike['image_url'],
            "price_hourly" => $bike['price_hourly'],
            "price_daily" => $bike['price_daily'],
            "price_monthly" => $bike['price_monthly']
        );

        // Return the response as JSON
        echo json_encode($bike_details);
    } else {
        // No bike found with the given ID
        echo json_encode(array("message" => "Bike not found."));
    }
} else {
    // If bike ID is not provided
    echo json_encode(array("message" => "Bike ID is required."));
}

// Close the database connection
mysqli_close($conn);
?>
