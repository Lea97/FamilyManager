package dhbw.familymanager.familymanager.List;

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

public class ListActivity extends AppCompatActivity {

    private static Boolean update = false;
    private String family;
    private List<String> lists;

    public static void updateFolders() {
        update = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        family = MainActivity.getFamily();
        lists = new ArrayList<String>();
        if (family != null) {
            setContentView(R.layout.list_main);
            addLists();
        } else {
            setContentView(R.layout.empty_page);
            Toast.makeText(ListActivity.this, "Wählen Sie eine Familie um die Funktionalitäten zu nutzen.", Toast.LENGTH_LONG).show();
        }
    }

    private void addLists() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("todolist").document(family);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                        lists = (ArrayList<String>) document.get("listName");
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
        ListView listView = (ListView) findViewById(R.id.list_view);
        String[] fileArray = new String[lists.size()];
        ListAdapter listAdapter = new ListAdapter(getApplicationContext(), lists.toArray(fileArray), this);
        listView.setAdapter(listAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ListActivity.this, ShowTaskActivity.class); // mit ShowTaskActivity ersetzen
                intent.putExtra("todoName", lists.get(position));
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (update) {
            update = false;
            addLists();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (family != null) {
            getMenuInflater().inflate(R.menu.list_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_list:
                Intent createList = new Intent(ListActivity.this, CreateNewListActivity.class);
                String[] fileArray = new String[lists.size()];
                createList.putExtra("lists", lists.toArray(fileArray));
                startActivity(createList);
                break;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return true;
    }
}
