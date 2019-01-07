package dhbw.familymanager.familymanager;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dhbw.familymanager.familymanager.model.Family;
import dhbw.familymanager.familymanager.model.User;


public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    private static final int RC_SIGN_IN = 4711;
    private FirebaseAuth mAuth;
    private Spinner dropdown;
    private ArrayList<String> items;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth=FirebaseAuth.getInstance();
        findViewById(R.id.profilButton).setOnClickListener(this);
        findViewById(R.id.listButton).setOnClickListener(this);
        findViewById(R.id.calendarButton).setOnClickListener(this);
        findViewById(R.id.addFamilyButton).setOnClickListener(this);
        setFamilies();
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

        } else {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            System.out.println("HalloHallo");
            Map<String, Object> city = new HashMap<>();
            city.put("name", "Los Angeles");
            city.put("state", "CA");
            city.put("country", "USA");

            db.collection("cities").document("LA")
                    .set(city)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            System.out.println("Successfully written");
                           //Log.d("iijau", "DocumentSnapshot successfully written!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Log.w("blabla", "Error writing document", e);
                            System.out.println("Failure blabla");
                        }
                    });
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String uid=user.getUid();
                String email=user.getEmail();

                User userModel = new User("", new Date(01,00,01), email, "");
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("users").document(uid).set(userModel);

                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }


    public void onClick(View view) {
        switch (view.getId()) {
        case R.id.profilButton:
            startActivity(new Intent(MainActivity.this, ProfilActivity.class));
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
        }
    }

    private void setFamilies(){

        final Spinner dropdown = findViewById(R.id.familySpinner);

        items = new ArrayList<String>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
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
    }
}
