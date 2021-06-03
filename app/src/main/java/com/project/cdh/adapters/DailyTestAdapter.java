package com.project.cdh.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.cdh.R;
import com.project.cdh.StickHeaderItemDecoration;
import com.project.cdh.models.DailyTest;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DailyTestAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements StickHeaderItemDecoration.StickyHeaderInterface
{
    private final Context context;
    private List<DailyTest> dailyTests;
    private static final int HEADER = 0, DAILY_TEST = 1;
    public DailyTestAdapter(Context context, List<DailyTest> dailyTests)
    {
        this.context = context;
        this.dailyTests = dailyTests;
    }


    public interface OnDailyTestItemClickListener
    {
        void onDailyTestItemClick(int position);
        void onDailyTestItemLongClick(int position);
    }


    class HeaderViewHolder extends RecyclerView.ViewHolder
    {

        public HeaderViewHolder(@NonNull View itemView)
        {
            super(itemView);
        }

        private void setTestsHeader()
        {

        }
    }

    class DailyTestViewHolder extends RecyclerView.ViewHolder
    {
        private TextView testTypeTextView;
        private TextView testLevelTextView;
        private TextView testTimeTextView;

        public DailyTestViewHolder(@NonNull View itemView)
        {
            super(itemView);

            testTypeTextView = itemView.findViewById(R.id.testTypeTextView);
            testLevelTextView = itemView.findViewById(R.id.testLevelTextView);
            testTimeTextView = itemView.findViewById(R.id.testTimeTextView);
        }

        private void setDailyTest(DailyTest dailyTest)
        {
            String unit = "";
            String testType = dailyTest.getType();
            testTypeTextView.setText(testType);

            if(testType.equals("Glucose"))
            {
                unit = "mg/dl";
                testTypeTextView.setTextColor(context.getColor(R.color.glucose_color));
            }
            else if(testType.equals("Blood Pressure"))
            {
                unit = "mm Hg";
                testTypeTextView.setTextColor(context.getColor(R.color.blood_pressure_color));
            }
            else if(testType.equals("Total Cholesterol"))
            {
                unit = "mg/dl";
                testTypeTextView.setTextColor(context.getColor(R.color.total_cholesterol_color));
            }
            else if(testType.equals("HDL"))
            {
                unit = "u/l";
                testTypeTextView.setTextColor(context.getColor(R.color.hdl_color));
            }
            else if(testType.equals("LDL"))
            {
                unit = "u/l";
                testTypeTextView.setTextColor(context.getColor(R.color.ldl_color));
            }
            else if(testType.equals("Triglyceride"))
            {
                unit = "mg/dl";
                testTypeTextView.setTextColor(context.getColor(R.color.triglyceride_color));
            }

            testLevelTextView.setText(dailyTest.getLevel() + " " + unit);

            Timestamp timestamp = dailyTest.getTimestamp();
            Date date = timestamp.toDate();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm aa", Locale.US);
            testTimeTextView.setText(simpleDateFormat.format(date));
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        if (viewType == HEADER)
        {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.header_daily_tests, parent, false);

            return new HeaderViewHolder(view);
        }
        else
        {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_daily_test, parent, false);

            return new DailyTestViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {

        DailyTest dailyTest = dailyTests.get(position);

        if(getItemViewType(position) == HEADER)
            ((HeaderViewHolder) holder).setTestsHeader();
        else
            ((DailyTestViewHolder) holder).setDailyTest(dailyTest);

    }

    @Override
    public int getItemCount()
    {
        return dailyTests.size();
    }

    @Override
    public int getItemViewType(int position)
    {
        if(position == 0)
            return HEADER;
        else
            return DAILY_TEST;
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
        return R.layout.header_daily_tests;
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

}
