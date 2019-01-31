package dhbw.familymanager.familymanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_members);
        db = FirebaseFirestore.getInstance();
        family = MainActivity.getFamily();
       // new Intent(ShowMemberFragment);

        memberList = new ArrayList<String>();
        getFamilyMembers();
    }

    private void setList() {

        ArrayAdapter<String> memeberlisteAdapter =
                new ArrayAdapter<>(
                        this, // Die aktuelle Umgebung (diese Activity)
                        R.layout.list_item_memberlist, // ID der XML-Layout Datei
                        R.id.list_item_memberlist_textview, // ID des TextViews
                        memberList); // Beispieldaten in einer ArrayList

       // ArrayAdapter adapter = new ArrayAdapter<String>(this,android.R.layout.activity_list_item,memberList);
      //  ListView listView = (ListView) findViewById(R.id.member_list);
       // listView.setAdapter(adapter);

        ListView memberlisteListView = (ListView) findViewById(R.id.listview_members);
        memberlisteListView.setAdapter(memeberlisteAdapter);
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
        TextView text = findViewById(R.id.membersOfFamily);
        ArrayList<String> members = (ArrayList<String>) document.get("members");

        String memberlist = "";
        for (String member:members) {
            this.memberList.add(member);
            memberlist = memberlist + member + ";";
        }

        text.setText(memberlist);
        setList();
    }
}
