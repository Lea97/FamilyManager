package dhbw.familymanager.familymanager;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

import dhbw.familymanager.familymanager.controller.EventRepository;
import dhbw.familymanager.familymanager.model.Event;

class AddEventActivity extends AppCompatActivity {

    private static final String TAG="CalendarActivity";
    private CalendarView calendar;
    private Button saveEventBtn;
    private Calendar eventStart, end;
    private EditText title;
    private EventRepository repository;
    private CalendarView calendarView;
    private DatePickerDialog datePickerDialog;




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

                eventStart = new GregorianCalendar(year, month, dayOfMonth);





                //eventDate = year + " /" + dayOfMonth + " /" + (month+1);

                //Log.d(TAG, "Selected date: " + eventDate);
            }
        });

        saveEventBtn = findViewById(R.id.saveEvent);
       saveEventBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               final Event e = new Event();
               Random r=new Random();
               GregorianCalendar start=new GregorianCalendar();
               //start.set(calendarView.getDate);

               e.setFamilyId("12345");
               //e.setStart(DatePickerDi);
              // e.setStart(eventStart);

               e.setStart(new Date(2019, 1, 1, 14, 50, 0));
               e.setEnd(new Date(2019, 1, 1, 14, 55, 0));

               e.setUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
               e.setTitle(title.getText().toString());
               e.setId(r.nextLong());

               //e.setEnd(new Date(2018, 1, 1, 16, 55, 0));

               Thread t=new Thread(new Runnable() {
                   @Override
                   public void run() {

                       repository.storeEvent(e);
                   }
               });

            t.start();

           }
       });}


}
