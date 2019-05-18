package dhbw.familymanager.familymanager.calendar;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import dhbw.familymanager.familymanager.DatePickerFragment;
import dhbw.familymanager.familymanager.R;
import dhbw.familymanager.familymanager.TimePickerFragment;
import dhbw.familymanager.familymanager.model.Event;

public class EditEventActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private EditText eventTitle, eventLocation, eventStart, eventEnd;
    private Button edit, cancel;
    private Event event;
    private String eventId, dbEntryId;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private boolean startEvent = false;
    private Calendar start, end;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_event);
        start = new GregorianCalendar();
        end = new GregorianCalendar();

        eventTitle = findViewById(R.id.eventTitle1);
        eventLocation = findViewById(R.id.eventLocation);
        eventStart = findViewById(R.id.eventStart);
        eventStart.setOnClickListener(this);
        eventEnd = findViewById(R.id.eventEnd);
        eventEnd.setOnClickListener(this);
        eventEnd.setOnClickListener(this);
        edit = findViewById(R.id.editEvent);
        edit.setOnClickListener(this);
        cancel = findViewById(R.id.cancelButton);
        cancel.setOnClickListener(this);
        setEventDetails();


    }

    private void setEventDetails() {
        Intent intent = getIntent();
        eventId = intent.getStringExtra("eventId");
        System.out.println(eventId);
        db.collection("events").whereEqualTo("id", Long.parseLong(eventId)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                event = task.getResult().toObjects(Event.class).get(0);
                eventTitle.setText(event.getTitle());
                eventLocation.setText(event.getLocation());
                eventStart.setText(event.getStart().toString());
                eventStart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatePickerFragment fragment = new DatePickerFragment();
                        fragment.show(getFragmentManager(), "datePicker");
                    }
                });
                eventEnd.setText(event.getEnd().toString());
                eventEnd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatePickerFragment fragment = new DatePickerFragment();
                        fragment.show(getFragmentManager(), "datePicker");
                    }
                });
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.editEvent:
                editEvent(eventId);
                break;
            case R.id.cancel_button:
                startActivity(new Intent(EditEventActivity.this, CalendarActivity.class));
                break;
        }
    }

    private void editEvent(String eventId) {

        db.collection("events").whereEqualTo("id", Long.parseLong(eventId)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                System.out.println(eventId);
                dbEntryId = task.getResult().getDocuments().get(0).getId();

                System.out.println(dbEntryId);
                final DocumentReference docRef = db.collection("events").document(dbEntryId);
                Task<Void> task1 = docRef.update(
                        "title", eventTitle.getText().toString(),
                        "location", eventLocation.getText().toString(),
                        "start", new Date(start.get(Calendar.YEAR) - 1900, start.get(Calendar.MONTH), start.get(Calendar.DAY_OF_MONTH), start.get(Calendar.HOUR_OF_DAY), start.get(Calendar.MINUTE)),
                        "end", new Date(end.get(Calendar.YEAR) - 1900, end.get(Calendar.MONTH), end.get(Calendar.DAY_OF_MONTH), end.get(Calendar.HOUR_OF_DAY), end.get(Calendar.MINUTE), 0)).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        startActivity(new Intent(EditEventActivity.this, CalendarActivity.class));
                    }
                });

            }
        });

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        {
            Calendar cal = new GregorianCalendar(year, month, dayOfMonth);
            final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
            if (!startEvent) {
                //if(eventStart.compareTo(Calendar.getInstance())>0)
                start.set(year, month, dayOfMonth);

                TimePickerFragment timePickerFragment = new TimePickerFragment();
                timePickerFragment.show(getFragmentManager(), "datePicker");
                eventStart.setText(dateFormat.format(cal.getTime()));

            } else {

                end.set(year, month, dayOfMonth);
                TimePickerFragment timePickerFragment = new TimePickerFragment();
                timePickerFragment.show(getFragmentManager(), "datePicker");
                eventEnd.setText(dateFormat.format(cal.getTime()));
            }

//        TimePicker mTimePicker = new TimePicker();
            //datePickerDialog.show();
            //((TextView) findViewById(R.id.showDate)).setText(dateFormat.format(cal.getTime()));

        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (!startEvent) {

            start.set(Calendar.HOUR_OF_DAY, hourOfDay);
            start.set(Calendar.MINUTE, minute);
            eventStart.append("; " + hourOfDay + ":" + minute + " Uhr");
            //eventStart.set(hourOfDay, minute);

            startEvent = true;
        } else {
            end.set(Calendar.HOUR_OF_DAY, hourOfDay);
            end.set(Calendar.MINUTE, minute);
            eventEnd.append("; " + hourOfDay + ":" + minute + " Uhr");
        }


    }
}

