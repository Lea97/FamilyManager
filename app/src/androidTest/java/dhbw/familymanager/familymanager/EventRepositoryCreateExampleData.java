package dhbw.familymanager.familymanager;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.alamkanak.weekview.WeekViewEvent;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

import dhbw.familymanager.familymanager.controller.EventRepository;
import dhbw.familymanager.familymanager.model.Event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

@RunWith(AndroidJUnit4.class)
public class EventRepositoryCreateExampleData {

    EventRepository repository;
    Random random;

    @Before
    public void setup() {

        FirebaseApp.initializeApp(InstrumentationRegistry.getTargetContext());
        repository = EventRepository.getInstance(EventRepository.RepositoryMode.TEST);
        random = new Random();

    }

    Event getExampleEvent() {
        Event event = new Event();
        event.setId(1628787648);
        event.setTitle("TestEventTitle");
        event.setFamilyId("12345");
        event.setStart(new Date(2018, 1, 1, 14, 50, 0));
        event.setEnd(new Date(2018, 1, 1, 14, 55, 0));
        return event;
    }


   @Ignore("Only switch on when you want to generate event data!")
    @Test
    public void createDataInProductiveEventStorage() {
        Calendar now = new GregorianCalendar();
        Calendar current = (Calendar) now.clone();
        current.add(Calendar.MONTH, -1);
        current.set(Calendar.HOUR_OF_DAY, 0);
        current.set(Calendar.MINUTE, 0);


        for (int i = 1; i <= 25; i++) {

            System.out.println("Creating event " + i);
            Event e = new Event();
            e.setStart(current.getTime());
            current.add(Calendar.MINUTE, 55);
            e.setEnd(current.getTime());
            e.setTitle("Termin " + i);
            e.setId(random.nextLong());
            e.setFamilyId("123123");
            repository.storeEvent(e);


            current.add(Calendar.HOUR_OF_DAY, 5);
        }


    }


}
