package com.example.healthza.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.healthza.R;
import com.example.healthza.adapters.ChatAdapter;
import com.example.healthza.models.Chat;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DoctorChatListActivity extends AppCompatActivity implements ChatAdapter.OnChatItemClickListener
{
    ///////////////////////////////////////////////
    private ListenerRegistration chatEventListener;
    private ImageView emptyChatListimageView;
    private TextView emptyChatListTextView;
    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private List<Chat> chats;
    private Set<String> chatsIds;
    private String doctorId;
    private FloatingActionButton addChatFloatingActionButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    ///////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_chat_list);

        getSupportActionBar().setTitle("Chat");


        emptyChatListimageView = findViewById(R.id.emptyChatListimageView);
        emptyChatListTextView = findViewById(R.id.emptyChatListTextView);
        addChatFloatingActionButton = findViewById(R.id.addChatFloatingActionButton);
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        doctorId = firebaseAuth.getCurrentUser().getUid();

        chats = new ArrayList<>();
        chatsIds = new HashSet<>();
        chatRecyclerView = findViewById(R.id.chatRecyclerView);

        chatAdapter = new ChatAdapter(this, chats,db, this);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);

        addChatFloatingActionButton.setOnClickListener(v ->
        {
           addNewChat();
        });
    }

    private void getChats()
    {
        CollectionReference chatsRef = db.collection("chat");

        chatEventListener = chatsRef.whereEqualTo("doctorId",doctorId)
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
                                Chat chat = document.toObject(Chat.class);
                                String chatId = chat.getChatId();

                                if(!chatsIds.contains(chatId))
                                {
                                    chatsIds.add(chatId);
                                    chats.add(chat);
                                }
                            }
                            if (chats.isEmpty())
                            {
                                emptyChatListimageView.setVisibility(View.VISIBLE);
                                emptyChatListTextView.setVisibility(View.VISIBLE);
                            }
                        }
                        chatAdapter.notifyDataSetChanged();
                    }
                });
    }


    private void addNewChat()
    {
        new DoctorContactDialog(this, emptyChatListimageView, emptyChatListTextView);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        getChats();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (chatEventListener != null)
            chatEventListener.remove();
    }

    @Override
    public void onChatItemClick(int position)
    {
        Chat chat = chats.get(position);
        String chatId = chat.getChatId();
        String chatName = chat.getPatientName();

        Intent intent = new Intent(this, ChatMessagesActivity.class);
        intent.putExtra("chatId", chatId);
        intent.putExtra("chatName", chatName);

        startActivity(intent);
    }

    @Override
    public void onChatItemLongClick(int position)
    {
        Chat chat = chats.get(position);
        String chatId = chat.getChatId();

        //batch write can contain up to 500 operation.. so this will work for deleting only 500 messages
        // other method is to use cloud functions .. but spark plan doesn't include it
        WriteBatch batch = db.batch();

        CollectionReference messagesRef = db.collection("chat").document(chatId)
                .collection("messages");

        messagesRef.get().addOnSuccessListener(queryDocumentSnapshots ->
        {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots)
            {
                String messageId = document.getId();
                DocumentReference messageRef = messagesRef.document(messageId);

                batch.delete(messageRef);
            }
            batch.commit();
        });

        db.collection("chat").document(chatId)
             .delete();

        chats.remove(position);
        chatAdapter.notifyItemRemoved(position);

        db.collection("patients").document(chat.getPatientId())
                .collection("doctors").document(chat.getDoctorId())
                .update("startedChat",false);

        db.collection("doctors").document(chat.getDoctorId())
                .collection("patients").document(chat.getPatientId())
                .update("startedChat",false);


        if(chats.isEmpty())
        {
            emptyChatListTextView.setVisibility(View.VISIBLE);
            emptyChatListimageView.setVisibility(View.VISIBLE);
        }
    }
}