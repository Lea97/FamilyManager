package dhbw.familymanager.familymanager.photoGallery;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import dhbw.familymanager.familymanager.MainActivity;
import dhbw.familymanager.familymanager.R;
import dhbw.familymanager.familymanager.adapter.GalleryViewAdapter;

public class FolderScreenActivity extends AppCompatActivity {

    private String family;
    private String folderName;
    private ArrayList<String> photos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        family = MainActivity.getFamily();
        Intent i = getIntent();
        folderName = i.getStringExtra("albumName");
        setContentView(R.layout.image_gallery);
        getPhotos();
    }

    private void getPhotos() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("folders").document(family + folderName);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                        photos = (ArrayList<String>) document.get("photos");
                        setAdapter();
                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });
    }

    private void setAdapter() {
        GridView gridView = (GridView)findViewById(R.id.gridview);
        ArrayList<String> photosPath = new ArrayList<>();
        for(String photo: photos)
        {
            photosPath.add("albumPhotos/"+family + "/" + folderName+ "/" +photo);
        }
        String[] photoArray = new String[photosPath.size()];
        GalleryViewAdapter adapter = new GalleryViewAdapter(getApplicationContext(), photosPath.toArray(photoArray), this);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String photo = photos.get(position);
                Intent intent = new Intent(FolderScreenActivity.this, ShowPictureActivity.class);
                intent.putExtra("photoObject", family+folderName+photo);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.photoalbum_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.add_photo:
                Intent intent = new Intent(FolderScreenActivity.this, AddPictureActivity.class);
                intent.putExtra("albumName", folderName);
                startActivity(intent);
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return true;
    }
}
