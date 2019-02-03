package dhbw.familymanager.familymanager;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import dhbw.familymanager.familymanager.model.User;


public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    private static final int RC_SIGN_IN = 4711;
    private FirebaseAuth mAuth;
    private ArrayList<String> items;
    private ArrayList<String> familieIds;
    private ArrayAdapter<String> adapter;
    private static String currentFamily;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder().setTimestampsInSnapshotsEnabled(true).build();
        firestore.setFirestoreSettings(settings);
        mAuth=FirebaseAuth.getInstance();

        setContentView(R.layout.activity_main);

        findViewById(R.id.profilButton).setOnClickListener(this);
        findViewById(R.id.listButton).setOnClickListener(this);
        findViewById(R.id.calendarButton).setOnClickListener(this);
        findViewById(R.id.addFamilyButton).setOnClickListener(this);
        findViewById(R.id.logoutButton).setOnClickListener(this);
        findViewById(R.id.memberButton).setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.EmailBuilder().build(),
                    new AuthUI.IdpConfig.FacebookBuilder().build());

            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build(),
                    RC_SIGN_IN);
        }
    }

    @Override
    protected void onPostResume(){
        super.onPostResume();
        setFamilies();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                String uid = user.getUid();
                Boolean exists =!db.collection("users").equals(uid);
                if (!exists)
                {
                    String email=user.getEmail();
                    User userModel = new User("", new Date(01,00,01), email, "", "ProfilPictures/profile_picture.png");
                    db.collection("users").document(uid).set(userModel);
                }
            }
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
        case R.id.profilButton:
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            break;
        case R.id.listButton:
            startActivity(new Intent(MainActivity.this, Lists.class));
            break;
        case R.id.calendarButton:
            startActivity(new Intent(MainActivity.this, CalendarActivity.class));
            break;
        case R.id.addFamilyButton:
            startActivity(new Intent(MainActivity.this, AddFamilyActivity.class));
            break;
        case R.id.logoutButton:
            logout();
            break;
        case R.id.memberButton:
            startActivity(new Intent(MainActivity.this, ShowMemberActivity.class));
            break;
        }
    }

    private void logout() {
        AuthUI.getInstance().signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(MainActivity.this,
                                "You have been signed out.",
                                Toast.LENGTH_LONG)
                                .show();
                        // Close activity
                        finish();
                    }
                });
    }

    public void setFamilies(){
        final Spinner dropdown = findViewById(R.id.familySpinner);
        items = new ArrayList<String>();
        familieIds = new ArrayList<String>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null)
        {
            String mail = mAuth.getCurrentUser().getEmail();
            if(mail!= null) {
                Query query = db.collection("families").whereArrayContains("members", mail);

                query.get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot querySnapshot) {

                                List<DocumentSnapshot> documents = querySnapshot.getDocuments();

                                for (DocumentSnapshot document : documents) {
                                    Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                                    familieIds.add(document.getId());
                                    adapter.add(document.get("familyName").toString());
                                }
                                if (items.isEmpty()){
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
                    if(familieIds.size() != 0)
                    {
                        setFamily(familieIds.get(pos));
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {}
            });
        }
    }

    private void setFamily(String family)
    {
        currentFamily = family;
    }

    public static String getFamily(){
        return currentFamily;
    }
}
