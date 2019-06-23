package dhbw.familymanager.familymanager.Profile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import dhbw.familymanager.familymanager.R;

public class EditCredentialsActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth auth;
    private EditText emailField;
    private EditText oldPasswordField;
    private EditText newPasswordField;
    private String oldEmail;
    private FirebaseFirestore db;
    private FirebaseUser user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        setContentView(R.layout.change_credentials_page);
        findViewById(R.id.saveCedentialChange).setOnClickListener(this);
        findViewById(R.id.cancleCredentialChange).setOnClickListener(this);
        fillEmailField();
    }

    private void fillEmailField() {
        emailField = findViewById(R.id.emailTextfield);
        emailField.setText(auth.getCurrentUser().getEmail());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancleCredentialChange:
                this.finish();
                break;
            case R.id.saveCedentialChange:
                if (validate()) {
                    saveChanges();
                }
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return true;
    }

    private boolean validate() {
        oldPasswordField = findViewById(R.id.oldPasswordTextfield);
        if (oldPasswordField.getText().toString().isEmpty()) {
            oldPasswordField.setError("Bitte geben Sie ihr aktuelles Passwort ein.");
            return false;
        }
        newPasswordField = findViewById(R.id.newPasswordTextfield);
        EditText repeatNewPasswordField = findViewById(R.id.repeatPasswordTextfield);
        if (!(newPasswordField.getText().toString().isEmpty()) & !(repeatNewPasswordField.getText().toString().isEmpty())) {
            if (!newPasswordField.getText().toString().equals(repeatNewPasswordField.getText().toString())) {
                repeatNewPasswordField.setError("Die Wiederholung des Passwort stimmt nicht mit dem eingegeben überein.");
                return false;
            }
        }
        if (!(newPasswordField.getText().toString().isEmpty()) & (repeatNewPasswordField.getText().toString().isEmpty())) {
            repeatNewPasswordField.setError("Bitte wiederholen Sie ihr neues Passwort.");
            return false;
        }
        if ((newPasswordField.getText().toString().isEmpty()) & !(repeatNewPasswordField.getText().toString().isEmpty())) {
            newPasswordField.setError("Bitte geben Sie ihr neues Passwort ein.");
            return false;
        }
        return true;
    }

    private void saveChanges() {
        user = auth.getCurrentUser();
        oldEmail = user.getEmail();
        AuthCredential credential = EmailAuthProvider.getCredential(oldEmail, oldPasswordField.getText().toString());
        user.reauthenticateAndRetrieveData(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                if (!oldEmail.equals(emailField.getText().toString())) {
                    user.updateEmail(emailField.getText().toString()).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditCredentialsActivity.this, "Die Email-Adresse entspricht nicht dem erwateten Format.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    })
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    changePasswort();
                                    changeEmailInDB();
                                    ProfileActivity.refreshValues();
                                }
                            });
                } else {
                    changePasswort();
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditCredentialsActivity.this, "Das aktuelle Passwort ist nicht korrekt.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void changePasswort() {
        if (!newPasswordField.getText().toString().isEmpty()) {
            user.updatePassword(newPasswordField.getText().toString()).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditCredentialsActivity.this, "Das Passwort konnten nicht geändert werden.", Toast.LENGTH_SHORT).show();
                    return;
                }
            })
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            finish();
                        }
                    });
        } else {
            finish();
        }
    }

    private void changeEmailInDB() {
        db = FirebaseFirestore.getInstance();
        db.collection("users").document(auth.getCurrentUser().getUid()).update("email", emailField.getText().toString());

        Query query = db.collection("families").whereArrayContains("members", oldEmail);
        query.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        List<DocumentSnapshot> documents = querySnapshot.getDocuments();
                        for (DocumentSnapshot document : documents) {
                            Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                            ArrayList<String> members = (ArrayList<String>) document.get("members");
                            members.remove(oldEmail);
                            members.add(emailField.getText().toString());
                            db.collection("families").document(document.getId()).update("members", members);
                        }
                    }
                });
    }
}
