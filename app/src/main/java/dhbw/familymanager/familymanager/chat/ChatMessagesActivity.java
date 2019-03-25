package dhbw.familymanager.familymanager.chat;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.Transaction;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import dhbw.familymanager.familymanager.MainActivity;
import dhbw.familymanager.familymanager.R;
import dhbw.familymanager.familymanager.model.ChatMessage;
import dhbw.familymanager.familymanager.model.ChatRoom;
import dhbw.familymanager.familymanager.model.Family;

public class ChatMessagesActivity extends AppCompatActivity {
    private String chatName;
    private EditText messageSent;
    private ImageButton sendMessage;
    private String family;
    private FirebaseUser user;
    private List<String> members;
    private String chatId;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user= FirebaseAuth.getInstance().getCurrentUser();
        family=MainActivity.getFamily();
        Task<QuerySnapshot> querySnapshotTask=FirebaseFirestore.getInstance().collection("chatrooms").whereEqualTo("chatName", chatName).get();
        ChatRoom current=querySnapshotTask.getResult().toObjects(ChatRoom.class).get(0);
        chatId=current.getChatId();

        chatName=getIntent().getStringExtra("chatName");
        setTitle(chatName);
        setContentView(R.layout.chat_messages);
        messageSent=findViewById(R.id.messageSent);
        sendMessage=findViewById(R.id.send_message);




    }
    public void sendMessage(String message){
        getFamilyMembers();
        for(String s:members){
            System.out.println(s);
        }
        Random r=new Random();
        ChatMessage messageObject=new ChatMessage();
        messageObject.setMessageId(String.valueOf(r.nextLong()));
        messageObject.setMessageText(messageSent.getText().toString());
        messageObject.setSenderId(user.getUid());
        FirebaseFirestore.getInstance().collection("chatrooms").document(chatId).collection("messages").add(ChatMessage.class);


    }

    private void getFamilyMembers() {


        Task<DocumentSnapshot> result=FirebaseFirestore.getInstance().collection("families").document(family).get().addOnCompleteListener(
                new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        members= (List<String>) task.getResult().get("members");


                    }
                }
        );
    }

    public void displayMessages(){
        //TODO implement
    }
}
