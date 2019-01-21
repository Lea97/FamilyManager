package dhbw.familymanager.familymanager;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

import dhbw.familymanager.familymanager.controller.EventRepository;
import dhbw.familymanager.familymanager.model.Event;

class AddEventActivity extends AppCompatActivity {

    private static final String TAG="CalendarActivity";
    private CalendarView calendar;
    private Button saveEventBtn;
    private String eventDate;
    private EditText title;
    private EventRepository repository;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_event);
        repository = EventRepository.getInstance(EventRepository.RepositoryMode.PRODUCTIVE);
        title=findViewById(R.id.eventTitle);


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
                Event e =new Event();

                e.setUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
                e.setTitle(title.getText().toString());
               // e.setStart(new Date(2018, 1, 1, 14, 50, 0));
                //e.setEnd(new Date(2018, 1, 1, 16, 55, 0));

                repository.storeEvent(e);









                System.out.println("hallo, Event "+ eventDate + " added");
            }
        });


    }}

//}
