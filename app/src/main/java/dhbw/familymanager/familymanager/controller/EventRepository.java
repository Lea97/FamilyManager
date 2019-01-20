package dhbw.familymanager.familymanager.controller;

import android.provider.ContactsContract;
import android.support.annotation.NonNull;

import com.alamkanak.weekview.WeekViewEvent;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
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


    public static EventRepository getInstance() {
        return getInstance(RepositoryMode.TEST);
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


    private EventRepository(String collectionPath) {
        this.collectionPath = collectionPath;
        db = FirebaseFirestore.getInstance();
    }


    public void storeEvent(Event event) {
        FirebaseAuth auth=FirebaseAuth.getInstance();
        String uid=auth.getCurrentUser().getUid();




        Task<DocumentReference> task = db.collection(collectionPath).add(event);
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

    public List<Event> readEventsForUser() {
        FirebaseAuth auth=FirebaseAuth.getInstance();
        String current=auth.getCurrentUser().getUid();
        Task<QuerySnapshot> task = db.collection(collectionPath).get();




        try {
            QuerySnapshot querySnapshot = Tasks.await(task);
            return querySnapshot.toObjects(Event.class);
        } catch (Exception e) {
            throw new DatabaseCommunicationException("Event reading failed", e);
        }

    }

//}


    public List<Event> readAllEvents() {

        Task<QuerySnapshot> task = db.collection(collectionPath).get();

        try {
            QuerySnapshot querySnapshot = Tasks.await(task);
            return querySnapshot.toObjects(Event.class);
        } catch (Exception e) {
            throw new DatabaseCommunicationException("Event reading failed", e);
        }

    }

}

class DatabaseCommunicationException extends RuntimeException {
    public DatabaseCommunicationException(String message, Throwable cause) {
        super(message, cause);
    }
}