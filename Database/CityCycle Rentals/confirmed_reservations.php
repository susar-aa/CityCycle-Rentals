<?php
include 'db_connect.php';

$sql = "SELECT reservations.*, bikes.image_url AS bike_image_url 
        FROM reservations 
        LEFT JOIN bikes ON reservations.bike_id = bikes.id 
        WHERE reservations.status='confirmed'";
$result = $conn->query($sql);

$reservations = array();

if ($result->num_rows > 0) {
    while($row = $result->fetch_assoc()) {
        $reservations[] = $row;
    }
}

header('Content-Type: application/json');
echo json_encode($reservations);

$conn->close();
?>