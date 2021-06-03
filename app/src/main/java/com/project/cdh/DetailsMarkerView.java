package com.project.cdh;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.math.BigDecimal;

public class DetailsMarkerView extends MarkerView
{

    private TextView dailyTestTimeTextView;
    private TextView dailyTestLevelTextView;

    /**
     * Pass in your own layout and instantiated controls in the constructor
     * @param  context
     * @param  layout
     */
    public DetailsMarkerView(Context context, int layout)
    {
        super(context, layout);
        dailyTestTimeTextView = findViewById(R.id.dailyTestTimeTextView);
        dailyTestLevelTextView = findViewById(R.id.dailyTestLevelTextView);
    }

    //Each time you redraw, this method will be called to refresh the data
    @Override
    public void refreshContent(Entry e, Highlight highlight)
    {
        super.refreshContent(e, highlight);
        try
        {
            int time = (int) (e.getX() * 100);
            int hours = time / 100;
            String minutes = time % 100 + "";

            if(minutes.length() == 1)
                minutes = "0" + minutes;

            time = time / 100;
            if(time >=  12)
            {
                if(hours != 12)
                    dailyTestTimeTextView.setText((hours - 12) + ":" + minutes + " PM");
                else
                    dailyTestTimeTextView.setText(hours + ":" + minutes + " PM");
            }
            else
            {
                if(hours != 0)
                    dailyTestTimeTextView.setText(hours + ":" + minutes + " AM");
                else
                    dailyTestTimeTextView.setText((hours + 12) + ":" + minutes + " AM");
            }

            int dataSetIndex = highlight.getDataSetIndex();
            String unit = "";

            if(dataSetIndex == 1 || dataSetIndex == 3 || dataSetIndex == 6)
                unit = "mg/dl";
            else if(dataSetIndex == 2)
                unit = "mm Hg";
            else if(dataSetIndex == 4 || dataSetIndex == 5)
                unit = "u/l";

            String level ="Level: " + e.getY() + " " + unit;
            Spannable spannable = new SpannableString(level);
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(getContext().getColor(R.color.transparent_color));


            if(dataSetIndex == 1)
                foregroundColorSpan = new ForegroundColorSpan(getContext().getColor(R.color.glucose_color));
            else if(dataSetIndex  == 2)
                foregroundColorSpan = new ForegroundColorSpan(getContext().getColor(R.color.blood_pressure_color));
            else if(dataSetIndex == 3)
                foregroundColorSpan = new ForegroundColorSpan(getContext().getColor(R.color.total_cholesterol_color));
            else if(dataSetIndex == 4)
                foregroundColorSpan = new ForegroundColorSpan(getContext().getColor(R.color.hdl_color));
            else if(dataSetIndex == 5)
                foregroundColorSpan = new ForegroundColorSpan(getContext().getColor(R.color.ldl_color));
            else if(dataSetIndex == 6)
                foregroundColorSpan = new ForegroundColorSpan(getContext().getColor(R.color.triglyceride_color));


            spannable.setSpan(foregroundColorSpan, 7, level.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            dailyTestLevelTextView.setText(spannable);

        }
        catch (Exception e1)
        {
            e1.printStackTrace();
        }
        super.refreshContent(e, highlight);
    }

    //The offset of the layout. Is where the layout is displayed on the dot
    // -(width / 2) Center the layout horizontally
    //-(height) The layout is displayed above the dot
    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }

    public String concat(float money, String values)
    {
        return values + new BigDecimal(money).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "yuan";
    }

}