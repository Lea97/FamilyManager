package dhbw.familymanager.familymanager.family;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import dhbw.familymanager.familymanager.MainActivity;
import dhbw.familymanager.familymanager.R;


public class AddMemberActivity extends AppCompatActivity implements View.OnClickListener {

    private String currentFamily;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_member);
        currentFamily = MainActivity.getFamily();
        findViewById(R.id.addMember).setOnClickListener(this);
        findViewById(R.id.cancleAddMember).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancleAddMember:
                this.finish();
                break;
            case R.id.addMember:
                addMember();
                break;
        }
    }

    private void addMember() {
        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("families").document(currentFamily);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                        ArrayList<String> members = (ArrayList<String>) document.get("members");
                        EditText editText = findViewById(R.id.newMemberTextfield);
                        members.add(editText.getText().toString());
                        db.collection("families").document(currentFamily).update("members", members);
                        finishCurrentActivity();
                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });
    }

    private void finishCurrentActivity() {
        ShowMemberActivity.update();
        this.finish();
    }
}
