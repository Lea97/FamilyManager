package dhbw.familymanager.familymanager;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import dhbw.familymanager.familymanager.model.User;

public class ShowMemberActivity extends AppCompatActivity {

    FirebaseFirestore db;
    String family;
    ListView simpleList;
    List<User> users;

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

    private void addListAdapter() {
        simpleList = (ListView)findViewById(R.id.simpleListView);
        User[]usersArray = new User[users.size()];
        MembersAdapter membersAdapter = new MembersAdapter(getApplicationContext(), users.toArray(usersArray), this);
        simpleList.setAdapter(membersAdapter);
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

        Query query = db.collection("users");//.whereEqualTo("email",member);
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
                                //user.setBirthday((Date) document.get("birthday"));
                                //TODO
                                user.setName((String) document.get("name"));
                                user.setPhonenumber((String) document.get("phonenumber"));
                                user.setPicturePath((String) document.get("picturePath"));

                                users.add(user);
                                Log.d("MEMBER HINZUGEFUEGT",userMail);
                            }
                        }
                        for (String member : members) {
                            Log.d("MEMBER",member);
                            User user = new User();
                            user.setEmail(member);
                            user.setPicturePath("ProfilPictures/profile_picture.png");
                            users.add(user);
                        }
                        addListAdapter();
                    }
                });
        }
}
