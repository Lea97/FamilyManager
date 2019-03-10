package dhbw.familymanager.familymanager.Todo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

import dhbw.familymanager.familymanager.MainActivity;
import dhbw.familymanager.familymanager.R;

public class ListsScreen extends AppCompatActivity {

    private String family;
    private String listName;
    private ArrayList<String> tasks;
    private static boolean refresh = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        family = MainActivity.getFamily();
        Intent i = getIntent();
        listName = i.getStringExtra("listName");
        setContentView(R.layout.list_layout);
        getTasks();
    }

    public static void update(){
        refresh = true;
    }

    public void getTasks(){

    }
}
