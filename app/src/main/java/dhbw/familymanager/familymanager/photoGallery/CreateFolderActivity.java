package dhbw.familymanager.familymanager.photoGallery;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        family = MainActivity.getFamily();
        Intent i = getIntent();
        String[] folder = i.getStringArrayExtra("folders");
        folders = new ArrayList<>(Arrays.asList(folder));
        setContentView(R.layout.create_photo_folder);
        findViewById(R.id.createNewFolder).setOnClickListener(this);
        findViewById(R.id.cancleNewFolder).setOnClickListener(this);
    }

    private void createFolder() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        TextView nameField = (TextView) findViewById(R.id.newFolderName);
        String newFolderName = nameField.getText().toString();
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
                createFolder();
                PhotoGalleryActivity.updateFolders();
                finish();
                break;
            case R.id.cancleNewFolder:
                finish();
                break;
        }
    }
}
