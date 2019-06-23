package dhbw.familymanager.familymanager.List;

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
import dhbw.familymanager.familymanager.model.Todo;

public class CreateNewListActivity extends AppCompatActivity implements View.OnClickListener {
    private String family;
    private ArrayList<String> lists;
    private FirebaseFirestore db;
    private String newListName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        family = MainActivity.getFamily();
        Intent i = getIntent();
        String[] list = i.getStringArrayExtra("lists");
        lists = new ArrayList<>(Arrays.asList(list));
        setContentView(R.layout.createnewlist);
        findViewById(R.id.createNewList).setOnClickListener(this);
        findViewById(R.id.cancleNewList).setOnClickListener(this);
    }

    private void createList() {

        lists.add(newListName);
        Map<String, Object> listen = new HashMap<>();
        listen.put("listName", lists);
        db.collection("todolist").document(family).set(listen);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        Todo todo = new Todo(auth.getUid(), new Date(), new ArrayList<String>());
        db.collection("lists").document(family + newListName).set(todo);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.createNewList:
                TextView nameField = (TextView) findViewById(R.id.newListName);
                newListName = nameField.getText().toString();
                createList();
                ListActivity.updateFolders();
                finish();
                break;
            case R.id.cancleNewList:
                finish();
                break;
        }
    }

    /*private void validateIfTodoExist() {
        DocumentReference docRef = db.collection("todolist").document(family);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                        ArrayList<String> listsInDB = (ArrayList<String>) document.get("listName");
                        if (listsInDB.contains(newListName))
                        {
                            TextView nameField = findViewById(R.id.newListName);
                            nameField.setError("Eine Liste mit diesem Namen gibt es bereits.");
                        }
                        else {
                            createList();
                            ListActivity.updateFolders();
                            finish();
                        }

                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });
    }*/
}
