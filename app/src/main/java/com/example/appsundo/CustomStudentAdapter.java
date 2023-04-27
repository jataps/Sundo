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
    ArrayList<User> list;

    public CustomStudentAdapter(Context context, ArrayList<User> list, RecyclerViewInterface recyclerViewInterface) {
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

        User user = list.get(position);


        String firstName = user.getFirstName() ;
        String lastName = user.getLastName() + ", ";
        String streetAddress = user.getADDRESS().getStreetAddress();
        String barangay = user.getADDRESS().getBarangay().toUpperCase() + " |";
        String finalCity = user.getADDRESS().getCity().replace("CITY OF ","") + " |";
        String province = user.getADDRESS().getProvince();
        String accountCode = user.getAccountCode();

        //holder.accountCode.setText(accountCode);
        holder.textFN.setText(firstName);
        holder.textLN.setText(lastName);
        holder.textStAddress.setText(streetAddress);
        holder.textBrgy.setText(barangay);
        holder.textCity.setText(finalCity);
        holder.textProvince.setText(province);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{


        TextView textFN, textLN, textStAddress, textBrgy, textCity, textProvince, accountCode;

        public MyViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);

            textFN = itemView.findViewById(R.id.textFN);
            textLN = itemView.findViewById(R.id.textLN);
            textStAddress = itemView.findViewById(R.id.textStAddress);
            textBrgy = itemView.findViewById(R.id.textBrgy);
            textCity = itemView.findViewById(R.id.textCity);
            textProvince = itemView.findViewById(R.id.textProvince);
            //accountCode = itemView.findViewById(R.id.txtACode);
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
