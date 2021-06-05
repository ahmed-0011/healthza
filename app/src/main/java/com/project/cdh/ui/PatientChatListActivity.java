package com.project.cdh.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.project.cdh.adapters.ChatAdapter;
import com.project.cdh.models.Chat;
import com.project.cdh.R;
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
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PatientChatListActivity extends AppCompatActivity implements ChatAdapter.OnChatItemClickListener
{
    private ListenerRegistration chatEventListener;
    private ImageView emptyChatListimageView;
    private TextView emptyChatListTextView;
    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private List<Chat> chats;
    private Set<String> chatsIds;
    private String patientId;
    private FloatingActionButton addChatFloatingActionButton;
    private ChipNavigationBar chipNavigationBar;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_chat_list);

        getSupportActionBar().setTitle("Chat");

        emptyChatListimageView = findViewById(R.id.emptyChatListimageView);
        emptyChatListTextView = findViewById(R.id.emptyChatListTextView);
        addChatFloatingActionButton = findViewById(R.id.addChatFloatingActionButton);
        chipNavigationBar = findViewById(R.id.bottomNavigationBar);

        setupBottomNavigationBar();

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        patientId = firebaseAuth.getCurrentUser().getUid();

        chats = new ArrayList<>();
        chatsIds = new HashSet<>();
        chatRecyclerView = findViewById(R.id.chatRecyclerView);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        chatAdapter = new ChatAdapter(this, chats, db, patientId,this);
        chatRecyclerView.addItemDecoration(dividerItemDecoration);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);

        addChatFloatingActionButton.setOnClickListener(v -> addNewChat());
    }


    private void setupBottomNavigationBar()
    {
        chipNavigationBar.setItemSelected(R.id.chatItem, true);

        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(int i)
            {

                Intent intent = null;

                if (i == R.id.chatItem)
                    return;
                else if (i == R.id.homeItem)
                    intent = new Intent(PatientChatListActivity.this, PatientHomeActivity.class);
                else if (i == R.id.medicalHistoryItem)
                    intent = new Intent(PatientChatListActivity.this, PatientMedicalHistoryActivity.class);
                else if (i == R.id.chartsItem)
                    intent = new Intent(PatientChatListActivity.this, PatientChartsActivity.class);
                else if (i == R.id.appointmentsItem)
                    intent = new Intent(PatientChatListActivity.this, PatientAppointmentsActivity.class);

                if(intent != null)
                {
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            }
        });
    }


    private void getChats()
    {
        CollectionReference chatsRef = db.collection("chat");

        chatEventListener = chatsRef.whereEqualTo("patientId",patientId)
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
        new PatientContactDialog(this, emptyChatListimageView, emptyChatListTextView);
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
        String chatName = chat.getDoctorName();

        Intent intent = new Intent(this, ChatMessagesActivity.class);
        intent.putExtra("chatId", chatId);
        intent.putExtra("chatName", chatName);

        startActivity(intent);
        overridePendingTransition(R.anim.zoom_in, R.anim.static_animation);
    }


    @Override
    public void onChatItemLongClick(int position)
    {
        Chat chat = chats.get(position);

        new MaterialAlertDialogBuilder(this, R.style.DeleteDialogTheme)
                .setTitle("Delete Chat")

                .setMessage("Do you want to delete chat with " + chat.getDoctorName() + " ?")
                .setPositiveButton("DELETE", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {

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
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) { }
                })
                .show();
    }


    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(this, PatientHomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}