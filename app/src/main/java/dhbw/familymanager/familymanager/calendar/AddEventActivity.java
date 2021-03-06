package dhbw.familymanager.familymanager.calendar;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

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

    private static final String TAG = "CalendarActivity";
    private CalendarView calendar;
    private Button saveEventBtn;
    private Calendar eventStart, eventEnd;
    private EditText title, location;
    private EventRepository repository;
    private boolean startEvent = false;
    private String eventId;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Event event;
    private String dbEntryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        eventId = intent.getStringExtra("eventId");
        setContentView(R.layout.add_event);

        repository = EventRepository.getInstance(EventRepository.RepositoryMode.PRODUCTIVE);
        title = findViewById(R.id.eventTitle);
        location = findViewById(R.id.eventLocation);
        Button start = findViewById(R.id.eventStart);
        Button end = findViewById(R.id.end);
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

        eventStart = new GregorianCalendar();
        eventEnd = new GregorianCalendar();

        saveEventBtn = findViewById(R.id.saveEvent);
        saveEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewEvent();
                finish();
            }
        });
    }

    private void createNewEvent() {
        final Event e = new Event();
        Random r = new Random();
        e.setFamilyId(MainActivity.getFamily());
        e.setStart(new Date(eventStart.get(Calendar.YEAR) - 1900, eventStart.get(Calendar.MONTH), eventStart.get(Calendar.DAY_OF_MONTH), eventStart.get(Calendar.HOUR_OF_DAY), eventStart.get(Calendar.MINUTE), 0));
        e.setEnd(new Date(eventEnd.get(Calendar.YEAR) - 1900, eventEnd.get(Calendar.MONTH), eventEnd.get(Calendar.DAY_OF_MONTH), eventEnd.get(Calendar.HOUR_OF_DAY), eventEnd.get(Calendar.MINUTE), 0));
        e.setUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
        e.setTitle(title.getText().toString());
        e.setId(r.nextLong());
        e.setLocation(location.getText().toString());

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                repository.storeEvent(e);
            }
        });

        t.start();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        Calendar cal = new GregorianCalendar(year, month, dayOfMonth);
        final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
        if (!startEvent) {
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
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (!startEvent) {

            eventStart.set(Calendar.HOUR_OF_DAY, hourOfDay);
            eventStart.set(Calendar.MINUTE, minute);
            TextView startTime = (TextView) findViewById(R.id.showStartDate);
            startTime.setText((String) startTime.getText() + "; " + hourOfDay + ":" + minute + " Uhr");
            startEvent = true;
        } else {
            eventEnd.set(Calendar.HOUR_OF_DAY, hourOfDay);
            eventEnd.set(Calendar.MINUTE, minute);
            TextView endTime = (TextView) findViewById(R.id.showEndDate);
            endTime.setText((String) endTime.getText() + "; " + hourOfDay + ":" + minute + " Uhr");
        }
    }
}


