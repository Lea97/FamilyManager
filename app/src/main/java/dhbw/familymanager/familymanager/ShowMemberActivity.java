package dhbw.familymanager.familymanager;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ShowMemberActivity extends AppCompatActivity {

    FirebaseFirestore db;
    String family;
    List<String> memberList;
    ListView simpleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.members_list_layout);

        memberList = new ArrayList<String>();
        db = FirebaseFirestore.getInstance();
        family = MainActivity.getFamily();
        if(family != null)
        {
            getFamilyMembers();
        }
    }

    private void addListAdapter() {
        simpleList = (ListView)findViewById(R.id.simpleListView);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.list_item_memberlist, R.id.memberTextView, memberList);
        simpleList.setAdapter(arrayAdapter);
    }

    private void getFamilyMembers() {
        DocumentReference docRef = db.collection("families").document(family);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                        setMembers(document);

                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });
    }

    private void setMembers(DocumentSnapshot document) {
        ArrayList<String> members = (ArrayList<String>) document.get("members");

        for (String member:members) {
            this.memberList.add(member);
        }
        addListAdapter();
    }
}
