<?php
// Enable error reporting for debugging
ini_set('display_errors', 1);
error_reporting(E_ALL);

// Include database connection
include('db_connect.php');

// Get data from request
$user_id = $_POST['user_id']; 
$name = $_POST['name'];
$contactNumber = $_POST['contact_number'];
$nic = $_POST['nic'];
$startDate = $_POST['start_date'];
$endDate = $_POST['end_date'];
$paymentMethod = $_POST['payment_method'];
$promoCode = $_POST['promo_code'];
$bike_id = $_POST['bike_id']; 
$duration = $_POST['duration']; 

// Validate required fields
if (empty($user_id) || empty($name) || empty($contactNumber) || empty($nic) || empty($startDate) || empty($endDate) || empty($paymentMethod) || empty($bike_id)) {
    echo "Error: Missing required fields.";
    exit();
}

// Ensure dates are in correct format
$startDate = date('Y-m-d H:i:s', strtotime($startDate));
$endDate = date('Y-m-d H:i:s', strtotime($endDate));

// Fetch bike price from database
$query_bike_price = "SELECT price_hourly FROM bikes WHERE id = ?";
if ($stmt_bike = $conn->prepare($query_bike_price)) {
    $stmt_bike->bind_param("i", $bike_id);
    $stmt_bike->execute();
    $stmt_bike->bind_result($price_hourly);
    $stmt_bike->fetch();
    $stmt_bike->close();

    if (!$price_hourly) {
        echo "Error: Bike not found.";
        exit();
    }
} else {
    echo "Error preparing query: " . $conn->error;
    exit();
}

// Calculate total price before discount
$totalPrice = $price_hourly * $duration;

// Default discount percentage
$discount_percentage = 0;

// If a promo code is provided, fetch the discount percentage
if (!empty($promoCode)) {
    $query_promo = "SELECT discount_percentage FROM promotions WHERE promo_code = ? AND CURDATE() BETWEEN start_date AND end_date";
    if ($stmt_promo = $conn->prepare($query_promo)) {
        $stmt_promo->bind_param("s", $promoCode);
        $stmt_promo->execute();
        $stmt_promo->bind_result($discount_percentage);
        $stmt_promo->fetch();
        $stmt_promo->close();

        if ($discount_percentage) {
            // Apply discount
            $discountAmount = ($discount_percentage / 100) * $totalPrice;
            $totalPrice -= $discountAmount;
        } else {
            echo "Error: Promo code is invalid or expired.";
            exit();
        }
    } else {
        echo "Error preparing query: " . $conn->error;
        exit();
    }
}

// Convert discount percentage to string format (e.g., "50%")
$discount_percentage_str = $discount_percentage . "%";

// **Force reservation status to 'Pending'**
$status = "pending";

// Insert into reservations table
$query = "INSERT INTO reservations (user_id, bike_id, name, contact_number, nic, start_date, end_date, payment_method, promo_code, discount, total_price, status, duration) 
          VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'pending', ?)"; 

if ($stmt = $conn->prepare($query)) {
    $stmt->bind_param("iisisssssdsd", $user_id, $bike_id, $name, $contactNumber, $nic, $startDate, $endDate, $paymentMethod, $promoCode, $discount_percentage_str, $totalPrice, $duration);

    if ($stmt->execute()) {
        echo "Reservation successfully made!";
    } else {
        echo "Error executing query: " . $stmt->error;
    }

    $stmt->close();
} else {
    echo "Error preparing query: " . $conn->error;
}

// Close connection
$conn->close();
?>
