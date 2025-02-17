<?php
require 'C:/xampp/htdocs/vendor/autoload.php'; // Adjust path to Composer's autoloader

use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;

$log_file = 'reset_password_log.txt';
file_put_contents($log_file, "Reset Password Script Started: " . date("Y-m-d H:i:s") . "\n", FILE_APPEND);

$servername = "localhost";
$username = "root";
$password = "";
$dbname = "citycyclerentals";

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);

// Check connection
if ($conn->connect_error) {
    $error_message = "Connection failed: " . $conn->connect_error;
    file_put_contents($log_file, $error_message . "\n", FILE_APPEND);
    die($error_message);
}

// Set the header to indicate the response is in JSON format
header('Content-Type: application/json');

// Get the POSTed email
$email = isset($_POST['email']) ? $_POST['email'] : '';

file_put_contents($log_file, "Email: " . $email . "\n", FILE_APPEND);

// Validate the input
if (empty($email)) {
    $response = array('error' => 'Email is required');
    file_put_contents($log_file, json_encode($response) . "\n", FILE_APPEND);
    echo json_encode($response);
    exit();
}

// Prepare SQL query to prevent SQL injection
$sql = "SELECT * FROM users WHERE email = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("s", $email); // "s" means string (for email)
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows > 0) {
    // User found, generate a reset token and send email
    $row = $result->fetch_assoc();
    $reset_token = bin2hex(random_bytes(16));
    $reset_url = "http://192.168.1.2/CityCycle%20Rentals/reset_password_form.php?token=" . $reset_token;

    // Save the reset token to the database
    $sql_update = "UPDATE users SET reset_token = ? WHERE email = ?";
    $stmt_update = $conn->prepare($sql_update);
    $stmt_update->bind_param("ss", $reset_token, $email);
    $stmt_update->execute();

    file_put_contents($log_file, "Reset Token: " . $reset_token . "\n", FILE_APPEND);

    // Send reset email using PHPMailer
    $mail = new PHPMailer(true);
    try {
        // Server settings
        $mail->isSMTP();
        $mail->Host = 'smtp.gmail.com'; // Set the SMTP server to send through
        $mail->SMTPAuth = true;
        $mail->Username = 'suz.x2006@gmail.com'; // SMTP username
        $mail->Password = 'hvba ctix veuv wwve'; // SMTP password
        $mail->SMTPSecure = PHPMailer::ENCRYPTION_STARTTLS;
        $mail->Port = 587;

        // Recipients
        $mail->setFrom('no-reply@citycyclerentals.com', 'CityCycle Rentals');
        $mail->addAddress($email);

        // Content
        $mail->isHTML(true);
        $mail->Subject = 'Password Reset Request';
        $mail->Body    = "Click the following link to reset your password: <a href='$reset_url'>$reset_url</a>";

        $mail->send();
        $response = array('success' => 'Password reset instructions sent to your email');
        file_put_contents($log_file, json_encode($response) . "\n", FILE_APPEND);
        echo json_encode($response);
    } catch (Exception $e) {
        $error_message = 'Message could not be sent. Mailer Error: ' . $mail->ErrorInfo;
        file_put_contents($log_file, $error_message . "\n", FILE_APPEND);
        echo json_encode(array('error' => $error_message));
    }
} else {
    // User not found
    $response = array('error' => 'Email not found');
    file_put_contents($log_file, json_encode($response) . "\n", FILE_APPEND);
    echo json_encode($response);
}

$stmt->close();
$conn->close();
?>