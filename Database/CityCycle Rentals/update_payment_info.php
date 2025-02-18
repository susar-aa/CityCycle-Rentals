<?php
include '../db_connect.php';

$user_id = $_GET['user_id']; // Get user ID from session or request

$sql = "SELECT * FROM payments WHERE user_id = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $user_id);
$stmt->execute();
$result = $stmt->get_result();

$payments = [];
while ($row = $result->fetch_assoc()) {
    $payments[] = $row;
}

echo json_encode($payments);
?>
