package dhbw.familymanager.familymanager.Profile;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
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
    private Calendar cal;
    private final int REQUEST_WRITE_STORAGE = 1;
    private final int REQUEST_CAMERA = 2;

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
        cal = new GregorianCalendar(year, month, day);
        final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
        ((TextView) findViewById(R.id.showDate)).setText(dateFormat.format(cal.getTime()));
    }

    public void datePicker(View view){
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setMaxDay();
        fragment.show(getFragmentManager(), "datePicker");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    createFolder();
                } else
                {
                    Toast.makeText(this, "Die App hat keine Erlaubnis auf deine Dateien zuzugreifen. Willst du dieses Recht erlauben? ", Toast.LENGTH_LONG).show();
                }
            }
            case REQUEST_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(this, "Die App hat keine Erlaubnis auf deine Kamera zuzugreifen. Willst du dieses Recht erlauben? ", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void createFolder() {
        final File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "FamilyManager");
        if (!imageStorageDir.exists()) {
            Toast.makeText(this, "Neuer Ordner wird erstellt...", Toast.LENGTH_SHORT).show();
            boolean rv = imageStorageDir.mkdir();
            Toast.makeText(this, "Ordner" + ( rv ? "wurde erstellt" : "konnte nicht erstellt werden"), Toast.LENGTH_SHORT).show();
        }
    }

    private void showFileChooser() {
        final File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "FamilyManager");
        try {

            boolean hasPermission = (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);

            boolean hasCameraPermission = (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);

            if (!hasPermission) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_WRITE_STORAGE);
                return;
            }
            if (!hasCameraPermission) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        REQUEST_CAMERA);
                return;
            }
            createFolder();
            File file = new File(
                     imageStorageDir + File.separator + "IMG_"
                            + String.valueOf(System.currentTimeMillis())
                            + ".jpg");

            filePath = Uri.fromFile(file);
            final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, filePath);
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*");

            Intent chooserIntent = Intent.createChooser(i, "Wähle ein Bild zu hochladen aus");

            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[] { captureIntent });
            startActivityForResult(chooserIntent, PICK_IMAGE_REQUEST);
        }
        catch(Exception e){
                Toast.makeText(getBaseContext(), "Exception:"+e,
                        Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
        }
        if(filePath != null)
        {
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
            final EditText nameTextView = findViewById(R.id.nameTextfield);
            final EditText numberTextView = findViewById(R.id.numberTextfield);


            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = auth.getCurrentUser();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            if(currentUser != null) {
                if (filePath != null) {
                    db.collection("users").document(currentUser.getUid()).update("picturePath", picturePath, "name", nameTextView.getText().toString(), "phonenumber", numberTextView.getText().toString());
                } else {
                    db.collection("users").document(currentUser.getUid()).update("name", nameTextView.getText().toString(), "phonenumber", numberTextView.getText().toString());
                }

                if (cal != null)
                {
                    Timestamp birthday = new Timestamp(cal.getTime());
                    db.collection("users").document(currentUser.getUid()).update("birthday", birthday);
                }
            }
            else {
                Toast.makeText(EditProfileActivity.this, "Die Nutzerdaten konnten aufgrund eines Fehlers nicht geändert werden.", Toast.LENGTH_LONG).show();
            }
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //TODO E-Mail muss validiert werden
        getMenuInflater().inflate(R.menu.edit_profil_menu, menu);
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
