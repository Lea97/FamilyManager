package dhbw.familymanager.familymanager;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;


public class ProfileActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;

    private FirebaseUser user;
    private Boolean editmode;
    private ImageView mImageView;
    private static final int FILE_SELECT_CODE = 0;
    private static final String TAG = null;
    FirebaseStorage storage;
    StorageReference storageReference;
    String picturePath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        setValues();
        setContentView(R.layout.profil);
        editmode = false;
        findViewById(R.id.changeProfilButton).setOnClickListener(this);
    }

    private void showFileChooser() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a Picture to Upload"),
                    PICK_IMAGE_REQUEST);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                mImageView.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            picturePath = "ProfilPictures/"+ UUID.randomUUID().toString();
            StorageReference ref = storageReference.child(picturePath);
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(ProfileActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            setValues();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(ProfileActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }

    private void showPicture(String picturePath) {
        StorageReference storageReference = storage.getReference(picturePath);
        ImageView imageView = findViewById(R.id.imageView);
        Glide.with(this /* context */).load(storageReference).into(imageView);
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

        Timestamp userBirthday = (Timestamp) document.get("birthday");
        final TextView birthTextView = (TextView) findViewById(R.id.showDate);
        if (userBirthday != null)
        {
            final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
            if (!editmode)
            {
                ((TextView) findViewById(R.id.birthTextfield)).setText(dateFormat.format(userBirthday.toDate()));
            }
            else
            {
                ((TextView) findViewById(R.id.showDate)).setText(dateFormat.format(userBirthday.toDate()));

                Button btnChoose = (Button) findViewById(R.id.btnChoose);
                mImageView = (ImageView) findViewById(R.id.imageView);
                btnChoose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showFileChooser();
                    }
                });
            }
        }
        String userNumber = (String) document.get("phonenumber");
        final TextView numberTextfield = (TextView) findViewById(R.id.numberTextfield);
        numberTextfield.setText(userNumber);

        String picturePath = (String) document.get("picturePath");
        showPicture(picturePath);
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
                saveProfileChanges();
                setContentView(R.layout.profil);
                editmode = false;
                findViewById(R.id.changeProfilButton).setOnClickListener(this);
                setValues();
                break;
        }
    }

    private void saveProfileChanges() {
        uploadImage();
        final TextView dateTextView = (TextView) findViewById(R.id.showDate);
        final TextView nameTextView = (TextView) findViewById(R.id.nameTextfield);
        final TextView emailTextView = (TextView) findViewById(R.id.emailTextfield);
        final TextView numberTextView = (TextView) findViewById(R.id.numberTextfield);

        final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
        Date date = new Date(dateTextView.getText().toString());
        Timestamp birthday = new Timestamp(date);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if(filePath != null)
        {
            db.collection("users").document(user.getUid()).update("picturePath",picturePath,"birthday", birthday,"name", nameTextView.getText().toString(),"phonenumber", numberTextView.getText().toString(), "email", emailTextView.getText().toString());
        }
        else{
            db.collection("users").document(user.getUid()).update("birthday", birthday,"name", nameTextView.getText().toString(),"phonenumber", numberTextView.getText().toString(), "email", emailTextView.getText().toString());
        }
       // user.updateEmail(emailTextfield.getText().toString());
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar cal = new GregorianCalendar(year, month, day);
        final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
        ((TextView) findViewById(R.id.showDate)).setText(dateFormat.format(cal.getTime()));
    }

    public void datePicker(View view){
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setMaxDay();
        fragment.show(getFragmentManager(), "datePicker");
    }

}
