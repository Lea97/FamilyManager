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
import android.widget.ListView;
import android.widget.Toast;

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

public class PhotoGalleryActivity extends AppCompatActivity {

    private static Boolean update = false;
    private String family;
    private List<String> folders;

    public static void updateFolders() {
        update = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        family = MainActivity.getFamily();
        folders = new ArrayList<String>();
        if (family != null) {
            setContentView(R.layout.photo_gallery);
            addFolders();
        } else {
            setContentView(R.layout.empty_page);
            Toast.makeText(PhotoGalleryActivity.this, "Wählen Sie eine Familie um die Funktionalitäten zu nutzen.", Toast.LENGTH_LONG).show();
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

    private void addListAdapter() {
        ListView listView = (ListView) findViewById(R.id.galleryListView);
        String[] fileArray = new String[folders.size()];
        FolderAdapter folderAdapter = new FolderAdapter(getApplicationContext(), folders.toArray(fileArray), this);
        listView.setAdapter(folderAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(PhotoGalleryActivity.this, FolderScreenActivity.class);
                intent.putExtra("albumName", folders.get(position));
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (update) {
            update = false;
            addFolders();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (family != null) {
            getMenuInflater().inflate(R.menu.folderoverview_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_folder:
                Intent createFolder = new Intent(PhotoGalleryActivity.this, CreateFolderActivity.class);
                String[] fileArray = new String[folders.size()];
                createFolder.putExtra("folders", folders.toArray(fileArray));
                startActivity(createFolder);
                break;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return true;
    }
}
