package dhbw.familymanager.familymanager.photoGallery;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import dhbw.familymanager.familymanager.FileChooser;
import dhbw.familymanager.familymanager.MainActivity;
import dhbw.familymanager.familymanager.R;
import dhbw.familymanager.familymanager.model.Photo;

public class AddPictureActivity extends AppCompatActivity implements View.OnClickListener {

    private final int PICK_IMAGE_REQUEST = 71;
    private String family;
    private String folderName;
    private FileChooser fileChooser;
    private String photoName;
    private FirebaseFirestore db;
    private String photoPath;
    private Uri filePath;
    private ArrayList<String> photos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        family = MainActivity.getFamily();
        Intent i = getIntent();
        folderName = i.getStringExtra("albumName");
        photos = new ArrayList<String>();
        setContentView(R.layout.add_photo);
        findViewById(R.id.addPhoto).setOnClickListener(this);
        findViewById(R.id.cancleAddPhoto).setOnClickListener(this);
        findViewById(R.id.choosePicture).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addPhoto:
                setFamilyPhotos();
                if (validate()) {
                    addPhoto();
                    this.finish();
                }
                break;
            case R.id.cancleAddPhoto:
                this.finish();
                break;
            case R.id.choosePicture:
                startFileChooser();
                break;
        }
    }

    private void startFileChooser() {
        fileChooser = new FileChooser(getApplicationContext(), this);
    }

    private Boolean validate() {
        EditText editText = findViewById(R.id.newPhotoName);
        String photoName = editText.getText().toString();
        if (filePath == null) {
            Toast.makeText(AddPictureActivity.this, "Es wurde kein Bild zum Hochladen ausgewählt.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (photoName.isEmpty()) {
            editText.setError("Name des Bildes muss gesetzt werden.");
            return false;
        }
        if (photos.contains(photoName)) {
            editText.setError("Ein Foto mit diesem Namen existiert bereits im Album");
            return false;
        }
        return true;
    }

    private void addPhoto() {
        setPhotoName();
        addPictureToFolderDB();
        addPictureToStorage();
        addPictureToPhotosDB();
    }

    private void setPhotoName() {
        EditText nameText = findViewById(R.id.newPhotoName);
        photoName = nameText.getText().toString();
    }

    private void addPictureToPhotosDB() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        Photo photo = new Photo(photoPath, auth.getCurrentUser().getUid(), new Date(), photoName);
        db.collection("photos").document(family + folderName + photoName).set(photo);
    }

    private void addPictureToStorage() {
        photoPath = "albumPhotos/" + family + "/" + folderName + "/" + photoName;
        fileChooser.uploadImage(photoPath, filePath);
    }

    private void setFamilyPhotos() {
        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("folders").document(family + folderName);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                        photos = (ArrayList<String>) document.get("photos");
                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });
    }

    private void addPictureToFolderDB() {
        photos.add(photoName);
        db.collection("folders").document(family + folderName).update("photos", photos);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                ImageView imageView = findViewById(R.id.choosedPicture);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
