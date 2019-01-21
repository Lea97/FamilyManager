package dhbw.familymanager.familymanager;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;

class AddEventActivity extends AppCompatActivity {

    private static final String TAG="CalendarActivity";
    private CalendarView calendar;
    private Button saveEventBtn;
    private String eventDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.add_event);
        calendar = (CalendarView) findViewById(R.id.calendarView);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                eventDate = year + " /" + dayOfMonth + " /" + (month+1);
                Log.d(TAG, "Selected date: " + eventDate);
            }
        });

        saveEventBtn = findViewById(R.id.saveEvent);
        saveEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("hallo, Event "+ eventDate + " added");
            }
        });


    }}

//}
