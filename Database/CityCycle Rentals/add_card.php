<?php
include '../db_connect.php';

$user_id = $_POST['user_id'];
$card_number = $_POST['card_number'];
$expiry_date = $_POST['expiry_date'];
$card_holder_name = $_POST['card_holder_name'];
$cvv = $_POST['cvv'];

$sql = "INSERT INTO payments (user_id, card_number, expiry_date, card_holder_name, cvv, payment_method, status)
        VALUES (?, ?, ?, ?, ?, 'Card', 'Pending')";

$stmt = $conn->prepare($sql);
$stmt->bind_param("issss", $user_id, $card_number, $expiry_date, $card_holder_name, $cvv);

if ($stmt->execute()) {
    echo json_encode(["success" => true, "message" => "Card added successfully"]);
} else {
    echo json_encode(["success" => false, "message" => "Error adding card"]);
}
?>
