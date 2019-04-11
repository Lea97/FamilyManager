package dhbw.familymanager.familymanager.chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.ref.Reference;
import java.util.HashMap;
import java.util.Map;

import dhbw.familymanager.familymanager.R;
import dhbw.familymanager.familymanager.model.ChatMessage;
import dhbw.familymanager.familymanager.model.ChatRoom;

public class ChatMessagesActivity extends AppCompatActivity {
        LinearLayout layout;
        RelativeLayout layout_2;
        ImageView sendButton;
        EditText messageArea;
        ScrollView scrollView;
        FirebaseFirestore reference1, reference2;
        String user=FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        String chatroomId="", chatName;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.chat_messages);

            layout = (LinearLayout) findViewById(R.id.layout1);
            layout_2 = (RelativeLayout) findViewById(R.id.layout2);
            sendButton = (ImageView) findViewById(R.id.sendButton);
            messageArea = (EditText) findViewById(R.id.messageArea);
            scrollView = (ScrollView) findViewById(R.id.scrollView);
            Intent intent = getIntent();
            chatName = intent.getStringExtra("chatName");
            System.out.println(chatName);
            sendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String messageText = messageArea.getText().toString();

                    if(!messageText.equals("")){
                        ChatMessage message=new ChatMessage();
                        message.setMessageText(messageText);
                        message.setSenderId(user);
                        addMessageBox(messageText, 1);
                        //reference1.add(message);


                        messageArea.setText("");
                    }
                }
            });


            setUp();

        }


                private void setUp(){
            System.out.println(chatName);
                    db.collection("chatrooms").whereEqualTo("chatName", chatName).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            System.out.println(task.getResult().getDocuments().size());
                            //System.out.println(task.getResult().getDocuments().get(0).getId());
                            if(task.isSuccessful()&&task.getResult().getDocuments().size()==1){
                            chatroomId=task.getResult().getDocuments().get(0).getId();}
                            //ChatRoom room=task.getResult().toObjects(ChatRoom.class).get(0);
                           // chatroomId= String.valueOf(room.getChatId());
                        }
                    });
                    System.out.println(chatroomId);

                    FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                            addMessageBox(messageArea.getText().toString(), 1);
                        }
                    });


                    //reference1 = new FirebaseFirestore("https://androidchatapp-76776.firebaseio.com/messages/" + UserDetails.username + "_" + UserDetails.chatWith);
                    //reference2 = new Firebase("https://androidchatapp-76776.firebaseio.com/messages/" + UserDetails.chatWith + "_" + UserDetails.username);


                    //final DocumentReference docRef = FirebaseFirestore.getInstance().collection("chatrooms").document("");

                }








        public void addMessageBox(String message, int type){
                TextView textView = new TextView(ChatMessagesActivity.this);
                textView.setText(message);

                LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp2.weight = 1.0f;

                if(type == 1) {
                        lp2.gravity = Gravity.LEFT;
                        textView.setBackgroundResource(R.drawable.bubble_in);
                }
                else{
                        lp2.gravity = Gravity.RIGHT;
                        textView.setBackgroundResource(R.drawable.bubble_out);
                }
                textView.setLayoutParams(lp2);
                layout.addView(textView);
                scrollView.fullScroll(View.FOCUS_DOWN);
        }
}