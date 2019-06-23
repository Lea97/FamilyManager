package dhbw.familymanager.familymanager;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.google.firebase.FirebaseApp;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.List;

import dhbw.familymanager.familymanager.controller.EventRepository;
import dhbw.familymanager.familymanager.model.Event;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class EventRepositoryTest {

    EventRepository codeUnderTest;

    @Before
    public void setup() {
        //FirebaseFirestore.getInstance().
        FirebaseApp.initializeApp(InstrumentationRegistry.getTargetContext());
        codeUnderTest = EventRepository.getInstance(EventRepository.RepositoryMode.TEST);
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

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        assertEquals("dhbw.familymanager.familymanager", appContext.getPackageName());
    }

    @Test
    public void storeEvent() {
        codeUnderTest.storeEvent(getExampleEvent());
        // expect no exception
    }

    public void storeTestEvents() {

    }

    @Test
    public void storeAndReadEvent() {
        codeUnderTest.storeEvent(getExampleEvent());
        //List<Event> events = codeUnderTest.readAllEvents();
        List<Event> events = codeUnderTest.readEventsForUser();

        //assertNotNull (events);
        //assertTrue (events.size()>0);

        Event lastEventInCollection = events.get(events.size() - 1);

        assertEquals(lastEventInCollection.getTitle(), getExampleEvent().getTitle());
        assertEquals(lastEventInCollection.getStart(), getExampleEvent().getStart());
        assertEquals(lastEventInCollection.getEnd(), getExampleEvent().getEnd());
        assertEquals(lastEventInCollection.getId(), getExampleEvent().getId());
        assertEquals(lastEventInCollection.getFamilyId(), getExampleEvent().getFamilyId());
    }
}
