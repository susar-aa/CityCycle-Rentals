<?php
$servername = "localhost";
$username = "root";
$password = "";
$dbname = "citycyclerentals";

$conn = new mysqli($servername, $username, $password, $dbname);

if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

$userId = $_GET['userId'];  // Get the userId from the request

// Modify the query to include bike name from bikes table
$sql = "SELECT r.*, b.name AS bike_name, b.image_url 
        FROM reservations r
        JOIN bikes b ON r.bike_id = b.id
        WHERE r.user_id = $userId";

$result = $conn->query($sql);

$reservations = array();

if ($result->num_rows > 0) {
    while ($row = $result->fetch_assoc()) {
        $reservations[] = array(
            "reservation_id" => $row["reservation_id"],
            "bike_id" => $row["bike_id"],
            "name" => $row["name"],
            "start_date" => $row["start_date"],
            "end_date" => $row["end_date"],
            "total_price" => $row["total_price"],
            "status" => $row["status"],
            "bike_name" => $row["bike_name"],
            "discount" => $row['discount'],
            "bike_image_url" => $row["image_url"]
        );
    }
}

echo json_encode($reservations);

$conn->close();
?>
