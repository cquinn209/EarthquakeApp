package org.me.gcu.equakestartercode;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CalendarView;

import java.util.Calendar;
import java.util.Date;

//Conor Quinn
//S1705540


public class CalendarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);


        CalendarView calendarView = findViewById(R.id.calendarView);


        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int i, int i1, int i2) {

                String date = i + "/" +  i1+"/"+ i2;



                Intent intent = new Intent(CalendarActivity.this, MainActivity.class);

                intent.putExtra("date", date);

                        startActivity(intent);
            }
        });





    }












}