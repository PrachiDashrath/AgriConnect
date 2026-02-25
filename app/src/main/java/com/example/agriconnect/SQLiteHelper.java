package com.example.agriconnect;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;

public class SQLiteHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "agriconnect.db";
    private static final int DB_VERSION = 1;

    public SQLiteHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Farmers table
        db.execSQL("CREATE TABLE farmers (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, mobile TEXT, location TEXT)");

        // Buyers table
        db.execSQL("CREATE TABLE buyers (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, mobile TEXT, location TEXT)");

        // Crops table
        db.execSQL("CREATE TABLE crops (id INTEGER PRIMARY KEY AUTOINCREMENT, farmerId INTEGER, name TEXT, qty TEXT, price TEXT)");

        // Scholarships table
        db.execSQL("CREATE TABLE scholarships (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, details TEXT)");

        // Insert default scholarships
        db.execSQL("INSERT INTO scholarships (title, details) VALUES ('PM-Kisan Yojana','₹6000/year for farmers')");
        db.execSQL("INSERT INTO scholarships (title, details) VALUES ('Agri Scholarship','Financial aid for higher studies')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS farmers");
        db.execSQL("DROP TABLE IF EXISTS buyers");
        db.execSQL("DROP TABLE IF EXISTS crops");
        db.execSQL("DROP TABLE IF EXISTS scholarships");
        onCreate(db);
    }

    // Add Farmer
    public long addFarmer(String name, String mobile, String location) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("mobile", mobile);
        cv.put("location", location);
        return db.insert("farmers", null, cv);
    }

    // Add Buyer
    public long addBuyer(String name, String mobile, String location) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("mobile", mobile);
        cv.put("location", location);
        return db.insert("buyers", null, cv);
    }

    // Add Crop
    public void addCrop(int farmerId, String name, String qty, String price) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("farmerId", farmerId);
        cv.put("name", name);
        cv.put("qty", qty);
        cv.put("price", price);
        db.insert("crops", null, cv);
    }

    // Get Crops by Location
    public Cursor getCropsByLocation(String location) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT c.name, c.qty, c.price, f.name AS farmerName " +
                "FROM crops c JOIN farmers f ON c.farmerId = f.id " +
                "WHERE f.location=?";
        return db.rawQuery(query, new String[]{location});
    }

    // Get Scholarships
    public Cursor getScholarships() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM scholarships", null);
    }
}
