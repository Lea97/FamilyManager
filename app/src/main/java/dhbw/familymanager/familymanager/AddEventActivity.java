package dhbw.familymanager.familymanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
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
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Time;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

import dhbw.familymanager.familymanager.controller.EventRepository;
import dhbw.familymanager.familymanager.model.Event;

class AddEventActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private static final String TAG="CalendarActivity";
    private CalendarView calendar;
    private Button saveEventBtn;
    private Calendar eventStart, eventEnd;
    private EditText title;
    private EventRepository repository;
    private boolean startEvent=false;
    private CalendarView calendarView;
    private DatePickerDialog datePickerDialog;
   // private Date startDate, endDate;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_event);
        repository = EventRepository.getInstance(EventRepository.RepositoryMode.PRODUCTIVE);
        title=findViewById(R.id.eventTitle);
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

        eventStart=new GregorianCalendar();
        eventEnd=new GregorianCalendar();
        //calendar = (CalendarView) findViewById(R.id.calendarView);
//        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
           // @Override
           // public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

             //   eventStart = new GregorianCalendar(year, month, dayOfMonth);





                //eventDate = year + " /" + dayOfMonth + " /" + (month+1);

                //Log.d(TAG, "Selected date: " + eventDate);
           // }
        //});

        saveEventBtn = findViewById(R.id.saveEvent);
       saveEventBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               final Event e = new Event();
               Random r=new Random();

              // GregorianCalendar start=new GregorianCalendar();
               //start.set(calendarView.getDate);

               e.setFamilyId("12345");
               //e.setStart(DatePickerDi);
              // e.setStart(eventStart);

               e.setStart(new Date(2019, 1, 1, 14, 50, 0));
               //e.setEnd(new Date(2019, 1, 1, 14, 55, 0));
               e.setStart(new Date(eventStart.get(Calendar.YEAR)-1900, eventStart.get(Calendar.MONTH), eventStart.get(Calendar.DAY_OF_MONTH), eventStart.get(Calendar.HOUR_OF_DAY), eventStart.get(Calendar.MINUTE), 0));
               e.setEnd(new Date(eventEnd.get(Calendar.YEAR)-1900, eventEnd.get(Calendar.MONTH), eventEnd.get(Calendar.DAY_OF_MONTH), eventEnd.get(Calendar.HOUR_OF_DAY), eventEnd.get(Calendar.MINUTE), 0));
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


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar cal = new GregorianCalendar(year, month, dayOfMonth);
        final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
        if(!startEvent) {
            //if(eventStart.compareTo(Calendar.getInstance())>0)
            eventStart.set(year, month, dayOfMonth);

            TimePickerFragment timePickerFragment=new TimePickerFragment();
            timePickerFragment.show(getFragmentManager(), "datePicker");


        }
        else{

            eventEnd.set(year, month,dayOfMonth);
            TimePickerFragment timePickerFragment=new TimePickerFragment();
            timePickerFragment.show(getFragmentManager(), "datePicker");
        }

//        TimePicker mTimePicker = new TimePicker();
        //datePickerDialog.show();
        //((TextView) findViewById(R.id.showDate)).setText(dateFormat.format(cal.getTime()));

    }


    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if(!startEvent){

        eventStart.set(Calendar.HOUR_OF_DAY,hourOfDay);
        eventStart.set(Calendar.MINUTE,minute);

        //eventStart.set(hourOfDay, minute);

        startEvent = true;}
        else{
            eventEnd.set(Calendar.HOUR_OF_DAY,hourOfDay);
            eventEnd.set(Calendar.MINUTE,minute);
        }


    }
}
