package dhbw.familymanager.familymanager;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class ProfileActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private FirebaseUser user;
    private Boolean editmode;
    private ImageView mImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setValues();
        setContentView(R.layout.profil);
        showPicture();
        editmode = false;
        findViewById(R.id.changeProfilButton).setOnClickListener(this);
    }


    private void showPicture() {
        //FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        //StorageReference storageRef = firebaseStorage.getReference("profile_picture.jpg");

        //StorageReference storageReference = firebaseStorage.getReferenceFromUrl("gs://familymanager-7cbd8.appspot.com/profile_picture.png");
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("gitlab.PNG");

        ImageView imageView = findViewById(R.id.imageView);

        Glide.with(this /* context */).load(storageReference).into(imageView);

        //mImageView = (ImageView) findViewById(R.id.imageView);
        //mImageView.setImageResource(R.drawable.profile_picture2);
        //mImageView.setImageBitmap(BitmapFactory.decodeFile(storageRef.getPath()));
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
        final TextView birthTextfield = (TextView) findViewById(R.id.showDate);
        if (userbirth != null)
        {
            final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
            if (editmode == false)
            {
                ((TextView) findViewById(R.id.birthTextfield)).setText(dateFormat.format(userbirth.toDate()));
            }
            else
            {
                ((TextView) findViewById(R.id.showDate)).setText(dateFormat.format(userbirth.toDate()));
            }
        }
        String userNumber = (String) document.get("phonenumber");
        final TextView numberTextfield = (TextView) findViewById(R.id.numberTextfield);
        numberTextfield.setText(userNumber);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.changeProfilButton:
                setContentView(R.layout.edit_profil);
                editmode = true;
                findViewById(R.id.saveButton).setOnClickListener(this);
                findViewById(R.id.cancelButton).setOnClickListener(this);
                setValues();
                break;
            case R.id.cancelButton:
                setContentView(R.layout.profil);
                editmode = false;
                findViewById(R.id.changeProfilButton).setOnClickListener(this);
                setValues();
                break;
            case R.id.saveButton:
                saveProfilChanges();
                setContentView(R.layout.profil);
                editmode = false;
                findViewById(R.id.changeProfilButton).setOnClickListener(this);
                setValues();
                break;
        }
    }

    private void saveProfilChanges() {
        final TextView dateField = (TextView) findViewById(R.id.showDate);
        final TextView nameTextield = (TextView) findViewById(R.id.nameTextfield);
        final TextView emailTextfield = (TextView) findViewById(R.id.emailTextfield);
        final TextView numberTextfield = (TextView) findViewById(R.id.numberTextfield);

        final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
        Date date = new Date(dateField.getText().toString());
        Timestamp birthday = new Timestamp(date);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(user.getUid()).update("birthday", birthday,"name", nameTextield.getText().toString(),"phonenumber", numberTextfield.getText().toString(), "email", emailTextfield.getText().toString());
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
        fragment.show(getFragmentManager(), "datePicker");
    }

   // public static int getDrawable(Context context, String ImageName) {
   //     return context.getResources().getIdentifier(ImageName, "drawable", context.getPackageName());
   // }

}
