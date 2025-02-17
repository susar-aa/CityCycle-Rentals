<?php
include 'db_connect.php';

$email = $_GET['email'];

$query = "SELECT name, email, phone, profile_picture FROM users WHERE email = ?";
$stmt = $conn->prepare($query);
$stmt->bind_param("s", $email);
$stmt->execute();
$stmt->bind_result($name, $email, $phone, $profile_picture);

if ($stmt->fetch()) {
    echo json_encode([
        "name" => $name,
        "email" => $email,
        "phone" => $phone,
        "profile_picture" => $profile_picture
    ]);
} else {
    echo json_encode(["error" => "User not found"]);
}

$stmt->close();
$conn->close();
?>
