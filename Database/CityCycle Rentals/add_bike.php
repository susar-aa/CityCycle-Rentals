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
$name = $_POST['name'];
$type = $_POST['type'];
$price_hourly = $_POST['price_hourly'];
$price_daily = $_POST['price_daily'];
$price_monthly = $_POST['price_monthly'];
$station_id = $_POST['station_id'];
$image = $_POST['image'];

// Decode the image from Base64
$image = base64_decode($image);

// Create a unique filename for the image
$image_filename = uniqid() . '.jpg';

// Save the image to the server
file_put_contents('bike_images/' . $image_filename, $image);

// Create the full URL path for the image
$image_url = 'http://192.168.1.2/CityCycle%20Rentals/bike_images/' . $image_filename;

// Set availability to 1 by default
$availability = 1;

// Insert bike data into the database
$sql = "INSERT INTO bikes (name, type, price_hourly, price_daily, price_monthly, station_id, image_url, availability) 
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

$stmt = $conn->prepare($sql);
$stmt->bind_param("ssdddssi", $name, $type, $price_hourly, $price_daily, $price_monthly, $station_id, $image_url, $availability);

if ($stmt->execute()) {
    echo json_encode(array("success" => "Bike added successfully"));
} else {
    echo json_encode(array("error" => "Error: " . $stmt->error));
}

$stmt->close();
$conn->close();
?>