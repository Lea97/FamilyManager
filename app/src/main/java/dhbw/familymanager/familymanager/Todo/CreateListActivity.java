package dhbw.familymanager.familymanager.Todo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dhbw.familymanager.familymanager.MainActivity;
import dhbw.familymanager.familymanager.R;
import dhbw.familymanager.familymanager.model.Folder;

public class CreateListActivity extends AppCompatActivity implements View.OnClickListener{

    private String family;
    private ArrayList<String> lists;

    private EditText newListName;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createnewlist);

        findViewById(R.id.createNewList).setOnClickListener(this);
        findViewById(R.id.cancleNewList).setOnClickListener(this);




    }

    public void createList(){
        List<String> tasks = new ArrayList<>();


        final TextView listNameTextfield =  findViewById(R.id.newListName);

    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancleNewList:
                this.finish();
                break;
            case R.id.createNewList:
                createList();
                this.finish();
                break;

        }
    }
}
