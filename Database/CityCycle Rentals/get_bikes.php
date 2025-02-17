<?php
// Include your database connection
include('db_connect.php');

// Set content type to JSON
header("Content-Type: application/json");

// Check if the connection is successful
if (!$conn) {
    echo json_encode(["error" => "Database connection failed"]);
    exit;
}

// Get search keyword if provided
$search = isset($_GET['search']) ? mysqli_real_escape_string($conn, $_GET['search']) : '';

// Base SQL query to fetch available bikes
$query = "SELECT 
                b.id, 
                b.type, 
                b.name, 
                b.availability, 
                b.price_hourly, 
                b.price_daily, 
                b.price_monthly, 
                b.image_url, 
                s.name AS station_name, 
                s.id AS station_id,  
                s.latitude, 
                s.longitude 
          FROM bikes b
          JOIN stations s ON b.station_id = s.id
          WHERE b.availability = 1";

// Apply search filter if a search term is provided
if (!empty($search)) {
    $query .= " AND (b.name LIKE '%$search%' OR s.name LIKE '%$search%' OR b.type LIKE '%$search%')";
}

// Execute query
$result = mysqli_query($conn, $query);

// Handle query failure
if (!$result) {
    echo json_encode(["error" => "Query failed: " . mysqli_error($conn)]);
    exit;
}

// Initialize an array to store bike data
$bikes = [];

// If rows are fetched
if (mysqli_num_rows($result) > 0) {
    while ($row = mysqli_fetch_assoc($result)) {
        // Sanitize and format data before returning
        $bikes[] = [
            'bike_id' => (int) $row['id'],
            'bike_name' => $row['name'],
            'bike_availability' => (int) $row['availability'],
            'bike_type' => $row['type'],
            'price_hourly' => (double) $row['price_hourly'],
            'price_daily' => (double) $row['price_daily'],
            'price_monthly' => (double) $row['price_monthly'],
            'bike_image' => $row['image_url'],
            'station_name' => $row['station_name'],
            'station_id' => (int) $row['station_id'],
            'latitude' => (double) $row['latitude'],
            'longitude' => (double) $row['longitude']
        ];
    }
}

// Return the available bikes as JSON
echo json_encode($bikes, JSON_PRETTY_PRINT);

// Close database connection
mysqli_close($conn);
?>
