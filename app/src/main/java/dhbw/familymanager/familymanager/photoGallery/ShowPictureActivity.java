package dhbw.familymanager.familymanager.photoGallery;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import dhbw.familymanager.familymanager.R;

public class ShowPictureActivity extends AppCompatActivity {

    private String photo;
    private FirebaseStorage storage;
    private FirebaseFirestore db;
    private String path;
    private String albumName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
        Intent i = getIntent();
        photo = i.getStringExtra("photoObject");
        albumName = i.getStringExtra("albumName");
        setContentView(R.layout.show_photo);
        loadData();
    }

    private void loadData() {
        DocumentReference docRef = db.collection("photos").document(albumName+photo);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                        fillSite(document);

                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });
    }

    private void fillSite(DocumentSnapshot document) {
        ImageView imageView = findViewById(R.id.showPhotoView);
        path = document.get("path").toString();
        StorageReference storageReference = storage.getReference(path);
        Glide.with(this /* context */).load(storageReference).into(imageView);

        TextView textView = findViewById(R.id.pictureNameField);
        textView.setText(document.get("name").toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.show_picture_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.delete_photo:
                deletePhoto();
                return true;
            case android.R.id.home:
                this.finish();
                return true;
        }
        return true;
    }

    private void deletePhoto() {
        DocumentReference docRef = db.collection("folders").document(albumName);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                        ArrayList<String> photos =(ArrayList<String>) document.get("photos");
                        photos.remove(photo);
                        db.collection("folders").document(albumName).update("photos", photos);
                        fishView();
                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });

        db.collection("photos").document(albumName+photo).delete();
        storage.getReference(path).delete();
    }

    private void fishView() {
        FolderScreenActivity.update();
        this.finish();
    }
}
