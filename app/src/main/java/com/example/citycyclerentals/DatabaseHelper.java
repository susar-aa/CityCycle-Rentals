package com.example.citycyclerentals;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "citycyclerentals.db";
    private static final int DATABASE_VERSION = 2;

    // Table name
    private static final String TABLE_BIKES = "bikes";
    private static final String TABLE_PROMOTIONS = "promotions";

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

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop the existing tables and recreate them for upgrades
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BIKES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROMOTIONS);
        onCreate(db);
    }

    // Get all promotions
    public Cursor getAllPromotions() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_PROMOTIONS, null);
    }

    // Get a bike by its ID
    public Cursor getBikeById(int bikeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_BIKES + " WHERE " + COLUMN_ID + " = ?", new String[]{String.valueOf(bikeId)});
    }

    // Insert a new bike into the database
    public void insertBike(int bikeId, String type, String name, int availability, int stationId,
                           double priceHourly, double priceDaily, double priceMonthly, String imageUrl) {
        SQLiteDatabase db = this.getWritableDatabase();
        String insertQuery = "INSERT INTO " + TABLE_BIKES + " (" + COLUMN_ID + ", "
                + COLUMN_TYPE + ", " + COLUMN_NAME + ", " + COLUMN_AVAILABILITY + ", "
                + COLUMN_STATION_ID + ", " + COLUMN_PRICE_HOURLY + ", " + COLUMN_PRICE_DAILY + ", "
                + COLUMN_PRICE_MONTHLY + ", " + COLUMN_IMAGE_URL + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        db.execSQL(insertQuery, new Object[]{bikeId, type, name, availability, stationId, priceHourly,
                priceDaily, priceMonthly, imageUrl});
    }
}
