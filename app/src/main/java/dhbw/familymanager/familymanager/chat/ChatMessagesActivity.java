package dhbw.familymanager.familymanager.chat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.List;

import dhbw.familymanager.familymanager.R;
import dhbw.familymanager.familymanager.model.ChatMessage;

public class ChatMessagesActivity extends AppCompatActivity {
    LinearLayout layout;
    RelativeLayout layout_2;
    List<ChatMessage> messages;
    private ImageView sendButton;
    private EditText messageArea;
    private ScrollView scrollView;
    private String user = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String chatroomId, chatName;
    private long timestamp = 0;

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
        setUp();
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageArea.getText().toString();

                if (!messageText.equals("")) {
                    ChatMessage message = new ChatMessage();
                    message.setMessageText(messageText);
                    message.setSenderId(user);
                    message.setTimestamp(new Date().getTime());
                    db.collection("chatrooms").document(chatroomId).collection("messages").add(message);
                    messageArea.setText("");
                }
            }
        });
    }

    private void readChatMessages() {
        db.collection("chatrooms").document(chatroomId).collection("messages").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (!task.getResult().isEmpty())
                    messages = task.getResult().toObjects(ChatMessage.class);
                if (messages != null)
                    messages.sort((e1, e2) -> new Long(e1.getTimestamp()).compareTo(new Long(e2.getTimestamp())));

                if (messages != null)
                    for (ChatMessage message : messages) {
                        if (message.getTimestamp() > timestamp) {

                            if (message.getSenderId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                addMessageBox(message.getMessageText(), 2);
                            } else {
                                addMessageBox(message.getMessageText(), 1);
                            }
                        }
                    }
                if (messages != null)
                    timestamp = messages.get(messages.size() - 1).getTimestamp();
            }
        });
    }

    private void setUp() {
        System.out.println(chatName);
        db.collection("chatrooms").whereEqualTo("chatName", chatName).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                System.out.println(task.getResult().getDocuments().size());
                //System.out.println(task.getResult().getDocuments().get(0).getId());
                if (task.isSuccessful()) {
                    chatroomId = task.getResult().getDocuments().get(0).getId();
                }
                getChatroomId();
            }
        });
        System.out.println(chatroomId);
    }

    private void getChatroomId() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("chatrooms").document(chatroomId).collection("messages").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {

                readChatMessages();
            }
        });
        readChatMessages();
    }

    public void addMessageBox(String message, int type) {
        TextView textView = new TextView(ChatMessagesActivity.this);
        textView.setText(message);

        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.weight = 1.0f;

        if (type == 1) {
            lp2.gravity = Gravity.LEFT;
            textView.setBackgroundResource(R.drawable.bubble_in);
        } else {
            lp2.gravity = Gravity.RIGHT;
            textView.setBackgroundResource(R.drawable.bubble_out);
        }
        textView.setLayoutParams(lp2);
        layout.addView(textView);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }
}