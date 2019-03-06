package dhbw.familymanager.familymanager.calendar;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alamkanak.weekview.WeekViewEvent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.Transaction;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.model.Document;
import com.google.type.TimeOfDayOrBuilder;

import dhbw.familymanager.familymanager.R;
import dhbw.familymanager.familymanager.model.Event;

public class EditEventActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText eventTitle, eventLocation, eventStart, eventEnd;
    private Button edit, delete;
    private Event event;
    String eventId;
    FirebaseFirestore db=FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_event);
        eventTitle=findViewById(R.id.eventTitle1);
        eventLocation=findViewById(R.id.eventLocation);
        eventStart=findViewById(R.id.eventStart);
        eventEnd=findViewById(R.id.eventEnd);


       Intent intent=getIntent();
       eventId=intent.getStringExtra("eventId");
       System.out.println(eventId);


        db.collection("events").whereEqualTo("id", Long.parseLong(eventId)).get().
        addOnCompleteListener((new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                        event=task.getResult().toObjects(Event.class).get(0);
                        //Log.d("TAG", document.getId() + " => " + document.getData());
                        setEventData();

                } else {
                    Log.d("TAG", "Error getting documents: ", task.getException());
                }
            }
        }));

    }

    private void setEventData() {

        eventTitle.setText(event.getTitle());
        eventLocation.setText(event.getLocation());
        eventStart.setText(event.getStart().toString());
        eventEnd.setText(event.getEnd().toString());
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case(R.id.deleteEvent):
                deleteEvent(eventId);
                break;
            case(R.id.editEvent):
                editEvent(eventId);
                break;
        }
    }

    private void editEvent(String eventId) {
        //TODO edit event
    }
//TODO fix bugs in deleteEvent
    private void deleteEvent(String eventId) {
       String id=db.collection("events").whereEqualTo("id",Long.parseLong(eventId)).get().getResult().getDocuments().get(0).getId();
       db.collection("events").document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
           @Override
           public void onSuccess(Void aVoid) {
               Log.d("TAG", "DocumentSnapshot successfully deleted!");
               System.out.println("Event mit id "+" deleted");
               //TODO toast message
           }
       })
               .addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                       Log.w("TAG", "Error deleting document", e);
                   }
               });


    }}



