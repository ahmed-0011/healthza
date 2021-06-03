package com.project.cdh.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.cdh.R;
import com.project.cdh.models.Contact;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder>
{
    private final Context context;
    private final List<Contact> contacts;
    private final OnContactItemClickListener onContactItemClickListener;

    public ContactAdapter(Context context, List<Contact> contacts,  OnContactItemClickListener onContactItemClickListener)
    {
        this.context = context;
        this.contacts = contacts;
        this.onContactItemClickListener = onContactItemClickListener;
    }

    public interface OnContactItemClickListener
    {
        void onContactItemClick(int position);
        void onContactItemLongClick(int position);
        void onStartChatImageButtonClick(int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView nameTextView;
        private final TextView phoneNumberTextView;
        private final Button startChatButton;

        public ViewHolder(View itemView)
        {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.contactNameTextView);
            phoneNumberTextView = itemView.findViewById(R.id.contactPhoneNumberTextView);
            startChatButton = itemView.findViewById(R.id.startChatButton);

        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_contact, parent, false);

        ContactAdapter.ViewHolder viewHolder = new ContactAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Contact contact = contacts.get(position);

        TextView nameTextView = holder.nameTextView;
        TextView phoneNumberTextView = holder.phoneNumberTextView;
        Button startChatButton = holder.startChatButton;

        nameTextView.append(contact.getName());
        phoneNumberTextView.append(contact.getPhoneNumber());

        startChatButton.setOnClickListener(v ->
        {
            onContactItemClickListener.onStartChatImageButtonClick(holder.getBindingAdapterPosition());
        });

    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }
}
