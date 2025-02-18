<?php
$servername = "localhost";
$username = "root";
$password = "";
$dbname = "citycyclerentals";

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

// Get the reset token from the URL
$reset_token = isset($_GET['token']) ? $_GET['token'] : '';

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    // Get the new password from the form
    $new_password = isset($_POST['new_password']) ? $_POST['new_password'] : '';
    $confirm_password = isset($_POST['confirm_password']) ? $_POST['confirm_password'] : '';

    // Validate the passwords
    if (empty($new_password) || empty($confirm_password)) {
        $error_message = "Both password fields are required";
    } elseif ($new_password !== $confirm_password) {
        $error_message = "Passwords do not match";
    } else {
        // Update the user's password in the database
        $sql = "UPDATE users SET password = ?, reset_token = NULL WHERE reset_token = ?";
        $stmt = $conn->prepare($sql);
        $stmt->bind_param("ss", password_hash($new_password, PASSWORD_DEFAULT), $reset_token);
        if ($stmt->execute()) {
            $success_message = "Password reset successful";
        } else {
            $error_message = "Failed to reset password";
        }
        $stmt->close();
    }
}

$conn->close();
?>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Password Reset</title>
    <link rel="stylesheet" href="styles.css">
    <style>
        body {
    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
    background-color: #f7f7f7;
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
    margin: 0;
    color: #333;
}

.container {
    background-color: #fff;
    padding: 30px;
    border-radius: 12px;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    max-width: 400px;
    width: 100%;
}

.form-container {
    text-align: center;
}

h2 {
    margin-bottom: 20px;
    color: #333;
    font-size: 24px;
    font-weight: 600;
}

label {
    display: block;
    font-weight: 500;
    margin-bottom: 8px;
    text-align: left;
}

input[type="password"] {
    width: 100%;
    padding: 12px;
    margin-bottom: 20px;
    border: 1px solid #ccc;
    border-radius: 8px;
    font-size: 16px;
    box-sizing: border-box;
    transition: border-color 0.3s;
}

input[type="password"]:focus {
    border-color: #007aff;
    outline: none;
}

input[type="submit"] {
    width: 100%;
    padding: 12px;
    background-color: #007aff;
    color: #fff;
    border: none;
    border-radius: 8px;
    font-size: 16px;
    font-weight: 600;
    cursor: pointer;
    transition: background-color 0.3s;
}

input[type="submit"]:hover {
    background-color: #005fcb;
}

.error-message, .success-message {
    padding: 12px;
    margin-bottom: 20px;
    border-radius: 8px;
    font-size: 14px;
    text-align: left;
}

.error-message {
    background-color: #f8d7da;
    color: #721c24;
    border: 1px solid #f5c6cb;
}

.success-message {
    background-color: #d4edda;
    color: #155724;
    border: 1px solid #c3e6cb;
}
        </style>
</head>
<body>
    <div class="container">
        <div class="form-container">
            <h2>Password Reset</h2>
            <?php if (isset($error_message)): ?>
                <div class="error-message"><?php echo $error_message; ?></div>
            <?php endif; ?>
            <?php if (isset($success_message)): ?>
                <div class="success-message"><?php echo $success_message; ?></div>
            <?php endif; ?>
            <form method="POST" action="">
                <label for="new_password">New Password:</label><br>
                <input type="password" id="new_password" name="new_password" required><br>
                <label for="confirm_password">Confirm Password:</label><br>
                <input type="password" id="confirm_password" name="confirm_password" required><br>
                <input type="submit" value="Reset Password">
            </form>
        </div>
    </div>
</body>
</html>