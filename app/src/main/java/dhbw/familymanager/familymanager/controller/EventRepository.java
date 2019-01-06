package dhbw.familymanager.familymanager.controller;

import android.provider.ContactsContract;
import android.support.annotation.NonNull;

import com.alamkanak.weekview.WeekViewEvent;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firestore.v1beta1.WriteResult;

import java.util.ArrayList;
import java.util.List;

import dhbw.familymanager.familymanager.model.Event;

public class EventRepository {

    public enum RepositoryMode {
        PRODUCTIVE, TEST;
    }

    static private String COLLECTION_PATH_PRODUCTIVE_EVENTS = "events";

    static private String COLLECTION_PATH_TEST_EVENTS = "test_events";


    private static EventRepository instance = null;

    FirebaseFirestore db;
    String collectionPath;


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


    private EventRepository(String collectionPath) {
        this.collectionPath = collectionPath;
        db = FirebaseFirestore.getInstance();
    }


    public void storeEvent(Event event) {
        instance=EventRepository.getInstance(RepositoryMode.PRODUCTIVE);

        Task<DocumentReference> task = db.collection(instance.collectionPath).add(event);
        task.addOnFailureListener(new OnFailureListener() {
                                      @Override
                                      public void onFailure(@NonNull Exception e) {
                                          e.printStackTrace();
                                          throw new DatabaseCommunicationException("Event writing failed", e);

                                      }
                                  }
        );

        try {
            Tasks.await(task);
        } catch (Exception e) {
            throw new DatabaseCommunicationException("Event writing failed", e);
        }
    }

    public List<Event> readAllEvents() {
        List<Event> events = new ArrayList<>();

        return events;
    }

}

class DatabaseCommunicationException extends RuntimeException {
    public DatabaseCommunicationException(String message, Throwable cause) {
        super(message, cause);
    }
}