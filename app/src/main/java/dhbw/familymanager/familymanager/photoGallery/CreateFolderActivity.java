package dhbw.familymanager.familymanager.photoGallery;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import dhbw.familymanager.familymanager.MainActivity;
import dhbw.familymanager.familymanager.R;
import dhbw.familymanager.familymanager.model.Folder;

public class CreateFolderActivity extends AppCompatActivity implements View.OnClickListener {

    private String family;
    private ArrayList<String> folders;
    private FirebaseFirestore db;
    private String newFolderName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        family = MainActivity.getFamily();
        Intent i = getIntent();
        String[] folder = i.getStringArrayExtra("folders");
        folders = new ArrayList<>(Arrays.asList(folder));
        setContentView(R.layout.create_photo_folder);
        findViewById(R.id.createNewFolder).setOnClickListener(this);
        findViewById(R.id.cancleNewFolder).setOnClickListener(this);
    }

    private void createFolder() {

        folders.add(newFolderName);
        Map<String, Object> gallery = new HashMap<>();
        gallery.put("folderName", folders);
        db.collection("gallery").document(family).set(gallery);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        Folder folder = new Folder(auth.getUid(), new Date(), new ArrayList<String>());
        db.collection("folders").document(family + newFolderName).set(folder);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.createNewFolder:
                TextView nameField = (TextView) findViewById(R.id.newFolderName);
                newFolderName = nameField.getText().toString();
                validateIfFolderExist();
                break;
            case R.id.cancleNewFolder:
                finish();
                break;
        }
    }

    private void validateIfFolderExist() {
        DocumentReference docRef = db.collection("gallery").document(family);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                        ArrayList<String> foldersInDB = (ArrayList<String>) document.get("folderName");
                        if (foldersInDB.contains(newFolderName)) {
                            TextView nameField = findViewById(R.id.newFolderName);
                            nameField.setError("Ein Ordner mit diesem Namen gibt es bereits.");
                        } else {
                            addNewFolder();
                        }
                    } else {
                        Log.d("TAG", "No such document");
                        addNewFolder();
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });
    }

    private void addNewFolder() {
        createFolder();
        PhotoGalleryActivity.updateFolders();
        finish();
    }
}
