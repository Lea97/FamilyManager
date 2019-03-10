package dhbw.familymanager.familymanager.Todo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
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
import dhbw.familymanager.familymanager.model.Todo;

public class ListsActivity extends AppCompatActivity implements View.OnClickListener{

    private String family;
    private List<Todo> lists;
    private static Boolean update = false;
    private ListView lists_view;


    private ArrayList<String> items;
    private ArrayList<String> listIds;
    private static String currentList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.list_main);
        lists = new ArrayList<Todo>();
        setLists();
        findViewById(R.id.addListButton).setOnClickListener(this);

    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addFamilyButton:
                startActivity(new Intent(ListsActivity.this, CreateListActivity.class));
                break;
        }
    }

    public void setLists(){
        final Spinner dropdown = findViewById(R.id.listSpinner);
        items = new ArrayList<String>();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String list = dropdown.getSelectedItem().toString();
                int pos = dropdown.getSelectedItemPosition();
                if(listIds.size() != 0)
                {
                    setList(listIds.get(pos));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    private void setList(String list)
    {
        currentList = list;
    }

    public static String getList(){
        return currentList;
    }

}
