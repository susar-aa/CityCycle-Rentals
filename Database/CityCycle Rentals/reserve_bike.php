<?php
session_start();
include('db_connect.php');  // Assuming you have a DB connection file

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    $user_id = $_SESSION['user_id'];  // Assuming user is logged in and their ID is stored in session
    $bike_id = $_POST['bike_id'];
    $name = $_POST['name'];
    $contact_number = $_POST['contact_number'];
    $nic = $_POST['nic'];
    $start_date = $_POST['start_date'];  // Expecting in format Y-m-d H:i:s
    $end_date = $_POST['end_date'];      // Expecting in format Y-m-d H:i:s
    $payment_method = $_POST['payment_method'];
    $promo_code = !empty($_POST['promo_code']) ? $_POST['promo_code'] : null;  // Set to null if empty
    
    // Fetch promo discount if applicable
    $discount = 0;
    if ($promo_code) {
        $sql = "SELECT discount_percentage FROM promo_codes WHERE promo_code = ? AND CURDATE() BETWEEN start_date AND end_date";
        $stmt = $conn->prepare($sql);
        $stmt->bind_param('s', $promo_code);
        $stmt->execute();
        $result = $stmt->get_result();
        if ($result->num_rows > 0) {
            $row = $result->fetch_assoc();
            $discount = $row['discount_percentage'];
        }
    }

    // Fetch bike price
    $sql = "SELECT price_hourly, price_daily, price_monthly FROM bikes WHERE id = ?";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param('i', $bike_id);
    $stmt->execute();
    $result = $stmt->get_result();
    $row = $result->fetch_assoc();

    // Calculate total price based on rental duration and type
    $total_price = calculate_total_price($row, $start_date, $end_date);  // Assuming you have a function to calculate the price
    
    // Apply discount if applicable
    if ($discount > 0) {
        $total_price -= ($total_price * ($discount / 100));
    }

    // Insert into reservations table
    $stmt = $conn->prepare("INSERT INTO reservations (user_id, bike_id, name, contact_number, nic, start_date, end_date, payment_method, promo_code, discount, total_price, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'pending')");
    $stmt->bind_param('iisssssssd', $user_id, $bike_id, $name, $contact_number, $nic, $start_date, $end_date, $payment_method, $promo_code, $discount, $total_price);
    
    if ($stmt->execute()) {
        echo "Reservation confirmed.";
    } else {
        echo "Error: " . $stmt->error;
    }
}

// Function to calculate total price based on rental duration
function calculate_total_price($row, $start_date, $end_date) {
    $start = new DateTime($start_date);
    $end = new DateTime($end_date);
    $interval = $start->diff($end);
    
    // Assuming $row contains hourly, daily, and monthly prices
    $hours = $interval->h + ($interval->days * 24);  // Total hours
    $total_price = 0;

    // Price calculation based on duration
    if ($hours <= 24) {
        $total_price = $row['price_hourly'] * $hours;  // Hourly price
    } elseif ($hours > 24 && $hours <= 720) {
        $total_price = $row['price_daily'] * ceil($hours / 24);  // Daily price
    } else {
        $total_price = $row['price_monthly'] * ceil($hours / 720);  // Monthly price
    }

    return $total_price;
}
?>
