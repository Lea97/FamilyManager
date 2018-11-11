package dhbw.familymanager.familymanager;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dhbw.familymanager.familymanager.model.User;


public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    private static final int RC_SIGN_IN = 4711;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth=FirebaseAuth.getInstance();
        findViewById(R.id.profilButton).setOnClickListener(this);
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

                User userModel = new User("", new Date(01,01,01), email, "");
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

        //super.onCreate(savedInstanceState);
        //setContentView(R.layout.login_page);
        //findViewById(R.id.registrationButton).setOnClickListener(this);
       // }

    public void onClick(View view) {
        switch (view.getId()) {
        case R.id.profilButton:
            startActivity(new Intent(MainActivity.this, ProfilActivity.class));
        break;
        }
    }
}

    //public void onClick(View view) {
      //  switch (view.getId()) {
        //    case R.id.registrationButton:
          //      startActivity(new Intent(MainActivity.this, SignUpActivity.class));
            //    break;
        //}
    //}
//}
