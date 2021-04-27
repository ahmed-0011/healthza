package com.example.healthza.adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthza.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RecordsAdapter extends RecyclerView.Adapter<RecordsAdapter.ViewHolder>
{
    Map<String, Object> record;
    List<Map<String, Object>> recordsList;
    private Context context;
    private OnRecordItemClickListener onRecordItemClickListener;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;

    public RecordsAdapter(Context context, List<Map<String, Object>> recordsList , OnRecordItemClickListener onRecordItemClickListener)
    {
        this.context = context;
        this.recordsList = recordsList;
        this.onRecordItemClickListener = onRecordItemClickListener;
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public interface OnRecordItemClickListener
    {
        void onItemClick(int position);

        void onItemLongClick(int position);

        void onRemoveButtonClick(int position);

        void onViewButtonClick(int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        private TextView testType, addDate, addTime,value;
        private Button removeButton;
        private Button viweButton;
        private View bac;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            testType = itemView.findViewById(R.id.testTypeTextView);
            addDate = itemView.findViewById(R.id.addDateTextView);
            addTime = itemView.findViewById(R.id.addTimeTextView);
            value = itemView.findViewById(R.id.testValuesTextView);
            removeButton = itemView.findViewById(R.id.removeTestButton);
            viweButton = itemView.findViewById(R.id.viweTestButton);
            bac = itemView.findViewById(R.id.bac);

            //viweButton.setEnabled(false); viweButton.setVisibility(View.INVISIBLE);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    onRecordItemClickListener.onItemClick(getBindingAdapterPosition());
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v)
                {
                    onRecordItemClickListener.onItemLongClick(getBindingAdapterPosition());
                    return true;
                }
            });
        }
    }

    @NonNull
    @Override
    public RecordsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.test_item, parent, false);

        RecordsAdapter.ViewHolder viewHolder = new RecordsAdapter.ViewHolder(view);
        return viewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull RecordsAdapter.ViewHolder holder, int position) {

        Map<String, Object> record = recordsList.get(position);

        TextView test_Type = holder.testType;
        TextView add_Date = holder.addDate;
        TextView add_Time = holder.addTime;
        TextView value_ = holder.value;
        View bac = holder.bac;

        Button remove_Button = holder.removeButton;

        remove_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // System.out.println("GGG : "+holder.getAdapterPosition());
               // System.out.println("GGG : "+holder.getLayoutPosition());
                String arg [] = (String[]) record.get("del");

                if(arg[1].equals("comprehensive"))
                {
                    String delT [] =new String[]{"fbs_test","liver_test" ,"Kidneys_test","cholesterolAndFats_test", "other_test"};
                    if(delT!=null) {
                        for (int f = 0; f < delT.length; f++) {
                            DocumentReference testRefs = db.collection("patients").document(arg[0])
                                    .collection("tests").document(delT[f]).collection(arg[2]).document(arg[3]);
                            testRefs.delete().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {

                                }
                            });
                        }
                        recordsList.remove(holder.getLayoutPosition());
                        notifyDataSetChanged();
                        notifyItemRemoved(holder.getAdapterPosition());
                    }
                    return;
                }
                DocumentReference testRefs = db.collection("patients").document(arg[0])
                        .collection("tests").document(arg[1]).collection(arg[2]).document(arg[3]);
                testRefs.delete().addOnCompleteListener(task -> {
                    if(task.isSuccessful())
                    {
                        recordsList.remove(holder.getLayoutPosition());
                        notifyDataSetChanged();
                        notifyItemRemoved(holder.getAdapterPosition());
                       // Toast.makeText(context, "Patient " + "\"" + patient.getName() + "\" " + "has beed deleted successfully.",Toast.LENGTH_LONG).show();
                    }
                    else
                        Toast.makeText(context, "Error", Toast.LENGTH_LONG).show();
                });
            }
        });
        Button viwe_Button = holder.viweButton;

       int typ = (int) record.getOrDefault("type",-1);

       switch (typ)
       {
           case 1:
           {
               bac.setBackgroundColor(Color.rgb(229,230,241));
               String tmp = "Test Type : Glucose Test";
               test_Type.setText(tmp);

               tmp ="Add Date: " + record.get("addDate");
               add_Date.setText(tmp);

               tmp = "Add Time: " + record.get("addTime");
               add_Time.setText(tmp);

               value_.setText("Glucose Percent: "+record.get("val"));

               viwe_Button.setEnabled(false);
               viwe_Button.setVisibility(View.INVISIBLE);

               break;
           }

           case 2:
           {
               bac.setBackgroundColor(Color.rgb(255, 239, 234));
               String tmp = "Test Type : F.B.S Test";
               test_Type.setText(tmp);

               tmp ="Add Date: " + record.get("addDate");
               add_Date.setText(tmp);

               tmp = "Add Time: " + record.get("addTime");
               add_Time.setText(tmp);

               value_.setText("F.B.S Percent: "+record.get("val"));

               viwe_Button.setEnabled(false);
               viwe_Button.setVisibility(View.INVISIBLE);

               break;
           }

           case 3:
           {
               bac.setBackgroundColor(Color.rgb(255, 221, 211));
               String tmp = "Test Type : Cumulative Test";
               test_Type.setText(tmp);

               tmp ="Add Date: " + record.get("addDate");
               add_Date.setText(tmp);

               tmp = "Add Time: " + record.get("addTime");
               add_Time.setText(tmp);

               value_.setText("Hb Alc Percent: "+record.get("val"));

               viwe_Button.setEnabled(false);
               viwe_Button.setVisibility(View.INVISIBLE);

               break;
           }

           case 4:
           {
               bac.setBackgroundColor(Color.rgb(253, 238, 255));
               String tmp = "Test Type : Hypertension Test";
               test_Type.setText(tmp);

               tmp ="Add Date: " + record.get("addDate");
               add_Date.setText(tmp);

               tmp = "Add Time: " + record.get("addTime");
               add_Time.setText(tmp);

               value_.setText("Hypertension Percent: "+record.get("val"));

               viwe_Button.setEnabled(false);
               viwe_Button.setVisibility(View.INVISIBLE);

               break;
           }

           case 5:
           {
               bac.setBackgroundColor(Color.rgb(212, 215, 187));
               String tmp = "Test Type : Cholesterol and Fats Test";
               test_Type.setText(tmp);

               tmp ="Add Date: " + record.get("addDate");
               add_Date.setText(tmp);

               tmp = "Add Time: " + record.get("addTime");
               add_Time.setText(tmp);
               value_.setVisibility(View.INVISIBLE);

               viwe_Button.setEnabled(true);
               viwe_Button.setVisibility(View.VISIBLE);

               viwe_Button.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       showCval(record);

                   }
               });

               break;
           }

           case 6:
           {
               bac.setBackgroundColor(Color.rgb(211, 215, 244));
               String tmp = "Test Type : Liver Test";
               test_Type.setText(tmp);

               tmp ="Add Date: " + record.get("addDate");
               add_Date.setText(tmp);

               tmp = "Add Time: " + record.get("addTime");
               add_Time.setText(tmp);
               value_.setVisibility(View.INVISIBLE);

               viwe_Button.setEnabled(true);
               viwe_Button.setVisibility(View.VISIBLE);

               viwe_Button.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       showLval(record);

                   }
               });

               break;
           }

           case 7:
           {
               bac.setBackgroundColor(Color.rgb(255, 251, 213));
               String tmp = "Test Type : Kidneys Test";
               test_Type.setText(tmp);

               tmp ="Add Date: " + record.get("addDate");
               add_Date.setText(tmp);

               tmp = "Add Time: " + record.get("addTime");
               add_Time.setText(tmp);
               value_.setVisibility(View.INVISIBLE);

               viwe_Button.setEnabled(true);
               viwe_Button.setVisibility(View.VISIBLE);

               viwe_Button.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       showKval(record);

                   }
               });

               break;
           }

           case 8:
           {
               bac.setBackgroundColor(Color.rgb(230, 246, 253));
               String tmp = "Test Type : Comprehensive Test";
               test_Type.setText(tmp);

               tmp ="Add Date: " + record.get("addDate");
               add_Date.setText(tmp);

               tmp = "Add Time: " + record.get("addTime");
               add_Time.setText(tmp);
               value_.setVisibility(View.INVISIBLE);

               viwe_Button.setEnabled(true);
               viwe_Button.setVisibility(View.VISIBLE);

               viwe_Button.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       showCoval(record);

                   }
               });

               break;
           }
       }

    }

    @Override
    public int getItemCount() {
        return recordsList.size();
    }

    void showKval(Map<String,Object>record)
    {
        List<String> V= new ArrayList<String>(Arrays.asList((String[]) record.get("val")));
        //String V [] = new String[]{};
        TextView textE [] = new TextView[3];
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.kidneys_val, null);

        View bac =view.findViewById(R.id.bac_);
        bac.setBackgroundColor(Color.rgb(255, 251, 213));

        AlertDialog Dialog = new AlertDialog.Builder(context)
                .setView(view)
                .create();

        ImageButton imgButton;
        imgButton =(ImageButton)view.findViewById(R.id.imageButton);
        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog.dismiss();
                return;
            }
        });

        textE[0] = view.findViewById(R.id.txv1); textE[0].setText("UricAcid Percent: "+V.get(0));
        textE[1] = view.findViewById(R.id.textView2_); textE[1].setText("Urea Percent: "+V.get(1));
        textE[2] = view.findViewById(R.id.textView3_); textE[2].setText("Creatinine Percent: "+V.get(2));

        Dialog.show();
    }

    void showLval(Map<String,Object>record)
    {
        List<String> V= new ArrayList<String>(Arrays.asList((String[]) record.get("val")));
        //String V [] = new String[]{};
        TextView textE [] = new TextView[4];
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.liver_val, null);

        View bac =view.findViewById(R.id.bac_);
        bac.setBackgroundColor(Color.rgb(211, 215, 244));

        AlertDialog Dialog = new AlertDialog.Builder(context)
                .setView(view)
                .create();

        ImageButton imgButton;
        imgButton =(ImageButton)view.findViewById(R.id.imageButton);
        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog.dismiss();
                return;
            }
        });

        textE[0] = view.findViewById(R.id.txv1); textE[0].setText("S.GOT Percent: "+V.get(0));
        textE[1] = view.findViewById(R.id.textView2_); textE[1].setText("S.GPT Percent: "+V.get(1));
        textE[2] = view.findViewById(R.id.textView3_); textE[2].setText("G.G.T Percent: "+V.get(2));
        textE[3] = view.findViewById(R.id.textView4_); textE[3].setText("Alk.Phosphatese Percent: "+V.get(3));

        Dialog.show();
    }

    void showCval(Map<String,Object>record)
    {
        List<String> V= new ArrayList<String>(Arrays.asList((String[]) record.get("val")));
        //String V [] = new String[]{};
        TextView textE [] = new TextView[4];
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.col_val, null);

        View bac =view.findViewById(R.id.bac_);
        bac.setBackgroundColor(Color.rgb(212, 215, 187));

        AlertDialog Dialog = new AlertDialog.Builder(context)
                .setView(view)
                .create();

        ImageButton imgButton;
        imgButton =(ImageButton)view.findViewById(R.id.imageButton);
        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog.dismiss();
                return;
            }
        });

        textE[0] = view.findViewById(R.id.txv1); textE[0].setText("Triglyceride Percent: "+V.get(0));
        textE[1] = view.findViewById(R.id.textView2_); textE[1].setText("LDL Cholesterol Percent: "+V.get(1));
        textE[2] = view.findViewById(R.id.textView3_); textE[2].setText("HDL Cholesterol Percent: "+V.get(2));
        textE[3] = view.findViewById(R.id.textView4_); textE[3].setText("Cholesterol Total Percent: "+V.get(3));

        Dialog.show();
    }

    void showCoval(Map<String,Object>record)
    {

        List<String> V= new ArrayList<String>(Arrays.asList((String[]) record.get("val")));
        //String V [] = new String[]{};
        TextView textE [] = new TextView[33];
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.comprehensive_val, null);

        AlertDialog Dialog = new AlertDialog.Builder(context)
                .setView(view)
                .create();

        View bac =view.findViewById(R.id.bac_);
        bac.setBackgroundColor(Color.rgb(230, 246, 253));


        ImageButton imgButton;
        imgButton =(ImageButton)view.findViewById(R.id.imageButton);
        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog.dismiss();
               return;
            }
        });

        ViewFlipper viewFlipper = view.findViewById(R.id.view_flipper);

        Button next = view.findViewById(R.id.next); next.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            viewFlipper.setInAnimation(context, android.R.anim.slide_in_left);
            viewFlipper.setOutAnimation(context, android.R.anim.slide_out_right);
            viewFlipper.showNext();
        }
    });

        Button prev = view.findViewById(R.id.previous); prev.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            viewFlipper.showPrevious();
        }
    });

        textE[0] = view.findViewById(R.id.txv1); textE[0].setText("F.B.S Percent: "+V.get(0));
        //
        textE[1] = view.findViewById(R.id.txv2); textE[1].setText("S.GOT Percent: "+V.get(1));
        textE[2] = view.findViewById(R.id.txv3); textE[2].setText("S.GPT Percent: "+V.get(2));
        textE[3] = view.findViewById(R.id.txv4); textE[3].setText("G.G.T Percent: "+V.get(3));
        textE[4] = view.findViewById(R.id.txv5); textE[4].setText("Alk.Phosphatese Percent: "+V.get(4));
        //
        textE[5] = view.findViewById(R.id.txv6); textE[5].setText("UricAcid Percent: "+V.get(5));
        textE[6] = view.findViewById(R.id.txv7); textE[6].setText("Urea Percent: "+V.get(6));
        textE[7] = view.findViewById(R.id.txv8); textE[7].setText("Creatinine Percent: "+V.get(7));
        //
        textE[8] = view.findViewById(R.id.txv9); textE[8].setText("Triglyceride Percent: "+V.get(8));
        textE[9] = view.findViewById(R.id.txv10); textE[9].setText("LDL Cholesterol Percent: "+V.get(9));
        textE[10] = view.findViewById(R.id.txv11); textE[10].setText("HDL Cholesterol Percent: "+V.get(10));
        textE[11] = view.findViewById(R.id.txv12); textE[11].setText("Cholesterol Total Percent: "+V.get(11));
        //
        textE[12] = view.findViewById(R.id.textView113_); textE[12].setText("Albumin Percent: "+V.get(12));
        textE[13] = view.findViewById(R.id.textView114_); textE[13].setText("Acp.Total Percent: "+V.get(13));
        textE[14] = view.findViewById(R.id.textView115_); textE[14].setText("Calcium Percent: "+V.get(14));
        textE[15] = view.findViewById(R.id.textView116_); textE[15].setText("Chloride Percent: "+V.get(15));
        textE[16] = view.findViewById(R.id.textView117_); textE[16].setText("S.Iron Percent: "+V.get(16));
        textE[17] = view.findViewById(R.id.textView118_); textE[17].setText("TIBC Percent: "+V.get(17));
        textE[18] = view.findViewById(R.id.textView119_); textE[18].setText("Acp.Prostatic Percent: "+V.get(18));
        textE[19] = view.findViewById(R.id.textView120_); textE[19].setText("Amylase Percent: "+V.get(19));
        textE[20] = view.findViewById(R.id.textView121_); textE[20].setText("Potassium Percent: "+V.get(20));
        textE[21] = view.findViewById(R.id.textView122_); textE[21].setText("Sodium Percent: "+V.get(21));
        textE[22] = view.findViewById(R.id.textView123_); textE[22].setText("2HPPS Percent: "+V.get(22));
        textE[23] = view.findViewById(R.id.textView124_); textE[23].setText("R.B.S  Percent: "+V.get(23));
        textE[24] = view.findViewById(R.id.textView125_); textE[24].setText("Bilirubin Total Percent: "+V.get(24));
        textE[25] = view.findViewById(R.id.textView126_); textE[25].setText("Bilirubin Direct  Percent: "+V.get(25));
        textE[26] = view.findViewById(R.id.textView127_); textE[26].setText("PSA Percent: "+V.get(26));
        textE[27] = view.findViewById(R.id.textView128_); textE[27].setText("Phosphours Percent: "+V.get(27));
        textE[28] = view.findViewById(R.id.textView129_); textE[28].setText("LDH Percent: "+V.get(28));
        textE[29] = view.findViewById(R.id.textView130_); textE[29].setText("CK-MB Percent: "+V.get(29));
        textE[30] = view.findViewById(R.id.textView131_); textE[30].setText("CPK Percent: "+V.get(30));
        textE[31] = view.findViewById(R.id.textView132_); textE[31].setText("T.Protein Percent: "+V.get(31));
        textE[32] = view.findViewById(R.id.textView133_); textE[32].setText("Magnesium Percent: "+V.get(32));

        Dialog.show();
    }
}
