package com.example.healthza.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthza.R;
import com.example.healthza.adapters.ContactAdapter;
import com.example.healthza.models.Chat;
import com.example.healthza.models.Contact;
import com.example.healthza.models.Patient;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class DoctorContactDialog implements ContactAdapter.OnContactItemClickListener
{

    private Context context;
    private ImageView emptyChatListimageView;
    private TextView emptyChatListTextView;
    private ImageView emptyContactListimageView;
    private TextView emptyContactListTextView;
    private AlertDialog contactDialog;
    private List<Contact> contacts = new ArrayList<>();
    private RecyclerView contactsRecyclerView;
    private ContactAdapter contactAdapter;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;

    public DoctorContactDialog(Context context, ImageView emptyChatListimageView, TextView emptyChatListTextView)
    {
        this.context = context;
        this.emptyChatListimageView = emptyChatListimageView;
        this.emptyChatListTextView = emptyChatListTextView;
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        showContactDialog();
    }

    private void showContactDialog()
    {
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.dialog_contact, null);

        emptyContactListimageView = view.findViewById(R.id.emptyContactListimageView);
        emptyContactListTextView = view.findViewById(R.id.emptyContactListTextView);
        contactsRecyclerView = view.findViewById(R.id.contactsRecyclerView);


        String doctorId = firebaseAuth.getCurrentUser().getUid();

        CollectionReference patientsRef = db.collection("doctors").document(doctorId)
                .collection("patients");

        patientsRef.get().addOnCompleteListener(task ->
        {
            if (task.isSuccessful())
            {
                Patient patient;
                for (QueryDocumentSnapshot document : task.getResult())
                {
                    boolean startedChat = document.getBoolean("startedChat");
                    if (!startedChat)   // if the doctor doesn't start a chat with this patient
                    {
                        patient = document.toObject(Patient.class);
                        contacts.add(new Contact(patient));
                    }
                }

                if (contacts.isEmpty())
                {
                    emptyContactListimageView.setVisibility(View.VISIBLE);
                    emptyContactListTextView.setVisibility(View.VISIBLE);
                }

                contactAdapter = new ContactAdapter(context, contacts, this);

                contactsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                contactsRecyclerView.setAdapter(contactAdapter);

                contactDialog = new AlertDialog.Builder(context)
                        .create();

                contactDialog.show();

                int height = (int) (context.getResources().getDisplayMetrics().heightPixels * 0.80);
                contactDialog.getWindow().setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, height);
                contactDialog.setContentView(view);
            }
        });
    }

    @Override
    public void onContactItemClick(int position)
    {
        Toast.makeText(context, contacts.get(position).getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onContactItemLongClick(int position)
    {

    }

    @Override
    public void onStartChatImageButtonClick(int position)
    {
        contactDialog.dismiss();

        emptyChatListimageView.setVisibility(View.GONE);
        emptyChatListTextView.setVisibility(View.GONE);

        emptyContactListimageView.setVisibility(View.GONE);
        emptyContactListTextView.setVisibility(View.GONE);

        Contact contact = contacts.get(position);

        String doctorId = firebaseAuth.getCurrentUser().getUid();
        String doctorName = firebaseAuth.getCurrentUser().getDisplayName();

        String patientId = contact.getContactId();
        String patientName = contact.getName();
        String chatId = doctorId + "_" + patientId;

        DocumentReference chatRef = db.collection("chat")
                .document(chatId);

        Timestamp timestamp = Timestamp.now();
        Chat chat = new Chat(chatId, doctorName, patientName,  doctorId, patientId, "", timestamp);
        chatRef.set(chat);

        DocumentReference doctorRef = db.collection("patients")
                .document(patientId)
                .collection("doctors")
                .document(doctorId);

        DocumentReference patientRef = db.collection("doctors")
                .document(doctorId)
                .collection("patients")
                .document(patientId);

        patientRef.update("startedChat", true);
        doctorRef.update("startedChat", true);

        contacts.remove(position);
        contactAdapter.notifyItemRemoved(position);

        if (contacts.isEmpty())
        {
            emptyContactListimageView.setVisibility(View.VISIBLE);
            emptyContactListTextView.setVisibility(View.VISIBLE);
        }
    }
}
