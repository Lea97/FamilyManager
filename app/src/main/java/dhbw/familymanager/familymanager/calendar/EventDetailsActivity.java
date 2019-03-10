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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import dhbw.familymanager.familymanager.R;
import dhbw.familymanager.familymanager.model.Event;

public class EventDetailsActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView eventTitle, eventLocation, eventStart, eventEnd;
    private Button edit, delete;
    private Event event;
    private String eventId, dbEntryId;


    FirebaseFirestore db=FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_details);
        eventTitle=findViewById(R.id.eventTitle1);
        eventLocation=findViewById(R.id.eventLocation);
        eventStart=findViewById(R.id.eventStart);
        eventStart.setOnClickListener(this);
        eventEnd=findViewById(R.id.eventEnd);
        eventEnd.setOnClickListener(this);


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
                        dbEntryId=task.getResult().getDocuments().get(0).getId();


                } else {
                    Log.d("TAG", "Error getting documents: ", task.getException());
                }
            }

        }));
        delete=findViewById(R.id.deleteEvent);
        delete.setOnClickListener(this);
        edit=findViewById(R.id.editEvent);
        edit.setOnClickListener(this);

    }

    private void setEventData() {

        eventTitle.setText(event.getTitle());
        eventLocation.setText(event.getLocation());
        eventStart.setText(event.getStart().toString());
        eventEnd.setText(event.getEnd().toString());
       // eventId= String.valueOf(event.getId());

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case(R.id.deleteEvent):
                deleteEvent(eventId);
                break;
            case(R.id.editEvent):
                startEdit(eventId);
                break;
        }
    }

    private void startEdit(String eventId) {
        Intent intent=new Intent(EventDetailsActivity.this, AddEventActivity.class);
        intent.putExtra("eventId", eventId);
        startActivity(intent);

    }

    private void deleteEvent(String eventId) {


       db.collection("events").document(dbEntryId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
           @Override
           public void onSuccess(Void aVoid) {
               Log.d("TAG", "DocumentSnapshot successfully deleted!");
               System.out.println("Event mit id "+" deleted");
               Toast.makeText(getApplicationContext(), " Event successfully deleted", Toast.LENGTH_SHORT).show();
               startActivity(new Intent(EventDetailsActivity.this, CalendarActivity.class));
           }
       })
               .addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                       Log.w("TAG", "Error deleting document", e);
                   }
               });


    }

    @Override
    public void onBackPressed(){
        startActivity(new Intent(EventDetailsActivity.this, CalendarActivity.class));
    }
}



