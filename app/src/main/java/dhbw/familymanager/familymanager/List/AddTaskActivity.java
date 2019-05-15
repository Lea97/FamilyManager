package dhbw.familymanager.familymanager.List;

import android.content.Intent;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import dhbw.familymanager.familymanager.MainActivity;
import dhbw.familymanager.familymanager.R;


public class AddTaskActivity extends AppCompatActivity implements View.OnClickListener{
    private String family;
    private FirebaseFirestore db;
    private ArrayList<String> tasks;
    private String listName;
    private String taskName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        family = MainActivity.getFamily();
        Intent i = getIntent();
        listName = i.getStringExtra("todoName");
        tasks = new ArrayList<String>();
        setContentView(R.layout.add_task);
        findViewById(R.id.add_task).setOnClickListener(this);
        findViewById(R.id.cancle_add_task).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancle_add_task:
                this.finish();
                break;
            case R.id.add_task:
                setTasks();
                addTask();
                ShowTaskActivity.update();
                this.finish();
                break;
        }
    }

    private void setTasks(){
        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("lists").document(family + listName);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                        tasks = (ArrayList<String>) document.get("tasks");
                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });
    }

    private void addTask(){
        setTaskName();
        addTaskToListsDB();
        addTaskToTasksDB();
    }

    private void setTaskName(){
        EditText nameText = findViewById(R.id.newTaskName);
        taskName = nameText.getText().toString();
    }

    private void addTaskToListsDB(){
        tasks.add(taskName);
        //Map<String, Object> listen = new HashMap<>();
        //listen.put("tasks", tasks);
        db.collection("lists").document(family+listName).update("tasks",tasks);
    }

    private void addTaskToTasksDB(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        //dhbw.familymanager.familymanager.model.Task task = new dhbw.familymanager.familymanager.model.Task(new String(), Boolean.valueOf(false));
        Aufgabe aufgabe = new Aufgabe(auth.getCurrentUser().getUid(), new Date(), taskName);
        db.collection("tasks").document(family + listName + taskName).set(aufgabe);
    }

}
