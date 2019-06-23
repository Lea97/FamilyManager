package dhbw.familymanager.familymanager.List;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import dhbw.familymanager.familymanager.R;
import dhbw.familymanager.familymanager.model.Task;

public class TaskScreenActivity extends AppCompatActivity {
    Button button;
    EditText input;
    ListView task_list_view;
    List<Task> tasks;
    ListTaskAdapter adapter;
    private String family;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_view);

        button = (Button) findViewById(R.id.add_task_button);
        input = (EditText) findViewById(R.id.input_task);
        task_list_view = (ListView) findViewById(R.id.task_view);
        tasks = new ArrayList<Task>();
        adapter = new ListTaskAdapter(tasks, this);
        task_list_view.setAdapter(adapter);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (input.getText().length() > 0) {
                    tasks.add(new Task(input.getText().toString(), false));
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void addTasksToDB() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.task_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_done_tasks:
                deleteDoneTasks();
                this.adapter.notifyDataSetChanged();
                break;
            case R.id.action_delete_all:
                this.tasks.clear();
                this.adapter.notifyDataSetChanged();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void deleteDoneTasks() {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).isDone()) {
                tasks.remove(i);
            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu_list, menu);
    }

    /*@Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = (int) info.id;

        switch (item.getItemId()) {

            case R.id.context_delete:
                this.tasks.remove(position);
                this.adapter.notifyDataSetChanged();
                break;
            case R.id.context_edit:

                createEditDialog(tasks.get(position));
                break;
        }
        return super.onContextItemSelected(item);
    }

    public void createEditDialog(final Task task) {

        LayoutInflater li = LayoutInflater.from(TaskScreenActivity.this);
        View dialogView = li.inflate(R.layout.edit_dialog, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TaskScreenActivity.this);

        alertDialogBuilder.setView(dialogView);

        final EditText inputText = (EditText) dialogView.findViewById(R.id.edit_dialog_input);
        inputText.setText(task.getTaskContent());
        final TextView dialogMessage = (TextView) dialogView.findViewById(R.id.edit_dialog_message);

        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton("Save",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                task.setTaskContent(inputText.getText().toString());
                                adapter.notifyDataSetChanged();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        final AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }*/
}
