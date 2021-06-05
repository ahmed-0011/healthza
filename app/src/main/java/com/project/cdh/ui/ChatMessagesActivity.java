package com.project.cdh.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.project.cdh.R;
import com.project.cdh.adapters.MessageAdapter;
import com.project.cdh.models.Message;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChatMessagesActivity extends AppCompatActivity
{
    private ImageView noMessagesImageView;
    private EditText newMessageEditText;
    private ImageButton sendNewMessageImageButton;
    private TextView noMessagesTextView;
    private ListenerRegistration chatMessageEventListener;
    private String chatId;
    private String chatName;
    private RecyclerView chatMessageRecyclerView;
    private MessageAdapter chatMessageAdapter;
    private List<Message> messages = new ArrayList<>();
    private Set<String> messagesIds = new HashSet<>();
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_messages);

        Intent intent = getIntent();
        chatId = intent.getStringExtra("chatId");
        chatName = intent.getStringExtra("chatName");

        noMessagesImageView = findViewById(R.id.noMessagesImageView);
        noMessagesTextView = findViewById(R.id.noMessagesTextView);
        newMessageEditText = findViewById(R.id.newMessageEditText);
        sendNewMessageImageButton = findViewById(R.id.sendNewMessageImageButton);
        chatMessageRecyclerView = findViewById(R.id.chatMessagesRecyclerView);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();

        setChatName();
        initChatRecyclerView();

        sendNewMessageImageButton.setOnClickListener(v -> sendNewMessage());
    }

    private void initChatRecyclerView()
    {
        chatMessageAdapter = new MessageAdapter(this, messages, userId);
        chatMessageRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatMessageRecyclerView.setAdapter(chatMessageAdapter);

        chatMessageRecyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener()
        {
            @Override
            public void onLayoutChange(View v,
                                       int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom)
            {
                if (bottom < oldBottom)
                {
                    chatMessageRecyclerView.postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            if (messages.size() > 0)
                            {
                                chatMessageRecyclerView.smoothScrollToPosition(
                                        chatMessageRecyclerView.getAdapter().getItemCount() - 1);
                            }
                        }
                    }, 100);
                }
            }
        });
    }

    private void setChatName()
    {
        getSupportActionBar().setTitle(chatName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void getChatMessages()
    {
        CollectionReference messagesRef = db.collection("chat").document(chatId)
                .collection("messages");

        chatMessageEventListener = messagesRef
                .orderBy("timeStamp", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>()
                {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error)
                    {
                        if (error != null)
                        {
                            //Log.e(TAG, "onEvent: Listen failed.", e);
                            return;
                        }

                        if (queryDocumentSnapshots != null)
                        {
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots)
                            {
                                Message message = document.toObject(Message.class);
                                String messageId = message.getMessageId();

                                if (!messagesIds.contains(messageId))
                                {
                                    messagesIds.add(messageId);
                                    messages.add(message);
                                    chatMessageRecyclerView.smoothScrollToPosition(messages.size() - 1);
                                }
                            }

                            if(messages.isEmpty())
                            {
                                noMessagesImageView.setVisibility(View.VISIBLE);
                                noMessagesTextView.setVisibility(View.VISIBLE);
                            }

                        }
                        chatMessageAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void sendNewMessage()
    {
        String messageContent = newMessageEditText.getText().toString();

        if (!messageContent.isEmpty())
        {
            messageContent = messageContent.replaceAll(System.getProperty("line.separator"), "");

            DocumentReference newMessageRef = db
                    .collection("chat")
                    .document(chatId)
                    .collection("messages")
                    .document();

            String messageId = newMessageRef.getId();
            String senderId = firebaseAuth.getCurrentUser().getUid();
            String senderName = firebaseAuth.getCurrentUser().getDisplayName();

            Timestamp timestamp = Timestamp.now();
            Message message = new Message(messageContent, timestamp, senderId, senderName, messageId );

            newMessageRef.set(message).addOnCompleteListener(new OnCompleteListener<Void>()
            {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    if (task.isSuccessful())
                    {
                        newMessageEditText.setText("");
                        noMessagesImageView.setVisibility(View.GONE);
                        noMessagesTextView.setVisibility(View.GONE);
                    }
                    else
                        {
                        View parentLayout = findViewById(android.R.id.content);
                        Snackbar.make(parentLayout, "Something went wrong...", Snackbar.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        getChatMessages();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (chatMessageEventListener != null)
            chatMessageEventListener.remove();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}