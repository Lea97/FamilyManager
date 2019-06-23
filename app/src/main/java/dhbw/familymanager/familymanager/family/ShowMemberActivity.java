package dhbw.familymanager.familymanager.family;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

import dhbw.familymanager.familymanager.MainActivity;
import dhbw.familymanager.familymanager.Profile.ProfileActivity;
import dhbw.familymanager.familymanager.R;
import dhbw.familymanager.familymanager.adapter.MembersAdapter;
import dhbw.familymanager.familymanager.model.User;

public class ShowMemberActivity extends AppCompatActivity {

    private static boolean update = false;
    private FirebaseFirestore db;
    private String family;
    private ListView listView;
    private List<User> users;
    private ArrayList<String> members;
    private FirebaseAuth auth;

    public static void update() {
        update = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        users = new ArrayList<User>();
        db = FirebaseFirestore.getInstance();
        family = MainActivity.getFamily();
        if (family != null) {
            setContentView(R.layout.members_list_layout);
            getFamilyMembers();
        } else {
            setContentView(R.layout.empty_page);
            Toast.makeText(ShowMemberActivity.this, "Wählen Sie eine Familie um die Funktionalitäten zu nutzen.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (update) {
            update = false;
            users.clear();
            getFamilyMembers();
        }
    }

    private void addListAdapter() {
        listView = (ListView) findViewById(R.id.simpleListView);
        User[] usersArray = new User[users.size()];
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
        members = (ArrayList<String>) document.get("members");

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
                                Timestamp birth = (Timestamp) document.get("birthday");
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
    public boolean onCreateOptionsMenu(final Menu menu) {
        if (family != null) {
            auth = FirebaseAuth.getInstance();
            DocumentReference docRef = db.collection("families").document(family);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                            String owner = document.get("owner").toString();
                            if (owner.equals(auth.getCurrentUser().getUid())) {
                                getMenuInflater().inflate(R.menu.family_menu_owner, menu);
                            } else {
                                getMenuInflater().inflate(R.menu.family_menu, menu);
                            }
                        } else {
                            Log.d("TAG", "No such document");
                        }
                    } else {
                        Log.d("TAG", "get failed with ", task.getException());
                    }
                }
            });
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_member:
                Intent intent = new Intent(ShowMemberActivity.this, AddMemberActivity.class);
                startActivity(intent);
                break;
            case R.id.leave_family:
                showAlertDialog();
                break;
            case R.id.delete_family:
                showDeleteAlertDialog();
                break;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return true;
    }

    private void showDeleteAlertDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Wollen Sie sicher die Familie löschen? Dies kann nicht rückgängig gemacht werden.");
        alertDialogBuilder.setCancelable(true);

        alertDialogBuilder.setPositiveButton(
                "Löschen",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        deleteFamily();
                    }
                });

        alertDialogBuilder.setNegativeButton(
                "Abbrechen",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void deleteFamily() {
        db.collection("families").document(family).delete();
        DocumentReference docRef = db.collection("gallery").document(family);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                        ArrayList<String> folders = (ArrayList<String>) document.get("folderName");
                        deleteFolders(folders);
                        db.collection("gallery").document(family).delete();
                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });
        MainActivity.updateFamiles();
        finish();
    }

    private void deleteFolders(ArrayList<String> folders) {
        for (final String folder : folders) {
            DocumentReference docRef = db.collection("folders").document(family + folder);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                            ArrayList<String> pictures = (ArrayList<String>) document.get("photos");
                            deletePhotos(pictures, folder);
                            db.collection("folders").document(family + folder).delete();
                        } else {
                            Log.d("TAG", "No such document");
                        }
                    } else {
                        Log.d("TAG", "get failed with ", task.getException());
                    }
                }
            });
        }
    }

    private void deletePhotos(ArrayList<String> pictures, final String folder) {
        for (final String photo : pictures) {
            DocumentReference docRef = db.collection("photos").document(family + folder + photo);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            storage.getReference(document.get("path").toString()).delete();
                            db.collection("photos").document(family + folder + photo).delete();
                        } else {
                            Log.d("TAG", "No such document");
                        }
                    } else {
                        Log.d("TAG", "get failed with ", task.getException());
                    }
                }
            });
        }
    }

    private void showAlertDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Wollen Sie wirklich die Familie verlassen?");
        alertDialogBuilder.setCancelable(true);

        alertDialogBuilder.setPositiveButton(
                "Verlassen",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        leaveFamily();
                    }
                });

        alertDialogBuilder.setNegativeButton(
                "Abbrechen",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void leaveFamily() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        DocumentReference docRef = db.collection("families").document(MainActivity.getFamily());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                        deleteMember(document);
                        finishActivityPage();
                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });
    }

    private void finishActivityPage() {
        MainActivity.updateFamiles();
        this.finish();
    }

    private void deleteMember(DocumentSnapshot document) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        ArrayList<String> memberList = (ArrayList<String>) document.get("members");
        memberList.remove(auth.getCurrentUser().getEmail());
        db.collection("families").document(family).update("members", memberList);
    }
}
