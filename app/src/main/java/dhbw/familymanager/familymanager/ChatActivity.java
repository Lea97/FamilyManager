package dhbw.familymanager.familymanager;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import dhbw.familymanager.familymanager.model.ChatMessage;

class ChatActivity extends AppCompatActivity {
    private FirebaseListAdapter<ChatMessage> adapter;
    private FloatingActionButton createRoom;
    private ListView layout;
    private FirebaseFirestore db;
    private String user;
    private ArrayList<String> chatrooms;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatrooms);
        user=FirebaseAuth.getInstance().getCurrentUser().getUid();
        db=FirebaseFirestore.getInstance();
        if(user!=null){
           // chatrooms=getUserChatrooms();

        }


       layout=findViewById(R.id.chatroomListView);
        chatrooms=new ArrayList<>();
        getUserChatrooms();
        if(chatrooms.size()==0){
            chatrooms.add("Sie haben noch keine Chats!");
        }

        ArrayList<String> values=new ArrayList<>();
        values.add("Chatroom hallo");
        values.add("chatroom2");
        //"Chatroom1", "Chatroom2", "Chatroom3", "Chatroom4", "Chatroom5"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, chatrooms);
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

        Task<QuerySnapshot> ref=db.collection("chatrooms").whereEqualTo("userId", user).get().
                addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        ArrayList<String> chatrooms=new ArrayList<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("TAG", document.getId() + " => " + document.getData());
                                chatrooms.add(document.getString("chatName"));
                            }
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
        return chatrooms;
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
