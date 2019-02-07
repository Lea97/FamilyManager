package dhbw.familymanager.familymanager.photoGallery;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import dhbw.familymanager.familymanager.MainActivity;
import dhbw.familymanager.familymanager.R;
import dhbw.familymanager.familymanager.adapter.FolderAdapter;

public class PhotoGalleryActivity extends AppCompatActivity implements View.OnClickListener {

    private String family;
    private List<String> folders;
    private static Boolean update = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        family = MainActivity.getFamily();
        folders = new ArrayList<String>();
        setContentView(R.layout.photo_gallery);
        if (family == null){
            findViewById(R.id.addFolder).setVisibility(View.INVISIBLE);
        }
        else {
            findViewById(R.id.addFolder).setOnClickListener(this);
            addFolders();
        }
    }

    private void addFolders() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("gallery").document(family);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                        folders = (ArrayList<String>) document.get("folderName");
                        addListAdapter();
                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });
    }

    public static void updateFolders()
    {
        update = true;
    }

    private void addListAdapter() {
        ListView listView = (ListView)findViewById(R.id.galleryListView);
        String[] fileArray = new String[folders.size()];
        FolderAdapter folderAdapter = new FolderAdapter(getApplicationContext(), folders.toArray(fileArray), this);
        listView.setAdapter(folderAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    @Override
    protected void onPostResume(){

        super.onPostResume();
        if(update)
        {
            update = false;
            addFolders();
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addFolder:
                Intent createFolder = new Intent(PhotoGalleryActivity.this, CreateFolderActivity.class);
                String[] fileArray = new String[folders.size()];
                createFolder.putExtra("folders", folders.toArray(fileArray));
                startActivity(createFolder);
                break;
        }
    }
}
