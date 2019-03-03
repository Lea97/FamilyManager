package dhbw.familymanager.familymanager;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.DocumentType;

import java.util.ArrayList;

import dhbw.familymanager.familymanager.model.ChatMessage;

class ChatActivity extends AppCompatActivity {
    private FirebaseListAdapter<ChatMessage> adapter;
    private FloatingActionButton createRoom;
    private ListView layout;
    private FirebaseFirestore db;
    private FirebaseAuth user;
    private ArrayList<String> chatrooms;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatrooms);
        user=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
        if(user!=null){
            chatrooms=getUserChatrooms();
        }


       layout=findViewById(R.id.chatroomListView);

        String [] values=new String[]{"Chatroom1", "Chatroom2", "Chatroom3", "Chatroom4", "Chatroom5"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, values);
        layout.setAdapter(adapter);

layout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
});



        //displayChatMessages();
       // createRoom = findViewById(R.id.create_room);

        //createRoom.setOnClickListener(new View.OnClickListener() {
            //@Override
            //public void onClick(View view) {
             //   Log.d("MainActivity", "Launch create a room screen");
             //   Intent intent=new Intent(ChatActivity.this, CreateChatroom.class);

              //  EditText input = (EditText)findViewById(R.id.input);
              //  startActivity(intent);

                // Read the input field and push a new instance
                // of ChatMessage to the Firebase database
               // FirebaseDatabase.getInstance()
               //         .getReference()
                //        .push()
                //        .setValue(new ChatMessage(input.getText().toString(),
                 //               FirebaseAuth.getInstance()
                 //                       .getCurrentUser()
                  //                      .getDisplayName())
                  //      );

                // Clear the input
                //input.setText("");
           // }
       // });

    }

    private ArrayList<String> getUserChatrooms() {
        //TODO read from db
        return null;
    }


    private void displayChatMessages() {
        ListView listOfMessages = (ListView)findViewById(R.id.message);

        adapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class,
                R.layout.message, FirebaseDatabase.getInstance().getReference()) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                // Get references to the views of message.xml
                TextView messageText = (TextView)v.findViewById(R.id.message_text);
                TextView messageUser = (TextView)v.findViewById(R.id.message_user);
                TextView messageTime = (TextView)v.findViewById(R.id.message_time);

                // Set their text
                messageText.setText(model.getMessageText());
                messageUser.setText(model.getSenderId());

                // Format the date before showing it
                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                        model.getTimestamp()));
            }
        };

        listOfMessages.setAdapter(adapter);
    }


}
