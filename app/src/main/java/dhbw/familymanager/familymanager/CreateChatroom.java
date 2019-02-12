package dhbw.familymanager.familymanager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

class CreateChatroom extends AppCompatActivity {
    private EditText roomName;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);
        roomName = findViewById(R.id.room_name);
        setTitle(getString(R.string.create_room));

    }
    }
