package dhbw.familymanager.familymanager;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import dhbw.familymanager.familymanager.R;
import dhbw.familymanager.familymanager.model.User;

public class ProfilActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profil);
        setValues();
    }

    private void setValues() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid=user.getUid();
        //Task<DocumentSnapshot> dbuser = db.collection("users").document(uid).get();

       // User userInfo = new User();

        DocumentReference docRef = db.collection("users").document(uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });


        //db.collection("users").document(uid).get().isCanceled(e => {});
        //User userDate = (User) dbuser;


        //final TextView nameTextfield = (TextView) findViewById(R.id.nameTextfield);
        //nameTextfield.setText("");
        //final TextView emailTextfield = (TextView) findViewById(R.id.emailTextfield);
        //nameTextfield.setText(email);
    }
}
