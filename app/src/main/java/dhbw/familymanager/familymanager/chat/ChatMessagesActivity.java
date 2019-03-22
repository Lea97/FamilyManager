package dhbw.familymanager.familymanager.chat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import dhbw.familymanager.familymanager.R;
import dhbw.familymanager.familymanager.model.ChatRoom;

public class ChatMessagesActivity extends AppCompatActivity {
    private String chatName;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chatName=getIntent().getStringExtra("chatName");
        setTitle(chatName);
        setContentView(R.layout.chat_messages);




    }
    public void sendMessage(){
        //TODO implement
    }
    public void displayMessages(){
        //TODO implement
    }
}
