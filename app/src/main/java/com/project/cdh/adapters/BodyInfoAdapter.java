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
import com.project.cdh.StickHeaderItemDecoration;
import com.project.cdh.models.BodyInfo;
import com.google.firebase.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class BodyInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements StickHeaderItemDecoration.StickyHeaderInterface
{
    private final Context context;
    private List<BodyInfo> bodyInfoList;
    private OnBodyInfoItemClickListener onBodyInfoItemClickListener;
    private static final int HEADER = 0, BODY_INFO = 1;

    public BodyInfoAdapter(Context context, List<BodyInfo> bodyInfoList, OnBodyInfoItemClickListener onBodyInfoItemClickListener)
    {
        this.context = context;
        this.bodyInfoList = bodyInfoList;
        this.onBodyInfoItemClickListener = onBodyInfoItemClickListener;
    }


    public interface OnBodyInfoItemClickListener
    {
        void onBodyInfoItemClick(int position);
        void onBodyInfoItemLongClick(int position);
        void onRemoveButtonClick(int position);
    }


    class HeaderViewHolder extends RecyclerView.ViewHolder
    {
        public HeaderViewHolder(@NonNull View itemView)
        {
            super(itemView);
        }

        private void setBodyInfoHeader() { }
    }


    class BodyInfoViewHolder extends RecyclerView.ViewHolder
    {
        private TextView bmiTextView;
        private TextView weightTextView;
        private TextView heightTextView;
        private TextView timeTextView;
        private Button removeButton;


        public BodyInfoViewHolder(@NonNull View itemView)
        {
            super(itemView);

            bmiTextView = itemView.findViewById(R.id.bmiTextView);
            weightTextView = itemView.findViewById(R.id.weightTextView);
            heightTextView = itemView.findViewById(R.id.heightTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            removeButton= itemView.findViewById(R.id.removeButton);
        }


        private void setBodyInfoRecord(BodyInfo bodyInfo)
        {
            String bmi = bodyInfo.getBmi() + "";
            String weight = bodyInfo.getWeight() + "";
            String height = bodyInfo.getHeight() + "";

            bmiTextView.setText(bmi);
            weightTextView.setText(weight);
            heightTextView.setText(height);

            Timestamp timestamp = bodyInfo.getTimestamp();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.US);
            timeTextView.setText(simpleDateFormat.format(timestamp.toDate()));


            itemView.setOnClickListener(v -> onBodyInfoItemClickListener.onBodyInfoItemClick(getBindingAdapterPosition()));

            itemView.setOnLongClickListener(v ->
            {
                onBodyInfoItemClickListener.onBodyInfoItemLongClick(getBindingAdapterPosition());
                return true;
            });

            removeButton.setOnClickListener(v ->
                    onBodyInfoItemClickListener.onRemoveButtonClick(getBindingAdapterPosition())
            );
        }


        private void clearViewHolder()
        {
            bmiTextView.setText("");
            weightTextView.setText("");
            heightTextView.setText("");
            removeButton.setOnClickListener(null);
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        if (viewType == HEADER)
        {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.header_body_info, parent, false);

            return new BodyInfoAdapter.HeaderViewHolder(view);
        }
        else
        {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_body_info, parent, false);

            return new BodyInfoViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        BodyInfo bodyInfo = bodyInfoList.get(position);

        if(getItemViewType(position) == HEADER)
            ((HeaderViewHolder) holder).setBodyInfoHeader();
        else
            ((BodyInfoViewHolder) holder).setBodyInfoRecord(bodyInfo);

    }

    @Override
    public int getItemCount()
    {
        return bodyInfoList.size();
    }

    @Override
    public int getItemViewType(int position)
    {
        if(position == 0)
            return HEADER;
        else
            return BODY_INFO;
    }

    @Override
    public int getHeaderPositionForItem(int itemPosition)
    {
        int headerPosition = 0;
        do {
            if (this.isHeader(itemPosition)) {
                headerPosition = itemPosition;
                break;
            }
            itemPosition -= 1;
        } while (itemPosition >= 0);
        return headerPosition;
    }


    @Override
    public int getHeaderLayout(int headerPosition)
    {
        return R.layout.header_body_info;
    }


    @Override
    public void bindHeaderData(View header, int headerPosition)
    {
        // binding our header data here
    }


    @Override
    public boolean isHeader(int itemPosition)
    {
        return itemPosition == 0;
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder)
    {

        if(holder instanceof BodyInfoViewHolder)
            ((BodyInfoViewHolder) holder).clearViewHolder();
    }
}


