-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Feb 18, 2025 at 06:06 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `citycyclerentals`
--

-- --------------------------------------------------------

--
-- Table structure for table `bikes`
--

CREATE TABLE `bikes` (
  `id` int(11) NOT NULL,
  `type` varchar(50) DEFAULT NULL,
  `name` varchar(100) DEFAULT NULL,
  `availability` tinyint(1) DEFAULT NULL,
  `station_id` int(11) DEFAULT NULL,
  `price_hourly` decimal(10,2) DEFAULT NULL,
  `price_daily` decimal(10,2) DEFAULT NULL,
  `price_monthly` decimal(10,2) DEFAULT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  `next_available_time` datetime DEFAULT NULL,
  `reserved_dates` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `bikes`
--

INSERT INTO `bikes` (`id`, `type`, `name`, `availability`, `station_id`, `price_hourly`, `price_daily`, `price_monthly`, `image_url`, `next_available_time`, `reserved_dates`) VALUES
(1, 'Mountain', 'G4TXM', 1, 1, 150.00, 1000.00, 3000.00, 'http://192.168.1.2/bike1.jpg', NULL, NULL),
(2, 'Road', 'LUMALA', 1, 2, 120.00, 800.00, 2500.00, 'http://192.168.1.2/CityCycle%20Rentals/bike_images/bike2.jpg', NULL, NULL),
(3, 'Electric', 'NGC Bike', 1, 3, 200.00, 1500.00, 4000.00, 'http://192.168.1.2/bike4.jpg', NULL, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `cards`
--

CREATE TABLE `cards` (
  `card_id` int(11) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `card_number` varchar(255) DEFAULT NULL,
  `expiry_date` varchar(20) DEFAULT NULL,
  `card_holder` varchar(255) DEFAULT NULL,
  `cvv` varchar(4) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `cards`
--

INSERT INTO `cards` (`card_id`, `user_id`, `card_number`, `expiry_date`, `card_holder`, `cvv`) VALUES
(8, 1, '0784646484484848', '12/27', 'dassamachan', '1916'),
(10, 53, '4216890338350925', '27/29', 'Susara Senarathne', '1254');

-- --------------------------------------------------------

--
-- Table structure for table `feedbacks`
--

CREATE TABLE `feedbacks` (
  `id` int(11) NOT NULL,
  `bike_id` int(11) NOT NULL,
  `user` varchar(255) DEFAULT 'Anonymous',
  `feedback` text NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `feedbacks`
--

INSERT INTO `feedbacks` (`id`, `bike_id`, `user`, `feedback`, `created_at`) VALUES
(1, 1, 'Anonymous', 'gemmk tmy itin meknm', '2025-02-17 15:48:08'),
(2, 1, 'Anonymous', 'gemma gemma', '2025-02-17 16:04:46'),
(3, 1, 'Anonymous', 'addddjdhjf', '2025-02-17 16:12:00');

-- --------------------------------------------------------

--
-- Table structure for table `promotions`
--

CREATE TABLE `promotions` (
  `promo_id` int(11) NOT NULL,
  `promo_code` varchar(50) NOT NULL,
  `discount_percentage` decimal(5,2) DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `end_date` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `promotions`
--

INSERT INTO `promotions` (`promo_id`, `promo_code`, `discount_percentage`, `start_date`, `end_date`) VALUES
(1, 'SUMMER200', 20.00, '2025-01-01', '2025-06-30'),
(2, 'BIKE50', 50.00, '0000-00-00', '2025-09-06'),
(3, 'NEWUSER100', 10.00, '0000-00-00', '2025-02-28'),
(4, 'CITYRIDE25', 25.00, '2025-03-10', '2025-03-20'),
(5, 'SPRING15', 15.00, '2025-04-05', '2025-04-25');

-- --------------------------------------------------------

--
-- Table structure for table `reservations`
--

CREATE TABLE `reservations` (
  `reservation_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `bike_id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `contact_number` varchar(20) NOT NULL,
  `nic` varchar(20) NOT NULL,
  `start_date` datetime NOT NULL,
  `end_date` datetime NOT NULL,
  `payment_method` enum('card','cash') NOT NULL,
  `promo_code` varchar(50) DEFAULT NULL,
  `discount` decimal(5,2) DEFAULT NULL,
  `total_price` decimal(10,2) NOT NULL,
  `status` enum('pending','confirmed','paid','ended','canceled') NOT NULL,
  `duration` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `reservations`
--

INSERT INTO `reservations` (`reservation_id`, `user_id`, `bike_id`, `name`, `contact_number`, `nic`, `start_date`, `end_date`, `payment_method`, `promo_code`, `discount`, `total_price`, `status`, `duration`) VALUES
(59, 1, 2, 'Susara Senarathne', '761407875', '200611003615', '2025-02-05 12:00:00', '2025-02-28 15:00:00', 'card', 'BIKE50', 50.00, 1440.00, 'pending', 24),
(60, 53, 1, 'Dasun', '7614078789', '8282hh', '2025-02-05 01:08:00', '2025-02-05 01:08:00', 'cash', '', 0.00, 0.00, 'pending', 0),
(68, 1, 2, 'hh', '996', 'gtt', '2025-02-05 01:40:00', '2025-02-05 01:40:00', 'cash', '', 0.00, 0.00, 'ended', 0),
(71, 53, 1, 'Susara', '761407875', '200600887765', '2025-02-09 06:54:00', '2025-02-28 06:54:00', 'cash', '', 0.00, 3600.00, 'pending', 24);

-- --------------------------------------------------------

--
-- Table structure for table `stations`
--

CREATE TABLE `stations` (
  `id` int(11) NOT NULL,
  `name` varchar(100) DEFAULT NULL,
  `latitude` decimal(10,7) DEFAULT NULL,
  `longitude` decimal(10,7) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `stations`
--

INSERT INTO `stations` (`id`, `name`, `latitude`, `longitude`) VALUES
(1, 'Colombo', 6.9267747, 79.8592601),
(2, 'Kurunegala', 7.4865987, 80.3684486),
(3, 'Kandy', 7.2905806, 80.6331236);

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `phone_number` varchar(20) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `role` varchar(20) NOT NULL,
  `profile_picture` text DEFAULT NULL,
  `reset_token` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `username`, `email`, `phone_number`, `password`, `role`, `profile_picture`, `reset_token`) VALUES
(1, 'User', 'dssusara3@gmail.com', '0780780780', '123', 'user', 'http://192.168.1.2/CityCycle%20Rentals/uploads/IMG_20250208_071737085.jpg', NULL),
(2, 'Admin', 'admin', '1234567890', '123', 'admin', NULL, NULL),
(53, 'Susara Senarathne', 'suz.x2006@gmail.com', '0761407875', '234', 'user', 'http://192.168.1.2/CityCycle%20Rentals/uploads/IMG_20250210_023541687.jpg', 'd778c3da7dd75415bf868c3ad45cbc3b');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `bikes`
--
ALTER TABLE `bikes`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `cards`
--
ALTER TABLE `cards`
  ADD PRIMARY KEY (`card_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `feedbacks`
--
ALTER TABLE `feedbacks`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `promotions`
--
ALTER TABLE `promotions`
  ADD PRIMARY KEY (`promo_id`),
  ADD UNIQUE KEY `promo_code` (`promo_code`);

--
-- Indexes for table `reservations`
--
ALTER TABLE `reservations`
  ADD PRIMARY KEY (`reservation_id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `bike_id` (`bike_id`);

--
-- Indexes for table `stations`
--
ALTER TABLE `stations`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `bikes`
--
ALTER TABLE `bikes`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `cards`
--
ALTER TABLE `cards`
  MODIFY `card_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `feedbacks`
--
ALTER TABLE `feedbacks`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `promotions`
--
ALTER TABLE `promotions`
  MODIFY `promo_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `reservations`
--
ALTER TABLE `reservations`
  MODIFY `reservation_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=72;

--
-- AUTO_INCREMENT for table `stations`
--
ALTER TABLE `stations`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=62;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `cards`
--
ALTER TABLE `cards`
  ADD CONSTRAINT `cards_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Constraints for table `reservations`
--
ALTER TABLE `reservations`
  ADD CONSTRAINT `reservations_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `reservations_ibfk_2` FOREIGN KEY (`bike_id`) REFERENCES `bikes` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
