package dhbw.familymanager.familymanager.chat;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Random;

import dhbw.familymanager.familymanager.R;
import dhbw.familymanager.familymanager.model.ChatRoom;

public class CreateChatroomActivity extends AppCompatActivity {
    private EditText roomName;
    private Button createChat;



    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);
        roomName = findViewById(R.id.room_name);
        setTitle(getString(R.string.create_room));
        createChat=findViewById(R.id.addChatroom);
        createChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatRoom createdChatroom=createChatroomObject();
                persistChatroomObject(createdChatroom);
                FirebaseFirestore db=FirebaseFirestore.getInstance();

            }
        });

    }

    private void persistChatroomObject(ChatRoom createdChatroom) {
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        db.collection("chatrooms").add(createdChatroom).
                addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                System.out.println("Chatroom added to database");

            }
        });
    }

    private ChatRoom createChatroomObject() {
        Random r=new Random();
        ChatRoom chatroom=new ChatRoom();
        chatroom.setChatId(String.valueOf(r.nextLong()));
        chatroom.setUserId(FirebaseAuth.getInstance().getCurrentUser().getUid());
        chatroom.setChatName(roomName.getText().toString());
        return  chatroom;



    }

}
