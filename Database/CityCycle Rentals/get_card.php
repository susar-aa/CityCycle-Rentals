<?php
include 'db_connect.php';

$userId = $_GET['userId'];  // Get user ID from URL parameters

// Query to fetch cards for the given user ID
$query = "SELECT * FROM cards WHERE user_id = '$userId'";  // Assuming a `user_id` field in your `cards` table
$result = mysqli_query($conn, $query);

if (mysqli_num_rows($result) > 0) {
    $cards = [];
    while ($row = mysqli_fetch_assoc($result)) {
        $cards[] = $row;
    }
    echo json_encode(['cards' => $cards]);
} else {
    echo json_encode(['message' => 'No cards found']);
}

mysqli_close($conn);
?>
