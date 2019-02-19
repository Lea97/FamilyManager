package dhbw.familymanager.familymanager.photoGallery;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

import dhbw.familymanager.familymanager.R;

public class ShowPictureActivity extends AppCompatActivity {

    private String photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        photo = i.getStringExtra("photoObject");
        setContentView(R.layout.show_photo);
        loadData();
    }

    private void loadData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("photos").document(photo);
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
        FirebaseStorage storage = FirebaseStorage.getInstance();
        ImageView imageView = findViewById(R.id.showPhotoView);
        StorageReference storageReference = storage.getReference(document.get("path").toString());
        Glide.with(this /* context */).load(storageReference).into(imageView);

        TextView textView = findViewById(R.id.pictureNameField);
        textView.setText(document.get("name").toString());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                return true;
        }
        return true;
    }
}
