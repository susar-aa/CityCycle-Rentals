<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manage Bikes</title>
    <link rel="stylesheet" href="styles.css">
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            fetch('fetch_bikes.php')
                .then(response => response.json())
                .then(data => {
                    const bikesContainer = document.getElementById('bikes-container');
                    data.forEach(bike => {
                        const bikeCard = `
                            <div class="bike-card">
                                <img src="${bike.image_url}" alt="${bike.name}">
                                <h3>${bike.name}</h3>
                                <p>Type: ${bike.type}</p>
                                <p>Station: ${bike.station_name}</p>
                                <p>Hourly Price: $${bike.price_hourly}</p>
                                <p>Daily Price: $${bike.price_daily}</p>
                                <p>Monthly Price: $${bike.price_monthly}</p>
                                <p>Status: ${bike.isAvailable ? 'Available' : 'Not Available'}</p>
                            </div>
                        `;
                        bikesContainer.innerHTML += bikeCard;
                    });
                })
                .catch(error => console.error('Error fetching bikes:', error));
        });
    </script>
</head>
<body>
    <div class="container">
        <h1>Manage Bikes</h1>
        <div id="bikes-container" class="bikes-container"></div>
    </div>
</body>
</html>