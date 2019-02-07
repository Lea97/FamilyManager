package dhbw.familymanager.familymanager.photoGallery;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import dhbw.familymanager.familymanager.FileChooser;
import dhbw.familymanager.familymanager.MainActivity;
import dhbw.familymanager.familymanager.R;
import dhbw.familymanager.familymanager.adapter.GalleryViewAdapter;

public class FolderScreenActivity extends AppCompatActivity implements View.OnClickListener {

    private String family;
    private String folderName;
    private ArrayList<String> photos;
    private PopupWindow popupWindow;

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
                LayoutInflater layoutInflater = (LayoutInflater) FolderScreenActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View customView = layoutInflater.inflate(R.layout.add_photo,null);
                customView.findViewById(R.id.cancleAddPhoto).setOnClickListener(this);
                popupWindow = new PopupWindow(customView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                RelativeLayout relativLayout1 = findViewById(R.id.relativLayout1);
                popupWindow.showAtLocation(relativLayout1, Gravity.CENTER, 0, 0);
                break;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return true;
    }

    private void addPhoto() {
        FileChooser fileChooser = new FileChooser("albumPhotos/"+family + "/" + folderName+ "/",this); //muss Filename noch dazu
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.cancleAddPhoto:
            popupWindow.dismiss();
            break;
        }
    }
}
