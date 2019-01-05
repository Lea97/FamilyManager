package dhbw.familymanager.familymanager;

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

public class CalendarActivity extends AppCompatActivity {
    private WeekView mWeekView;
    private FloatingActionButton addEventButton;
    private Random random = new Random();

    private List<WeekViewEvent> events = createEvents();

    private List<WeekViewEvent> createEvents() {
        List<WeekViewEvent> result = new ArrayList<>();
        Calendar now = new GregorianCalendar();
        Calendar current = (Calendar) now.clone();
        current.add(Calendar.DAY_OF_YEAR, -1);
        current.set(Calendar.HOUR_OF_DAY, 0);
        current.set(Calendar.MINUTE, 0);


        for (int i = 1; i <= 12; i++) {
            String title = "Termin " + i;
            WeekViewEvent e = createEvent(current, title);
            result.add(e);
            current.add(Calendar.HOUR_OF_DAY, 5);
        }

        return result;
    }

    private WeekViewEvent createEvent(Calendar startTime, String title) {

        Calendar startTimeCopy = (Calendar) startTime.clone();
        Calendar endTime = (Calendar) startTime.clone();
        endTime.add(Calendar.HOUR, 1);
        List<WeekViewEvent> events = new ArrayList<>();
        WeekViewEvent e = new WeekViewEvent(random.nextLong(), title, "Test location", startTimeCopy, endTime);
        return e;

    }


    MonthLoader.MonthChangeListener mMonthChangeListener = new MonthLoader.MonthChangeListener() {
        @Override
        public List<WeekViewEvent> onMonthChange(int newYear, final int newMonth) {
            ArrayList<WeekViewEvent> result = new ArrayList<>();
            for (WeekViewEvent e : events) {
                if (e.getStartTime().get(Calendar.MONTH) + 1 == newMonth && e.getStartTime().get(Calendar.YEAR)+1==newYear)
                {
                    result.add(e);
                }
            }
            return result;
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.calendar_menu, menu);
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar);


        // Get a reference for the week view in the layout.
        mWeekView = findViewById(R.id.weekView);
        mWeekView.goToDate(new GregorianCalendar());
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


    //mWeekView.setOnEventClickListener(onEventClickListener);


}





