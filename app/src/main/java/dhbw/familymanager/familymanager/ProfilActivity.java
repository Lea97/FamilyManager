package dhbw.familymanager.familymanager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
        Task<DocumentSnapshot> dbuser = db.collection("users").document(uid).get();
        //User userDate = (User) dbuser;





        //final TextView nameTextfield = (TextView) findViewById(R.id.nameTextfield);
        //nameTextfield.setText("");
        //final TextView emailTextfield = (TextView) findViewById(R.id.emailTextfield);
        //nameTextfield.setText(email);
    }
}
