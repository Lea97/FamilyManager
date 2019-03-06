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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.Transaction;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import dhbw.familymanager.familymanager.R;
import dhbw.familymanager.familymanager.model.Event;

public class EditEventActivity extends AppCompatActivity {
    private EditText eventTitle, eventLocation, eventStart, eventEnd;
    private Button edit, delete;
    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_event);
        eventTitle=findViewById(R.id.eventTitle1);
        eventLocation=findViewById(R.id.eventLocation);
        eventStart=findViewById(R.id.eventStart);
        eventEnd=findViewById(R.id.eventEnd);


       Intent intent=getIntent();
       String eventId=intent.getStringExtra("eventId");
       System.out.println(eventId);
       FirebaseFirestore db=FirebaseFirestore.getInstance();

        final Task<QuerySnapshot> docRef = db.collection("events").whereEqualTo("id", eventId).get();
        docRef.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                event=docRef.getResult().toObjects(Event.class).get(0);
                setEventData();
                }

            }
        });}










    private void setEventData() {

        eventTitle.setText(event.getTitle());
        eventLocation.setText(event.getLocation());
        eventStart.setText(event.getStart().toString());
        eventEnd.setText(event.getEnd().toString());
    }
}
