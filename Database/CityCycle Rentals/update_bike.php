<?php
header("Content-Type: application/json");

$servername = "localhost";
$username = "root";
$password = "";
$dbname = "citycyclerentals";

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);

// Check connection
if ($conn->connect_error) {
    die(json_encode(array("error" => "Connection failed: " . $conn->connect_error)));
}

// Get POST data
$bike_id = $_POST['id'];
$name = $_POST['name'];
$type = $_POST['type'];
$price_hourly = $_POST['price_hourly'];
$price_daily = $_POST['price_daily'];
$price_monthly = $_POST['price_monthly'];
$station_id = $_POST['station_id'];
$image = $_POST['image'];

// If an image is provided, update the image URL
if (!empty($image)) {
    // Decode the image from Base64
    $image = base64_decode($image);

    // Create a unique filename for the image
    $image_filename = uniqid() . '.jpg';

    // Save the image to the server
    file_put_contents('bike_images/' . $image_filename, $image);

    // Create the full URL path for the image
    $image_url = 'http://192.168.1.2/CityCycle%20Rentals/bike_images/' . $image_filename;

    // Update bike data including image
    $sql = "UPDATE bikes SET name = ?, type = ?, price_hourly = ?, price_daily = ?, price_monthly = ?, station_id = ?, image_url = ? WHERE id = ?";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("ssdddssi", $name, $type, $price_hourly, $price_daily, $price_monthly, $station_id, $image_url, $bike_id);
} else {
    // Update bike data without image
    $sql = "UPDATE bikes SET name = ?, type = ?, price_hourly = ?, price_daily = ?, price_monthly = ?, station_id = ? WHERE id = ?";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("ssdddsi", $name, $type, $price_hourly, $price_daily, $price_monthly, $station_id, $bike_id);
}

if ($stmt->execute()) {
    echo json_encode(array("success" => "Bike updated successfully"));
} else {
    echo json_encode(array("error" => "Error: " . $stmt->error));
}

$stmt->close();
$conn->close();
?>