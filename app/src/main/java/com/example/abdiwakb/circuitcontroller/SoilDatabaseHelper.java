package com.example.abdiwakb.circuitcontroller;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SoilDatabaseHelper extends SQLiteOpenHelper {

    private static final String DBname = "SoilDatabase";
    private static final int DB_Version = 1;

    SoilDatabaseHelper(Context context){

        super(context, DBname, null, DB_Version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create History Database
        String sqlHistory = "CREATE TABLE HISTORY(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "Date TEXT," +
                "MoistureAmount TEXT," +
                "Status TEXT);";

        db.execSQL(sqlHistory);

        //Inserting Default Data
        ContentValues soilHistory = new ContentValues();
        soilHistory.put("Date", "12/03/2020");
        soilHistory.put("MoistureAmount", "78.5%");
        soilHistory.put("Status", "HIGH");

        db.insert("HISTORY", null, soilHistory);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Do Nothing
    }

}
