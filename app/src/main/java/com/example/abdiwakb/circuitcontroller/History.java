package com.example.abdiwakb.circuitcontroller;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class History extends AppCompatActivity {

    SQLiteDatabase db;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        //Actionbar Reference
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        //TableLayout and its Components
        TableLayout tableDaily = findViewById(R.id.tableDaily);

        try{
            //Database Helper Class
            SQLiteOpenHelper drugDatabaseHelper = new SoilDatabaseHelper(this);
            db = drugDatabaseHelper.getReadableDatabase();

            //Cursor
            cursor = db.query("HISTORY",
                    new String[] {"_id", "Date", "MoistureAmount", "Status"},
                    null, null, null, null, null);

            if(cursor.getCount() > 0){

                for(int i = 1; i <= cursor.getCount(); i++){

                    cursor.moveToNext();

                    TableRow data = new TableRow(this);
                    data.setPadding(10, 10, 10, 10);

                    if(i%2 == 0){
                        data.setBackgroundColor(Color.parseColor("#d1e0e0"));
                    }

                    //Table Data TextView
                    TextView row_id = new TextView(this);
                    row_id.setText(Integer.toString(cursor.getInt(0)));
                    row_id.setGravity(Gravity.CENTER);

                    TextView row_Date = new TextView(this);
                    row_Date.setText(cursor.getString(1));
                    row_Date.setGravity(Gravity.CENTER);

                    TextView row_Moisture = new TextView(this);
                    row_Moisture.setText(cursor.getString(2));
                    row_Moisture.setGravity(Gravity.CENTER);

                    TextView row_status = new TextView(this);
                    row_status.setText(cursor.getString(3));
                    row_status.setGravity(Gravity.CENTER);

                    data.addView(row_id);
                    data.addView(row_Date);
                    data.addView(row_Moisture);
                    data.addView(row_status);

                    tableDaily.addView(data);
                }
            }
            else{
                Toast.makeText(this, "No Data", Toast.LENGTH_LONG).show();
            }

        }catch(SQLiteException e){
            Toast.makeText(this, "Database Error!", Toast.LENGTH_SHORT).show();
        }


    }
}
