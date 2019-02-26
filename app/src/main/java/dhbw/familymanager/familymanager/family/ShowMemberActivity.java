package dhbw.familymanager.familymanager.family;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import dhbw.familymanager.familymanager.MainActivity;
import dhbw.familymanager.familymanager.Profile.ProfileActivity;
import dhbw.familymanager.familymanager.R;
import dhbw.familymanager.familymanager.adapter.MembersAdapter;
import dhbw.familymanager.familymanager.model.User;

public class ShowMemberActivity extends AppCompatActivity {

    FirebaseFirestore db;
    String family;
    ListView listView;
    List<User> users;
    private static boolean update = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.members_list_layout);

        users = new ArrayList<User>();
        db = FirebaseFirestore.getInstance();
        family = MainActivity.getFamily();
        if(family != null)
        {
            getFamilyMembers();
        }
    }

    public static void update(){
        update = true;
    }

    @Override
    protected void onPostResume(){
        super.onPostResume();
        if (update)
        {
            update = false;
            users.clear();
            getFamilyMembers();
        }
    }

    private void addListAdapter() {
        listView = (ListView)findViewById(R.id.simpleListView);
        User[]usersArray = new User[users.size()];
        MembersAdapter membersAdapter = new MembersAdapter(getApplicationContext(), users.toArray(usersArray), this);
        listView.setAdapter(membersAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User user = users.get(position);
                Intent intent = new Intent(ShowMemberActivity.this, ProfileActivity.class);
                intent.putExtra("userObject", user);
                startActivity(intent);
            }
        });
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
        final ArrayList<String> members = (ArrayList<String>) document.get("members");

        Query query = db.collection("users");
        query.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        List<DocumentSnapshot> documents = querySnapshot.getDocuments();
                        for (DocumentSnapshot document : documents) {
                            Log.d("TAG", "DocumentSnapshot data: " + document.getData());

                            String userMail = (String) document.get("email");
                            if (members.contains(userMail)) {
                                members.remove(userMail);
                                User user = new User();
                                user.setEmail(userMail);
                                Timestamp birth = (Timestamp)document.get("birthday");
                                user.setBirthday(birth.toDate());
                                user.setName((String) document.get("name"));
                                user.setPhonenumber((String) document.get("phonenumber"));
                                user.setPicturePath((String) document.get("picturePath"));

                                users.add(user);
                            }
                        }
                        for (String member : members) {
                            User user = new User();
                            user.setEmail(member + "  (only invited)");
                            user.setPicturePath("ProfilPictures/profile_picture.png");
                            users.add(user);
                        }
                        addListAdapter();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.family_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.add_member:
                Intent intent = new Intent(ShowMemberActivity.this, AddMemberActivity.class);
                startActivity(intent);
                break;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return true;
    }
}
