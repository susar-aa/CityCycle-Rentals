package com.example.citycyclerentals;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "citycyclerentals.db";
    private static final int DATABASE_VERSION = 2;

    // Table names
    private static final String TABLE_BIKES = "bikes";
    private static final String TABLE_PROMOTIONS = "promotions";
    private static final String TABLE_RESERVATIONS = "reservations"; // Ensure this table exists

    // Bikes table columns
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_AVAILABILITY = "availability";
    public static final String COLUMN_STATION_ID = "station_id";
    public static final String COLUMN_PRICE_HOURLY = "price_hourly";
    public static final String COLUMN_PRICE_DAILY = "price_daily";
    public static final String COLUMN_PRICE_MONTHLY = "price_monthly";
    public static final String COLUMN_IMAGE_URL = "image_url";

    // Promotions table columns
    public static final String COLUMN_PROMO_ID = "promo_id";
    public static final String COLUMN_PROMO_CODE = "promo_code";
    public static final String COLUMN_DISCOUNT_PERCENTAGE = "discount_percentage";
    public static final String COLUMN_START_DATE = "start_date";
    public static final String COLUMN_END_DATE = "end_date";

    // Reservations table columns
    private static final String COLUMN_RESERVATION_ID = "id";
    private static final String COLUMN_RESERVATION_STATUS = "status"; // Ensure this column exists in the table
    private static final String DATABASE_URL = "jdbc:sqlite:/data/data/com.example.citycyclerentals/databases/citycyclerentals.db";
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName("org.sqlite.JDBC"); // Load SQLite JDBC driver
            connection = DriverManager.getConnection(DATABASE_URL);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create bikes table
        String createBikesTable = "CREATE TABLE IF NOT EXISTS " + TABLE_BIKES + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_TYPE + " TEXT, "
                + COLUMN_NAME + " TEXT, "
                + COLUMN_AVAILABILITY + " INTEGER, "
                + COLUMN_STATION_ID + " INTEGER, "
                + COLUMN_PRICE_HOURLY + " REAL, "
                + COLUMN_PRICE_DAILY + " REAL, "
                + COLUMN_PRICE_MONTHLY + " REAL, "
                + COLUMN_IMAGE_URL + " TEXT)";
        db.execSQL(createBikesTable);

        // Create promotions table
        String createPromoTable = "CREATE TABLE IF NOT EXISTS " + TABLE_PROMOTIONS + " ("
                + COLUMN_PROMO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_PROMO_CODE + " TEXT, "
                + COLUMN_DISCOUNT_PERCENTAGE + " INTEGER, "
                + COLUMN_START_DATE + " TEXT, "
                + COLUMN_END_DATE + " TEXT)";
        db.execSQL(createPromoTable);

        // Create reservations table if it does not exist
        String createReservationsTable = "CREATE TABLE IF NOT EXISTS " + TABLE_RESERVATIONS + " ("
                + COLUMN_RESERVATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_RESERVATION_STATUS + " TEXT)";
        db.execSQL(createReservationsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop the existing tables and recreate them for upgrades
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BIKES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROMOTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESERVATIONS);
        onCreate(db);
    }

    // Get all promotions
    public Cursor getAllPromotions() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PROMOTIONS, null);
        return cursor; // Caller must close the cursor
    }

    // Get a bike by its ID
    public Cursor getBikeById(int bikeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_BIKES + " WHERE " + COLUMN_ID + " = ?",
                new String[]{String.valueOf(bikeId)});
        return cursor; // Caller must close the cursor
    }

    // Insert a new bike into the database
    public void insertBike(String type, String name, int availability, int stationId,
                           double priceHourly, double priceDaily, double priceMonthly, String imageUrl) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TYPE, type);
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_AVAILABILITY, availability);
        values.put(COLUMN_STATION_ID, stationId);
        values.put(COLUMN_PRICE_HOURLY, priceHourly);
        values.put(COLUMN_PRICE_DAILY, priceDaily);
        values.put(COLUMN_PRICE_MONTHLY, priceMonthly);
        values.put(COLUMN_IMAGE_URL, imageUrl);

        db.insert(TABLE_BIKES, null, values);
        db.close();
    }

    // Update the reservation status safely
    public boolean updateReservationStatus(int reservationId, String newStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", newStatus);

        Log.d("DB_UPDATE", "Attempting to update reservation ID: " + reservationId + " to status: " + newStatus);

        // Update the reservation status in the database
        int rowsAffected = db.update("reservations", values, "id = ?", new String[]{String.valueOf(reservationId)});

        if (rowsAffected == 0) {
            Log.e("DB_UPDATE", "No rows updated! Possible reasons: invalid reservation ID or incorrect column name.");

            // Print current reservations
            Cursor cursor = db.rawQuery("SELECT * FROM reservations", null);
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String status = cursor.getString(cursor.getColumnIndex("status"));
                Log.e("DB_UPDATE", "Existing Reservation - ID: " + id + ", Status: " + status);
            }
            cursor.close();
        } else {
            Log.d("DB_UPDATE", "Update successful! Rows affected: " + rowsAffected);
        }

        db.close();
        return rowsAffected > 0;
    }


}
