<?php
$target_dir = "C:/xampp/htdocs/CityCycle Rentals/uploads/";
$target_file = $target_dir . basename($_FILES["file"]["name"]);
$imageFileType = strtolower(pathinfo($target_file, PATHINFO_EXTENSION));

// Check if image file is a actual image or fake image
if(isset($_FILES["file"])) {
    $check = getimagesize($_FILES["file"]["tmp_name"]);
    if($check !== false) {
        if (move_uploaded_file($_FILES["file"]["tmp_name"], $target_file)) {
            // Return the relative path to the uploaded file
            $relative_path = "uploads/" . basename($_FILES["file"]["name"]);
            echo json_encode(["url" => $relative_path]);
        } else {
            echo json_encode(["error" => "Sorry, there was an error uploading your file."]);
        }
    } else {
        echo json_encode(["error" => "File is not an image."]);
    }
} else {
    echo json_encode(["error" => "No file was uploaded."]);
}
?>