package dhbw.familymanager.familymanager;

import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Console;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class ProfilActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profil);
        setValues();
        findViewById(R.id.changeProfilButton).setOnClickListener(this);

    }

    private void setValues() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        //FirebaseAuth.getInstance().getCurrentUser().updateEmail("test@web.de");
        String userId = user.getUid();

        DocumentReference docRef = db.collection("users").document(userId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                        fillFormular(document);

                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });
    }

    private void fillFormular(DocumentSnapshot document) {
        String userName = (String) document.get("name");
        final TextView nameTextfield = (TextView) findViewById(R.id.nameTextfield);
        nameTextfield.setText(userName);

        String userMail = (String) document.get("email");
        final TextView mailTextfield = (TextView) findViewById(R.id.emailTextfield);
        mailTextfield.setText(userMail);

        Timestamp userbirth = (Timestamp) document.get("birthday");
        final TextView birthTextfield = (TextView) findViewById(R.id.birthTextfield);
        if (userbirth != null)
        {
            Log.d("DAAAAAAAAAAAAAAAAAAATE", userbirth.toString());
            //birthTextfield.setText(userbirth.toDate().getDay()+ "." + userbirth.toDate().getMonth() +"."+ userbirth.toDate().getYear());
            //birthTextfield.setText(userbirth.toString());
        }
        Log.d("DAAAAAAAAAAAAAAAAAAATE", "NUUUUUUUUUUUUUUUUUUUUUll");

        String userNumber = (String) document.get("phonenumber");
        final TextView numberTextfield = (TextView) findViewById(R.id.numberTextfield);
        numberTextfield.setText(userNumber);

        //User userModel = new User("", new Date(01,01,01), email, "");
        //FirebaseFirestore db = FirebaseFirestore.getInstance();
        //db.collection("users").document(uid).set(userModel);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.changeProfilButton:
                setContentView(R.layout.edit_profil);
                findViewById(R.id.saveButton).setOnClickListener(this);
                findViewById(R.id.cancelButton).setOnClickListener(this);
                setValues();
                break;
            case R.id.cancelButton:
                setContentView(R.layout.profil);
                findViewById(R.id.changeProfilButton).setOnClickListener(this);
                setValues();
                break;
            case R.id.saveButton:
                saveProfilChanges();
                setContentView(R.layout.profil);
                findViewById(R.id.changeProfilButton).setOnClickListener(this);
                setValues();
                break;
        }
    }

    private void saveProfilChanges() {
        //final TextView birthTextfield = (TextView) findViewById(R.id.birthTextfield);
        final TextView date = (TextView) findViewById(R.id.showDate);
        final TextView nameTextield = (TextView) findViewById(R.id.nameTextfield);
        final TextView emailTextfield = (TextView) findViewById(R.id.emailTextfield);
        final TextView numberTextfield = (TextView) findViewById(R.id.numberTextfield);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(user.getUid()).update("name", nameTextield.getText().toString(),"phonenumber", numberTextfield.getText().toString(), "email", emailTextfield.getText().toString());
        // "birthday",(Date) date.getText(),
       // user.updateEmail(emailTextfield.getText().toString());
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar cal = new GregorianCalendar(year, month, day);
        this.setDate(cal);
    }

    private void setDate(final Calendar calendar) {
        final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
        ((TextView) findViewById(R.id.showDate)).setText(dateFormat.format(calendar.getTime()));
    }

    public void datePicker(View view){

        DatePickerFragment fragment = new DatePickerFragment();
       // fragment.show(getSupportFragmentManager(), &quot;date&quot;);
        fragment.show(getFragmentManager(), "datePicker");
    }
}
