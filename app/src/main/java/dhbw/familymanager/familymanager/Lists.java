package dhbw.familymanager.familymanager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import dhbw.familymanager.familymanager.model.ListAdapter;
import dhbw.familymanager.familymanager.model.Task;

public class Lists extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lists);

    }

    private void createList(){

        ImageButton button;
        final EditText input;
        ListView task_list_view;
        final List<Task> tasks;
        final ListAdapter adapter;

        button = (ImageButton) findViewById(R.id.add_task_button);
        input = (EditText) findViewById(R.id.input_task);
        task_list_view = (ListView) findViewById(R.id.list_view);
        tasks = new ArrayList<Task>();
        adapter = new ListAdapter(tasks, this);
        task_list_view.setAdapter(adapter);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if(input.getText().length() >0){
                    tasks.add(new Task(input.getText().toString(), false));
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }
    @Override
    public void onClick(View view) {

        createList();
    }
}
