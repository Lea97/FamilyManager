package dhbw.familymanager.familymanager.calendar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.RectF;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

import dhbw.familymanager.familymanager.R;
import dhbw.familymanager.familymanager.controller.EventRepository;
import dhbw.familymanager.familymanager.model.Event;

public class CalendarActivity extends AppCompatActivity implements WeekView.EventClickListener {
    private FirebaseFirestore db;
    private WeekView mWeekView;
    private FloatingActionButton addEventButton;
    private Random random = new Random();
    private List<Integer> colors = new ArrayList();
    private List<WeekViewEvent> events = readEvents();
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


    private List<WeekViewEvent> readEvents() {

        final EventRepository repo = EventRepository.getInstance();

        List<Event> userEvents = repo.readEventsForFamily();
        List<WeekViewEvent> result = new ArrayList<>();
        if (!userEvents.isEmpty() && (userEvents != null)) {

            for (Event e : userEvents) {
                WeekViewEvent weekViewEvent = new WeekViewEvent();
                Calendar calStart = Calendar.getInstance();
                Calendar calEnd = Calendar.getInstance();
                calStart.setTime(e.getStart());
                calEnd.setTime(e.getEnd());

                weekViewEvent.setId(e.getId());
                weekViewEvent.setName(e.getTitle());
                weekViewEvent.setStartTime(calStart);
                weekViewEvent.setEndTime(calEnd);
                weekViewEvent.setStartTime(calStart);
                weekViewEvent.setLocation(e.getLocation());
                weekViewEvent.setColor(colors.get(random.nextInt(colors.size())));

                result.add(weekViewEvent);
            }
        }
        return result;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.calendar_menu, menu);

        return true;
    }

    @Override
    public void onResume() {

        super.onResume();
        mWeekView.notifyDatasetChanged();
        startEventReading();
        mWeekView.goToDate(new GregorianCalendar());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.today:
                mWeekView.setNumberOfVisibleDays(1);
                break;
            case R.id.events_three:
                mWeekView.setNumberOfVisibleDays(3);
                break;
            case R.id.events_week:
                mWeekView.setNumberOfVisibleDays(7);
                break;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        mWeekView.notifyDatasetChanged();
        mWeekView.goToDate(new GregorianCalendar());
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setColors();
        setContentView(R.layout.calendar);

        startEventReading();

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
        mWeekView.setMonthChangeListener(mMonthChangeListener);
        mWeekView.setOnEventClickListener(this);
        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CalendarActivity.this, AddEventActivity.class));
            }
        });
    }

    private void setColors() {
        colors.add(getResources().getColor(R.color.peach));
        colors.add(getResources().getColor(R.color.pink));
        colors.add(getResources().getColor(R.color.blue));
        colors.add(getResources().getColor(R.color.red));
        colors.add(getResources().getColor(R.color.green));
        colors.add(getResources().getColor(R.color.darkgreen));
        colors.add(getResources().getColor(R.color.orange));
    }


    private void startEventReading() {
        final Activity thisActivity = this;

        new Thread(new Runnable() {
            public void run() {
                events = readEvents();
                thisActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        mWeekView.notifyDatasetChanged();
                        mWeekView.goToDate(new GregorianCalendar());
                    }
                });
            }
        }).start();
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {

        startActivity(new Intent(CalendarActivity.this, EventDetailsActivity.class));

        Intent intent = new Intent(CalendarActivity.this, EventDetailsActivity.class);
        intent.putExtra("eventId", String.valueOf(event.getId()));
        System.out.println(event.getId());
        CalendarActivity.this.startActivity(intent);

    }

}





