package dhbw.familymanager.familymanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import dhbw.familymanager.familymanager.controller.EventRepository;
import dhbw.familymanager.familymanager.model.Event;

public class CalendarActivity extends AppCompatActivity {
    private WeekView mWeekView;
    private FloatingActionButton addEventButton;
    private Random random = new Random();
    private int calendarType;




    private List<WeekViewEvent> events = new ArrayList<>();

    private List<WeekViewEvent> readEvents() {

        EventRepository repo = EventRepository.getInstance();

        // TODO: read user-specific events
        List<Event> events = repo.readAllEvents();
        List<Event> userSpecificEvents=repo.readEventsForUser();
        List<WeekViewEvent> result = new ArrayList<>();

        for (Event e : events) {
            WeekViewEvent weekViewEvent = new WeekViewEvent();

            Calendar calStart = Calendar.getInstance();
            Calendar calEnd = Calendar.getInstance();

            calStart.setTime(e.getStart());
            calEnd.setTime(e.getEnd());
            weekViewEvent.setId(e.getId());
            weekViewEvent.setName(e.getTitle());
            weekViewEvent.setStartTime(calStart);
            weekViewEvent.setEndTime(calEnd);

            result.add(weekViewEvent);
        }
        return result;
    }


    MonthLoader.MonthChangeListener mMonthChangeListener = new MonthLoader.MonthChangeListener() {
        @Override
        public List<WeekViewEvent> onMonthChange(int newYear, final int newMonth) {
            ArrayList<WeekViewEvent> result = new ArrayList<>();
            for (WeekViewEvent e : events) {
                if (e.getStartTime().get(Calendar.MONTH) + 1 == newMonth && e.getStartTime().get(Calendar.YEAR) == newYear) {
                    result.add(e);
                }
            }
            System.out.println("Delivering " + result.size() + "events for " + newYear + " month " + newMonth + " out of " + events.size());
            return result;
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.calendar_menu, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.today:
                mWeekView.setNumberOfVisibleDays(1);
                break;
            case R.id.events_three:
                mWeekView.setNumberOfVisibleDays(3);
                break;
            case R.id.events_week:
                mWeekView.setNumberOfVisibleDays(7);
                break;
        }
        mWeekView.notifyDatasetChanged();
        mWeekView.goToDate(new GregorianCalendar());
        return true;

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar);


        startEventReading();

        // Get a reference for the week view in the layout.
        mWeekView = findViewById(R.id.weekView);
        mWeekView.goToDate(new GregorianCalendar());
        mWeekView.setNumberOfVisibleDays(3);
        mWeekView.setDateTimeInterpreter(new DateTimeInterpreter() {
            @Override
            public String interpretDate(Calendar date) {
                return date.get(Calendar.DAY_OF_MONTH) + "." + (date.get(Calendar.MONTH) + 1) + ".";
            }

            @Override
            public String interpretTime(int hour) {
                return hour + ":00";
            }
        });
        mWeekView.showContextMenu();


        addEventButton = findViewById(R.id.addEventButton);

// The week view has infinite scrolling horizontally. We have to provide the events of a
// month every time the month changes on the week view.
        mWeekView.setMonthChangeListener(mMonthChangeListener);


        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CalendarActivity.this, AddEventActivity.class));

            }
        });


    }

    private void startEventReading() {
        final Activity thisActivity = this;

        new Thread(new Runnable() {
            public void run() {
                System.out.println("Reading events");
                events = readEvents();
                System.out.println("Events read: " + events.size());



               thisActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        System.out.println("Refreshing calendar");
                        // because there is no method to refresh calendar
                      //  mWeekView.goToDate(new GregorianCalendar(2000, 5, 5));
                       // mWeekView.goToDate(new GregorianCalendar());

                        mWeekView.notifyDatasetChanged();
                        mWeekView.goToDate(new GregorianCalendar());

                    }
                });

            }
        }).start();


    }


    //mWeekView.setOnEventClickListener(onEventClickListener);


}





