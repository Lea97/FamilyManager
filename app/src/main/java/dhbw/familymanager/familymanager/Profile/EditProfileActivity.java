package dhbw.familymanager.familymanager.Profile;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

import dhbw.familymanager.familymanager.DatePickerFragment;
import dhbw.familymanager.familymanager.R;
import dhbw.familymanager.familymanager.model.User;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;
    private User user;
    private ImageView mImageView;
    private static final int FILE_SELECT_CODE = 0;
    private static final String TAG = null;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private String picturePath;
    private Boolean loadFile = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        Intent i = getIntent();
        user = (User) i.getSerializableExtra("userObject");

        setContentView(R.layout.edit_profil);
        fillFormular();
        findViewById(R.id.saveButton).setOnClickListener(this);
        findViewById(R.id.cancelButton).setOnClickListener(this);
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

    private void fillFormular() {
        final TextView nameTextfield = (TextView) findViewById(R.id.nameTextfield);
        nameTextfield.setText(user.getName());

        final TextView birthTextView = (TextView) findViewById(R.id.showDate);
        if (user.getBirthday() != null)
        {
            final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
            ((TextView) findViewById(R.id.showDate)).setText(dateFormat.format(user.getBirthday()));

            Button btnChoose = (Button) findViewById(R.id.btnChoose);
            mImageView = (ImageView) findViewById(R.id.imageView);
            btnChoose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showFileChooser();
                }
            });
        }
        final TextView numberTextfield = (TextView) findViewById(R.id.numberTextfield);
        numberTextfield.setText(user.getPhonenumber());

        String picturePath = user.getPicturePath();
        showPicture(picturePath);
    }

    private void showPicture(String picturePath) {
        StorageReference storageReference = storage.getReference(picturePath);
        ImageView imageView = findViewById(R.id.imageView);
        Glide.with(this /* context */).load(storageReference).into(imageView);
    }

    private void uploadImage() {

        if(filePath != null)
        {
            loadFile = true;
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
                            Toast.makeText(EditProfileActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            finishActivity();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(EditProfileActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void finishActivity() {
        ProfileActivity.refreshValues();
        this.finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancelButton:
                this.finish();
                break;
            case R.id.saveButton:
                saveProfileChanges();
                if (!loadFile)
                {
                    finishActivity();
                }
                break;
        }
    }

        private void saveProfileChanges() {
            uploadImage();
            final TextView dateTextView = findViewById(R.id.showDate);
            final EditText nameTextView = findViewById(R.id.nameTextfield);
            final EditText numberTextView = findViewById(R.id.numberTextfield);

            final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
            Date date = new Date(dateTextView.getText().toString());
            Timestamp birthday = new Timestamp(date);

            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = auth.getCurrentUser();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            if(currentUser != null) {
                if (filePath != null) {
                    db.collection("users").document(currentUser.getUid()).update("picturePath", picturePath, "birthday", birthday, "name", nameTextView.getText().toString(), "phonenumber", numberTextView.getText().toString());
                } else {
                    db.collection("users").document(currentUser.getUid()).update("birthday", birthday, "name", nameTextView.getText().toString(), "phonenumber", numberTextView.getText().toString());
                }
            }
            else {
                Toast.makeText(EditProfileActivity.this, "Die Nutzerdaten konnten aufgrund eines Fehlers nicht geändert werden.", Toast.LENGTH_LONG).show();
            }
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //TODO Passwort ändern geht noch nicht und eingegeben E-Mail muss validiert werden
        //getMenuInflater().inflate(R.menu.edit_profil_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.edit_credentials:
                Intent intent = new Intent(EditProfileActivity.this, EditCredentialsActivity.class);
                startActivity(intent);
                break;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return true;
    }
}
