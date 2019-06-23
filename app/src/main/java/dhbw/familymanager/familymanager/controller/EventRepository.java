package dhbw.familymanager.familymanager.controller;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import dhbw.familymanager.familymanager.MainActivity;
import dhbw.familymanager.familymanager.model.Event;

public class EventRepository {

    static private String COLLECTION_PATH_PRODUCTIVE_EVENTS = "events";
    static private String COLLECTION_PATH_TEST_EVENTS = "test_events";
    private static EventRepository instance = null;
    FirebaseFirestore db;
    String collectionPath;
    private EventRepository(String collectionPath) {
        this.collectionPath = collectionPath;
        db = FirebaseFirestore.getInstance();
    }

    public static EventRepository getInstance() {
        return getInstance(RepositoryMode.PRODUCTIVE);
    }

    public static EventRepository getInstance(RepositoryMode mode) {
        if (instance == null) {
            String collectionPath;
            if (mode == RepositoryMode.PRODUCTIVE) {
                collectionPath = COLLECTION_PATH_PRODUCTIVE_EVENTS;
            } else {
                collectionPath = COLLECTION_PATH_TEST_EVENTS;
            }
            instance = new EventRepository(collectionPath);
        }
        return instance;
    }

    public void storeEvent(Event event) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String uid = auth.getCurrentUser().getUid();
        Task<DocumentReference> task = db.collection(collectionPath).add(event);

        try {
            Tasks.await(task);
        } catch (Exception e) {
            throw new DatabaseCommunicationException("Event writing failed", e);
        }
    }

    public List<Event> readEventsForFamily() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String familyId = MainActivity.getFamily();
        System.out.println(familyId);

        try {

            Task<QuerySnapshot> task = db.collection(collectionPath).whereEqualTo("familyId", familyId).get();
            QuerySnapshot querySnapshot = Tasks.await(task);

            return querySnapshot.toObjects(Event.class);
        } catch (Exception e) {

            Log.d("TAG", "Event reading failed: ", e);
        }

        return new ArrayList<Event>();
    }

    public List<Event> readAllEvents() {

        Task<QuerySnapshot> task = db.collection(collectionPath).get();

        try {
            QuerySnapshot querySnapshot = Tasks.await(task);
            return querySnapshot.toObjects(Event.class);
        } catch (Exception e) {
            throw new DatabaseCommunicationException("Event reading failed", e);
        }
    }



    public enum RepositoryMode {
        PRODUCTIVE, TEST;
    }
}

class DatabaseCommunicationException extends RuntimeException {
    public DatabaseCommunicationException(String message, Throwable cause) {
        super(message, cause);
    }
}