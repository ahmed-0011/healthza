package com.example.healthza.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthza.R;
import com.example.healthza.Toasty;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

public class DoctorAppointmentAdapter
        extends RecyclerView.Adapter<DoctorAppointmentAdapter.ViewHolder>
{

    Map<String, Object> appointment;
    List<Map<String, Object>> appointments;
    private Context context;
    private DoctorAppointmentAdapter.OnRecordItemClickListener onRecordItemClickListener;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;

    public DoctorAppointmentAdapter(Context context, List<Map<String, Object>> appointments , DoctorAppointmentAdapter.OnRecordItemClickListener onRecordItemClickListener)
    {

        this.context = context;
        this.appointments = appointments;
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

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView type, dateD,dateY, name, id;
        TextInputLayout des;
        EditText desE;
        private Button removeButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            type = itemView.findViewById(R.id.type);
            dateD = itemView.findViewById(R.id.month);
            dateY = itemView.findViewById(R.id.year);
            name = itemView.findViewById(R.id.patientName);
            id = itemView.findViewById(R.id.patientId);
            removeButton = itemView.findViewById(R.id.removeButton);
            des = itemView.findViewById(R.id.descriptionI);
            des.setHint("Description");
            desE = itemView.findViewById(R.id.descriptionE);
            desE.setEnabled(false);

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
    public DoctorAppointmentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.doctor_appointment_item, parent, false);

        DoctorAppointmentAdapter.ViewHolder viewHolder = new DoctorAppointmentAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        Map<String, Object> record = appointments.get(position);

        TextView name = holder.name;
        TextView id = holder.id;
        TextView type = holder.type;
        TextView dateE = holder.dateD;
        TextView dateY = holder.dateY;
        EditText description = holder.desE;
        Button remove_Button = holder.removeButton;

        remove_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // System.out.println("GGG : "+holder.getAdapterPosition());
                // System.out.println("GGG : "+holder.getLayoutPosition());
                String arg [] = (String[]) record.get("del");

                DocumentReference Refs = db.collection("doctors")
                        .document(arg[0])
                        .collection("appointments").document(arg[2]);
                Refs.delete().addOnCompleteListener(task -> {
                    if(task.isSuccessful())
                    {

                        DocumentReference refs = db.collection("patients")
                                .document(arg[1])
                                .collection("appointments").document(arg[2]);

                        refs.delete().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                appointments.remove(holder.getLayoutPosition());
                                notifyDataSetChanged();
                                notifyItemRemoved(holder.getAdapterPosition());

                                Toasty.showText(context,
                                        ("Appointment: " + record.get("type") +" : " +record.get("date")
                                                + "\n\nPatient Name: "+record.get("patientName")
                                                + "\n\nPatient ID: "+record.get("patientId")
                                                + "\n\nhas beed deleted successfully.")
                                        ,Toasty.SUCCESS
                                        , Toast.LENGTH_LONG);
                            }
                        });

                    }
                    else
                        Toasty.showText(context, "Error", Toasty.ERROR,Toast.LENGTH_LONG);
                });
            }
        });

        id.setText(id.getText().toString()+record.get("patientId"));
        name.setText(name.getText().toString()+record.get("patientName"));
        type.setText(type.getText().toString()+record.get("type"));

        String [] date = DateFormat(record.get("date").toString());
        dateE.setText(date[0]);
        dateY.setText(date[1]);
        description.setText(description.getText().toString()+record.get("description"));

    }

    String [] DateFormat(String Date)
    {
        String d="",y="",m="";

        y = Date.substring(0,Date.indexOf("-"));
        m = Date.substring(Date.indexOf("-")+1,Date.lastIndexOf("-"));
        d = Date.substring(Date.lastIndexOf("-")+1);

        if(d.length()==1) d="0"+d;
        if(m.length()==1) m="0"+m;

        switch(m)
        {
            case "01":
            {
                m="Jan";
                break;
            }

            case "02":
            {
                m="Feb";
                break;
            }

            case "03":
            {
                m="Mar";
                break;
            }

            case "04":
            {
                m="Apr";
                break;
            }

            case "05":
            {
                m="May";
                break;
            }

            case "06":
            {
                m="Jun";
                break;
            }

            case "07":
            {
                m="Jul";
                break;
            }

            case "08":
            {
                m="Aug";
                break;
            }

            case "09":
            {
                m="Sep";
                break;
            }

            case "10":
            {
                m="Oct";
                break;
            }

            case "11":
            {
                m="Nov";
                break;
            }

            case "12":
            {
                m="Dec";
                break;
            }

            default:
            {
                m = "error";
                break;
            }
        }
          return new String[]{(d+" "+m),y};
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }
}
