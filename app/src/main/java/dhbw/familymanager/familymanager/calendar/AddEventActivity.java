package dhbw.familymanager.familymanager.calendar;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

import dhbw.familymanager.familymanager.DatePickerFragment;
import dhbw.familymanager.familymanager.MainActivity;
import dhbw.familymanager.familymanager.R;
import dhbw.familymanager.familymanager.TimePickerFragment;
import dhbw.familymanager.familymanager.controller.EventRepository;
import dhbw.familymanager.familymanager.model.Event;

public class AddEventActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private static final String TAG="CalendarActivity";
    private CalendarView calendar;
    private Button saveEventBtn;
    private Calendar eventStart, eventEnd;
    private EditText title,location;
    private EventRepository repository;
    private boolean startEvent=false;
    private CalendarView calendarView;
    private DatePickerDialog datePickerDialog;
    private String eventId;
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private Event event;
    private String dbEntryId;
   // private Date startDate, endDate;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent=getIntent();
        eventId=intent.getStringExtra("eventId");
        setContentView(R.layout.add_event);

        repository = EventRepository.getInstance(EventRepository.RepositoryMode.PRODUCTIVE);
        title=findViewById(R.id.eventTitle);
        location=findViewById(R.id.eventLocation);
        Button start=findViewById(R.id.eventStart);
        Button end=findViewById(R.id.end);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment fragment = new DatePickerFragment();
                fragment.show(getFragmentManager(), "datePicker");

            }
        });

        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment fragment = new DatePickerFragment();
                fragment.show(getFragmentManager(), "datePicker");

            }
        });

        Button cancle = findViewById(R.id.cancelEvent);
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        eventStart=new GregorianCalendar();
        eventEnd=new GregorianCalendar();


        saveEventBtn = findViewById(R.id.saveEvent);
       saveEventBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               //if(eventId!=null){
                   //editEvent(eventId);
              // }else{
                   createNewEvent();
                   finish();
              // }



           }
       });}

    private void createNewEvent() {
        final Event e = new Event();
        Random r=new Random();
        e.setFamilyId(MainActivity.getFamily());
        //e.setStart(new Date(2019, 1, 1, 14, 50, 0));
        //e.setEnd(new Date(2019, 1, 1, 14, 55, 0));
        e.setStart(new Date(eventStart.get(Calendar.YEAR)-1900, eventStart.get(Calendar.MONTH), eventStart.get(Calendar.DAY_OF_MONTH), eventStart.get(Calendar.HOUR_OF_DAY), eventStart.get(Calendar.MINUTE), 0));
        e.setEnd(new Date(eventEnd.get(Calendar.YEAR)-1900, eventEnd.get(Calendar.MONTH), eventEnd.get(Calendar.DAY_OF_MONTH), eventEnd.get(Calendar.HOUR_OF_DAY), eventEnd.get(Calendar.MINUTE), 0));
        e.setUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
        e.setTitle(title.getText().toString());
        e.setId(r.nextLong());
        e.setLocation(location.getText().toString());


        Thread t=new Thread(new Runnable() {
            @Override
            public void run() {

                repository.storeEvent(e);
            }
        });

        t.start();
    }

    private void editEvent(String eventId) {

         db.collection("events").whereEqualTo("id", eventId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                event = task.getResult().toObjects(Event.class).get(0);
                dbEntryId = String.valueOf(event.getId());
                final DocumentReference docRef = db.collection("events").document(dbEntryId);
                docRef.update("title", title.getText().toString());
                docRef.update("location", location.getText().toString());
                docRef.update("start", new Date(eventStart.get(Calendar.YEAR) - 1900, eventStart.get(Calendar.MONTH), eventStart.get(Calendar.DAY_OF_MONTH), eventStart.get(Calendar.HOUR_OF_DAY), eventStart.get(Calendar.MINUTE), 0));
                docRef.update("end", new Date(eventEnd.get(Calendar.YEAR) - 1900, eventEnd.get(Calendar.MONTH), eventEnd.get(Calendar.DAY_OF_MONTH), eventEnd.get(Calendar.HOUR_OF_DAY), eventEnd.get(Calendar.MINUTE), 0));


            }



        });}

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        {
            Calendar cal = new GregorianCalendar(year, month, dayOfMonth);
            final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
            if (!startEvent) {
                //if(eventStart.compareTo(Calendar.getInstance())>0)
                eventStart.set(year, month, dayOfMonth);

                TimePickerFragment timePickerFragment = new TimePickerFragment();
                timePickerFragment.show(getFragmentManager(), "datePicker");
                ((TextView) findViewById(R.id.showStartDate)).setText(dateFormat.format(cal.getTime()));

            } else {

                eventEnd.set(year, month, dayOfMonth);
                TimePickerFragment timePickerFragment = new TimePickerFragment();
                timePickerFragment.show(getFragmentManager(), "datePicker");
                ((TextView) findViewById(R.id.showEndDate)).setText(dateFormat.format(cal.getTime()));
            }

//        TimePicker mTimePicker = new TimePicker();
            //datePickerDialog.show();
            //((TextView) findViewById(R.id.showDate)).setText(dateFormat.format(cal.getTime()));

        }

    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (!startEvent) {

            eventStart.set(Calendar.HOUR_OF_DAY, hourOfDay);
            eventStart.set(Calendar.MINUTE, minute);
            TextView startTime = (TextView) findViewById(R.id.showStartDate);
            startTime.setText((String) startTime.getText()+ "; " + hourOfDay + ":" + minute + " Uhr");
            //eventStart.set(hourOfDay, minute);

            startEvent = true;
        } else {
            eventEnd.set(Calendar.HOUR_OF_DAY, hourOfDay);
            eventEnd.set(Calendar.MINUTE, minute);
            TextView endTime = (TextView) findViewById(R.id.showEndDate);
            endTime.setText((String) endTime.getText()+ "; " + hourOfDay + ":" + minute + " Uhr");
        }


    }
}


