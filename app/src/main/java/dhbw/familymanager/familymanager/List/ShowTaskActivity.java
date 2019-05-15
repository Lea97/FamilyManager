package dhbw.familymanager.familymanager.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import dhbw.familymanager.familymanager.MainActivity;
import dhbw.familymanager.familymanager.R;
import dhbw.familymanager.familymanager.model.User;

public class ShowTaskActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private String family;
    private String listName;
    private ArrayList<String> tasks;
    private static boolean update = false;
    private String task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        family = MainActivity.getFamily();
        Intent i = getIntent();
        listName = i.getStringExtra("todoName");
        tasks = new ArrayList<String>();
        setContentView(R.layout.task_main);
        addTasks();
    }

    public static void update(){
        update = true;
    }

    @Override
    protected void onPostResume(){
        super.onPostResume();
        if (update)
        {
            update = false;
            tasks.clear();
            addTasks();
        }
    }

    private void addTasks() {
        DocumentReference docRef = db.collection("lists").document(family+listName);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                        tasks = (ArrayList<String>) document.get("tasks");
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

    private void addListAdapter(){

        ListView listView = (ListView) findViewById(R.id.task_view_main);
        String[] fileArray = new String[tasks.size()];
        TaskAdapter taskAdapter = new TaskAdapter(getApplicationContext(), tasks.toArray(fileArray), this);
        listView.setAdapter(taskAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.task_menu2, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new_task:
                Intent intent = new Intent(ShowTaskActivity.this, AddTaskActivity.class);
                String[] fileArray = new String[tasks.size()];
                intent.putExtra("tasks", tasks.toArray(fileArray));
                intent.putExtra("todoName", listName);
                startActivity(intent);
                break;
            case R.id.action_delete_done_tasks:
                deleteDoneTasks();
                break;
            case R.id.action_delete_all:
                this.tasks.clear();

                break;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return true;
    }

    public void deleteDoneTasks(){
        DocumentReference docRef = db.collection("lists").document(listName);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                        ArrayList<String> tasks =(ArrayList<String>) document.get("tasks");
                        tasks.remove(task);
                        db.collection("lists").document(listName).update("tasks", tasks);
                        fishView();
                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });
    }

    private void fishView() {
        ShowTaskActivity.update();
        this.finish();
    }

}
