package com.project.cdh.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.project.cdh.R;
import com.project.cdh.models.Chat;
import com.project.cdh.models.Message;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder>
{
    private final Context context;
    private final List<Chat> chats;
    private final OnChatItemClickListener onChatItemClickListener;
    private final FirebaseFirestore db;
    private final String userId;

    public ChatAdapter(Context context, List<Chat> chats, FirebaseFirestore db, String userId,OnChatItemClickListener onChatItemClickListener)
    {
        this.context = context;
        this.chats = chats;
        this.onChatItemClickListener = onChatItemClickListener;
        this.db = db;
        this.userId = userId;
    }

    public interface OnChatItemClickListener
    {
        void onChatItemClick(int position);
        void onChatItemLongClick(int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView nameTextView;
        TextView latestMessageTextView;
        TextView latestMessageTimeStampTextView;

        public ViewHolder(View itemView)
        {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.nameTextView);
            latestMessageTextView = itemView.findViewById(R.id.latestMessageTextView);
            latestMessageTimeStampTextView = itemView.findViewById(R.id.latestMessageTimeStampTextView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onChatItemClickListener.onChatItemClick(getBindingAdapterPosition());
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onChatItemClickListener.onChatItemLongClick(getBindingAdapterPosition());
                    return true;
                }
            });
        }
    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_chat, parent, false);

        ChatAdapter.ViewHolder viewHolder = new ChatAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, int position)
    {
        Chat chat = chats.get(position);
        String chatId = chat.getChatId();

        TextView nameTextView = holder.nameTextView;
        TextView latestMessageTextView = holder.latestMessageTextView;
        TextView latestMessageTimeStampTextViewTextView = holder.latestMessageTimeStampTextView;

        db.collection("chat")
                .document(chatId).collection("messages")
                .orderBy("timeStamp", Query.Direction.DESCENDING).limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots ->
                {
                    if(!queryDocumentSnapshots.isEmpty())
                    {
                        Message latestMessage = queryDocumentSnapshots.getDocuments()
                                .get(0).toObject(Message.class);
                        if(latestMessage.getContent().length() > 38 )
                            latestMessageTextView.setText(latestMessage.getContent().substring(0,30) + "...");
                        else
                            latestMessageTextView.setText(latestMessage.getContent());
                        Timestamp timestamp = latestMessage.getTimeStamp();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm a", Locale.US);
                        latestMessageTimeStampTextViewTextView.setText(simpleDateFormat.format(timestamp.toDate()));
                    }
                });

        if(userId.equals(chat.getPatientId()))
            nameTextView.setText(chat.getDoctorName());
        else
            nameTextView.setText(chat.getPatientName());

    }

    @Override
    public int getItemCount()
    {
        return chats.size();
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder)
    {
        holder.nameTextView.setText("");
        holder.latestMessageTextView.setText("");
        holder.latestMessageTimeStampTextView.setText("");
    }
}
