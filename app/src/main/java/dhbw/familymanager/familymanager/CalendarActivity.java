package dhbw.familymanager.familymanager;

import android.graphics.RectF;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toolbar;

import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class CalendarActivity extends AppCompatActivity {
    private WeekView mWeekView;

    MonthLoader.MonthChangeListener mMonthChangeListener = new MonthLoader.MonthChangeListener() {
        @Override
        public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {
            // Populate the week view with some events.
            Calendar now = new GregorianCalendar();
            Calendar nowplusone = (Calendar) now.clone();
            nowplusone.add(Calendar.HOUR, 1);
            List<WeekViewEvent> events = new ArrayList<>();
            if (newMonth == now.get(Calendar.MONTH)) {
                WeekViewEvent e = new WeekViewEvent(4711, "Tralala", "Hopala", now, nowplusone);
                events.add(e);
            }
            return events;
        }
    };


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



// The week view has infinite scrolling horizontally. We have to provide the events of a
// month every time the month changes on the week view.
        mWeekView.setMonthChangeListener(mMonthChangeListener);


    }

}



