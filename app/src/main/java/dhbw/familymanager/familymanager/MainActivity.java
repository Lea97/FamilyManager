package dhbw.familymanager.familymanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import dhbw.familymanager.familymanager.List.ListActivity;
import dhbw.familymanager.familymanager.Profile.ProfileActivity;
import dhbw.familymanager.familymanager.calendar.CalendarActivity;
import dhbw.familymanager.familymanager.chat.ChatActivity;
import dhbw.familymanager.familymanager.family.AddFamilyActivity;
import dhbw.familymanager.familymanager.family.ShowMemberActivity;
import dhbw.familymanager.familymanager.model.User;
import dhbw.familymanager.familymanager.photoGallery.PhotoGalleryActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int RC_SIGN_IN = 4711;
    private static String currentFamily;
    private static Boolean updateFamilies = false;
    private static Boolean logoutUser = false;
    private FirebaseAuth mAuth;
    private ArrayList<String> items;
    private ArrayList<String> familieIds;
    private ArrayAdapter<String> adapter;
    private FirebaseFirestore db;
    private FirebaseUser user;

    public static void logoutUser() {
        logoutUser = true;
    }

    public static void updateFamiles() {
        updateFamilies = true;
    }

    public static String getFamily() {
        return currentFamily;
    }

    private void setFamily(String family) {
        currentFamily = family;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder().setTimestampsInSnapshotsEnabled(true).build();
        db.setFirestoreSettings(settings);
        mAuth = FirebaseAuth.getInstance();

        setContentView(R.layout.activity_main);
        setFamilies();
        findViewById(R.id.galleryButton).setOnClickListener(this);
        findViewById(R.id.profilButton).setOnClickListener(this);
        findViewById(R.id.listButton).setOnClickListener(this);
        findViewById(R.id.calendarButton).setOnClickListener(this);
        findViewById(R.id.addFamilyButton).setOnClickListener(this);
        findViewById(R.id.logoutButton).setOnClickListener(this);
        findViewById(R.id.memberButton).setOnClickListener(this);
        findViewById(R.id.chat).setOnClickListener(this);
        findViewById(R.id.impressum).setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        user = mAuth.getCurrentUser();
        showLoginDialog();
    }

    private void showLoginDialog() {
        if (user == null) {
            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.EmailBuilder().build(),
                    new AuthUI.IdpConfig.FacebookBuilder().build());

            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .setTheme(R.style.AppTheme)
                            .build(),
                    RC_SIGN_IN);
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (logoutUser) {
            logoutUser = false;
            user = null;
            showLoginDialog();
        }
        if (updateFamilies) {
            updateFamilies = false;
            setFamilies();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                user = FirebaseAuth.getInstance().getCurrentUser();

                DocumentReference docRef = db.collection("users").document(user.getUid());
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                            } else {
                                Log.d("TAG", "No such document");
                                String email = user.getEmail();
                                User userModel = new User(user.getDisplayName(), new Date(01, 00, 01), email, "", "ProfilPictures/profile_picture.png");
                                db.collection("users").document(user.getUid()).set(userModel);
                            }
                        } else {
                            Log.d("TAG", "get failed with ", task.getException());
                        }
                    }
                });
                setFamilies();
            }
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.profilButton:
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                break;
            case R.id.galleryButton:
                startActivity(new Intent(MainActivity.this, PhotoGalleryActivity.class));
                break;
            case R.id.listButton:
                startActivity(new Intent(MainActivity.this, ListActivity.class));
                break;
            case R.id.calendarButton:
                startActivity(new Intent(MainActivity.this, CalendarActivity.class));
                break;
            case R.id.impressum:
                startActivity(new Intent(MainActivity.this, ImpressumActivity.class));
                break;
            case R.id.addFamilyButton:
                startActivity(new Intent(MainActivity.this, AddFamilyActivity.class));
                break;
            case R.id.logoutButton:
                user = null;
                AuthUI.getInstance().signOut(this).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showLoginDialog();
                        Toast.makeText(MainActivity.this, R.string.logout_successful, Toast.LENGTH_LONG).show();
                    }
                });
                break;
            case R.id.memberButton:
                startActivity(new Intent(MainActivity.this, ShowMemberActivity.class));
                break;
            case R.id.chat:
                startActivity(new Intent(MainActivity.this, ChatActivity.class));
                break;
        }
    }

    public void setFamilies() {
        final Spinner dropdown = findViewById(R.id.familySpinner);
        items = new ArrayList<String>();
        familieIds = new ArrayList<String>();

        user = mAuth.getCurrentUser();
        if (user != null) {
            String mail = user.getEmail();
            if (!mail.isEmpty()) {
                Query query = db.collection("families").whereArrayContains("members", mail);

                query.get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot querySnapshot) {

                                List<DocumentSnapshot> documents = querySnapshot.getDocuments();
                                int number = 0;
                                for (DocumentSnapshot document : documents) {
                                    Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                                    familieIds.add(document.getId());
                                    adapter.add(document.get("familyName").toString());
                                    if (document.getId().equals(currentFamily)) {
                                        dropdown.setSelection(number);
                                    }
                                    number++;
                                }
                                if (items.isEmpty()) {
                                    currentFamily = null;
                                    adapter.add("No Family exist");
                                }
                            }
                        });
            }
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
            dropdown.setAdapter(adapter);
            dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    String family = dropdown.getSelectedItem().toString();
                    int pos = dropdown.getSelectedItemPosition();
                    if (familieIds.size() != 0) {
                        setFamily(familieIds.get(pos));
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });
        }
    }
}
