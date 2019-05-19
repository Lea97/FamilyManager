package dhbw.familymanager.familymanager.chat;

import android.annotation.SuppressLint;
import android.content.Intent;
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

import dhbw.familymanager.familymanager.MainActivity;
import dhbw.familymanager.familymanager.R;
import dhbw.familymanager.familymanager.model.ChatMessage;

public class ChatActivity extends AppCompatActivity {
    private FirebaseListAdapter<ChatMessage> fListAdapter;
    private FloatingActionButton createRoom;
    private ListView layout;
    private FirebaseFirestore db;
    private String user;
    private ArrayList<String> chatrooms;
    private View addChatroomButton;
    private ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatrooms);

        user = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();
        layout = findViewById(R.id.chatroomListView);
        chatrooms = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, chatrooms);

        if (user != null) {
            getUserChatrooms();
        }


        if (chatrooms.size() == 0) {
            chatrooms.add("Sie haben noch keine Chats!");
            arrayAdapter.notifyDataSetChanged();
        }


        layout.setAdapter(arrayAdapter);

        addChatroomButton = (View) findViewById(R.id.addChatroomButton);
        addChatroomButton.setOnClickListener(new View.OnClickListener() {
                                                 @Override
                                                 public void onClick(View v) {
                                                     startActivity(new Intent(ChatActivity.this, CreateChatroomActivity.class));
                                                 }
                                             }
        );


        layout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                String chatName = textView.getText().toString();
                startActivity(new Intent(ChatActivity.this, ChatMessagesActivity.class).putExtra("chatName", chatName));

            }
        });


    }


    private void getUserChatrooms() {

        final Task<QuerySnapshot> ref = db.collection("chatrooms").whereEqualTo("familyId", MainActivity.getFamily()).get().
                addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        chatrooms.clear();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("TAG", document.getId() + " => " + document.getData());
                                chatrooms.add(document.getString("chatName"));
                            }
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                        arrayAdapter.notifyDataSetChanged();

                    }
                });

    }


    private void displayChatMessages() {
        ListView listOfMessages = (ListView) findViewById(R.id.message);

        fListAdapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class,
                R.layout.message, FirebaseDatabase.getInstance().getReference()) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                TextView messageText = (TextView) v.findViewById(R.id.message_text);
                TextView messageUser = (TextView) v.findViewById(R.id.message_user);
                TextView messageTime = (TextView) v.findViewById(R.id.message_time);
                messageText.setText(model.getMessageText());
                messageUser.setText(model.getSenderId());
                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                        model.getTimestamp()));
            }
        };

        listOfMessages.setAdapter(fListAdapter);
    }


}
