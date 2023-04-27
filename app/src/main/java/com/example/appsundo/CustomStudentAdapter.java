package com.example.appsundo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class CustomStudentAdapter extends RecyclerView.Adapter<CustomStudentAdapter.MyViewHolder> {

    private final RecyclerViewInterface recyclerViewInterface;

    Context context;
    static ArrayList<User> list;

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
        String barangay = user.getADDRESS().getBarangay().toUpperCase();
        String finalCity = user.getADDRESS().getCity().replace("CITY OF ","");
        String province = user.getADDRESS().getProvince();
        String finalAddress = barangay + " | " + finalCity + " | " + province;
        String accountCode = user.getAccountCode();

        //holder.accountCode.setText(accountCode);
        holder.textFN.setText(firstName);
        holder.textLN.setText(lastName);
        holder.textStAddress.setText(streetAddress);
        holder.textAddress.setText(finalAddress);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView textFN, textLN, textStAddress, textAddress, accountCode;

        MaterialButton btnAdd;

        public MyViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);

            btnAdd = itemView.findViewById(R.id.btnAddStudent);
            textFN = itemView.findViewById(R.id.textFN);
            textLN = itemView.findViewById(R.id.textLN);
            textStAddress = itemView.findViewById(R.id.textStAddress);
            textAddress = itemView.findViewById(R.id.textAddress);
            //accountCode = itemView.findViewById(R.id.txtACode);
            //textMail = itemView.findViewById(R.id.textMail);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(recyclerViewInterface != null) {
                        int pos = getAdapterPosition();

                        if (pos != RecyclerView.NO_POSITION) {
                            recyclerViewInterface.onItemClick(pos);
                        }

                    }

                }
            });


        }


    }

}
