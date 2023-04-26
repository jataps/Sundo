package com.example.appsundo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomStudentAdapter extends RecyclerView.Adapter<CustomStudentAdapter.MyViewHolder> {

    private final RecyclerViewInterface recyclerViewInterface;

    Context context;
    ArrayList<Student> list;

    public CustomStudentAdapter(Context context, ArrayList<Student> list, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.list = list;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_student_list, parent, false);

        return new MyViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Student student = list.get(position);

        String fullName = student.getFirstName() + " " + student.getLastName();
        String barangayAtCity = student.getBarangay() + ", " + student.getCity();


        holder.textFN.setText(fullName);
        holder.textStAddress.setText(student.getstreetAddress());
        holder.textBrgyCity.setText(barangayAtCity);
        holder.textProvince.setText(student.getProvince());


        //holder.textLN.setText(student.getLastName());
        //holder.textMail.setText(student.getCompleteAdd());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView textFN, textLN, textStAddress, textBrgyCity, textCity, textProvince;

        public MyViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);

            textFN = itemView.findViewById(R.id.textFN);
            textLN = itemView.findViewById(R.id.textLN);
            textStAddress = itemView.findViewById(R.id.textStAddress);
            textBrgyCity = itemView.findViewById(R.id.textBrgyCity);
            textCity = itemView.findViewById(R.id.textCity);
            textProvince = itemView.findViewById(R.id.textProvince);
            //textMail = itemView.findViewById(R.id.textMail);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (recyclerViewInterface != null) {
                        int pos = getAdapterPosition();

                        if(pos != RecyclerView.NO_POSITION) {
                            recyclerViewInterface.onItemClick(pos);
                        }
                    }
                }
            });

        }
    }

}
