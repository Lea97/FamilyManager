package dhbw.familymanager.familymanager.List;

import android.content.Intent;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import dhbw.familymanager.familymanager.MainActivity;
import dhbw.familymanager.familymanager.R;
import dhbw.familymanager.familymanager.model.Task;

public class AddTaskActivity extends AppCompatActivity implements View.OnClickListener{
    private String family;
    private FirebaseFirestore db;
    private ArrayList<String> tasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        String[] list = i.getStringArrayExtra("tasks");
        tasks = new ArrayList<>(Arrays.asList(list));
        setContentView(R.layout.add_task);
        family = MainActivity.getFamily();
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
                addTask();
                ShowTaskActivity.update();
                this.finish();
                break;
        }
    }

    private void addTask() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        EditText editText = findViewById(R.id.newTaskName);
        String newTaskName = editText.getText().toString();

        tasks.add(newTaskName);
        //db.collection("lists").document(family+listName).update("tasks", tasks);

        Map<String, Object> listen = new HashMap<>();
        listen.put("taskName", tasks);
        db.collection("lists").document(family).set(listen);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        Task task = new Task(new String(), Boolean.valueOf(false));
        db.collection("tasks").document(family + newTaskName).set(task);
    }
}
