package dhbw.familymanager.familymanager.photoGallery;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import dhbw.familymanager.familymanager.MainActivity;
import dhbw.familymanager.familymanager.R;

public class CreateFolderActivity extends AppCompatActivity implements View.OnClickListener {

    private String family;
    private ArrayList<String> folders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        family = MainActivity.getFamily();
        Intent i = getIntent();
        String[] folder = i.getStringArrayExtra("folders");
        folders = new ArrayList<String>(Arrays.asList(folder));
        setContentView(R.layout.create_photo_folder);
        findViewById(R.id.createNewFolder).setOnClickListener(this);
        findViewById(R.id.cancleNewFolder).setOnClickListener(this);
    }

    private void createFolder() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        TextView nameField = (TextView) findViewById(R.id.newFolderName);
        folders.add(nameField.getText().toString());
        Map<String, Object> gallery = new HashMap<>();
        gallery.put("folderName", folders);
        db.collection("gallery").document(family).set(gallery);
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
