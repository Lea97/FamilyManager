package dhbw.familymanager.familymanager.calendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import dhbw.familymanager.familymanager.MainActivity;
import dhbw.familymanager.familymanager.R;
import dhbw.familymanager.familymanager.family.AddFamilyActivity;
import dhbw.familymanager.familymanager.model.Event;

class EditEventActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText eventTitle, eventLocation, eventStart, eventEnd;
    private Button edit, cancel;
    private Event event;
    private String eventId, dbEntryId;
    private FirebaseFirestore db=FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_event);
        eventTitle=findViewById(R.id.eventTitle1);
        eventLocation=findViewById(R.id.eventLocation);
        eventStart=findViewById(R.id.eventStart);
        eventStart.setOnClickListener(this);
        eventEnd=findViewById(R.id.eventEnd);
        eventEnd.setOnClickListener(this);
        eventEnd.setOnClickListener(this);
        edit=findViewById(R.id.editEvent);
        edit.setOnClickListener(this);
        cancel=findViewById(R.id.cancelButton);
        cancel.setOnClickListener(this);
        setEventId();

}

    private void setEventId() {
        Intent intent=getIntent();
        eventId=intent.getStringExtra("eventId");
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.editEvent:
                //editEvent(eventId);
                break;
            case R.id.cancel_button:
                //startActivity(new Intent(EditEventActivity.this, EventDetailsActivity.class));
                break;
        }
    }

    private void editEvent(String eventId) {

        db.collection("events").whereEqualTo("id", eventId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                event=task.getResult().toObjects(Event.class).get(0);
                dbEntryId= String.valueOf(event.getId());
                final DocumentReference docRef = db.collection("events").document(dbEntryId);
                docRef.update("title", eventTitle.getText().toString());
                docRef.update("location", eventLocation.getText().toString());
                docRef.update("start", eventStart.getText().toString());




            }
        });

    }
}
