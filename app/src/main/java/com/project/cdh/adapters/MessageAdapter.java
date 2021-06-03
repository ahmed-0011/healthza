package com.project.cdh.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.cdh.models.Message;
import com.project.cdh.R;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private final Context context;
    private final List<Message> messages;
    private final FirebaseFirestore db;
    private final String userId;
    private static final int SENDER = 0, RECEIVER = 1;

    public MessageAdapter(Context context, List<Message> messages, String userId)
    {
        this.context = context;
        this.messages = messages;
        this.userId = userId;
        db = FirebaseFirestore.getInstance();
    }

    class SenderViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView senderTextView;
        private final TextView messageTextView;
        private final TextView messageTimestampTextView;

        public SenderViewHolder(View itemView)
        {
            super(itemView);

            senderTextView = itemView.findViewById(R.id.senderTextView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            messageTimestampTextView = itemView.findViewById(R.id.messageTimestampTextView);
        }

        private void setSendMessage(Message message)
        {
            senderTextView.setText(message.getSenderName());
            messageTextView.setText(message.getContent());
            Timestamp timestamp = message.getTimeStamp();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm a", Locale.US);
            messageTimestampTextView.setText(simpleDateFormat.format(timestamp.toDate()));
        }
    }

    class ReceiverViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView receiverTextView;
        private final TextView messageTextView;
        private final TextView messageTimestampTextView;

        public ReceiverViewHolder(View itemView)
        {
            super(itemView);

            receiverTextView = itemView.findViewById(R.id.receiverTextView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            messageTimestampTextView = itemView.findViewById(R.id.messageTimestampTextView);
        }

        private void setReceiveMessage(Message message)
        {
            receiverTextView.setText(message.getSenderName());
            messageTextView.setText(message.getContent());
            Timestamp timestamp = message.getTimeStamp();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm a", Locale.US);
            messageTimestampTextView.setText(simpleDateFormat.format(timestamp.toDate()));
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        if(viewType == SENDER)
        {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_send_message, parent, false);

            return new SenderViewHolder(view);
        }
        else
        {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_receive_message, parent, false);

            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        Message message = messages.get(position);

        if(getItemViewType(position) == SENDER)
            ((SenderViewHolder) holder).setSendMessage(message);
        else
            ((ReceiverViewHolder) holder).setReceiveMessage(message);
    }

    @Override
    public int getItemViewType(int position)
    {
        Message message = messages.get(position);

        if(message.getSenderId().equals(userId))
            return SENDER;
        else
            return RECEIVER;
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

}
