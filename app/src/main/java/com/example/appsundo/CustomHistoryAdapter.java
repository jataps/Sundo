package com.example.appsundo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomHistoryAdapter extends RecyclerView.Adapter<CustomHistoryAdapter.MyViewHolder>{

    private final RecyclerViewInterface recyclerViewInterface;
    Context context;
    static ArrayList<History> list;

    public CustomHistoryAdapter(Context context, ArrayList<History> list, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.list = list;
        this.recyclerViewInterface = recyclerViewInterface;
    }


    @NonNull
    @Override
    public CustomHistoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_history_details, parent, false);

        return new CustomHistoryAdapter.MyViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomHistoryAdapter.MyViewHolder holder, int position) {

        History history = list.get(position);

        String date = history.getDate();
//        String driverUID = history.getDriverUID();
//        String studentUID = history.getStudentUID();

        String homePickupTime = history.getHomePickup().getPickupTime();
//       String homePickupLat = history.getHomePickup().getPickupLatitude();
//        String homePickupLong = history.getHomePickup().getPickupLongitude();
        String homeDropoffTime = history.getHomeDropOff().getDropoffTime();
//        String homeDropoffLat = history.getHomeDropOff().getDropoffLatitude();
//        String homeDropoffLong = history.getHomeDropOff().getDropoffLongitude();

        String schoolPickupTime = history.getSchoolPickup().getPickupTime();
//        String schoolPickupLat = history.getSchoolPickup().getPickupLatitude();
//        String schoolPickupLong = history.getSchoolPickup().getPickupLongitude();
        String schoolDropoffTime = history.getSchoolDropOff().getDropoffTime();
//        String schoolDropoffLat = history.getSchoolDropOff().getDropoffLatitude();
//        String schoolDropoffLong = history.getSchoolDropOff().getDropoffLongitude();

        holder.tvDate.setText(date);
        holder.schoolDriverPickup.setText(schoolPickupTime);
        holder.schoolDriverArrival.setText(schoolDropoffTime);
        holder.homeDriverPickup.setText(homePickupTime);
        holder.homeDriverArrival.setText(homeDropoffTime);

        holder.expandableLayout.setVisibility(View.GONE);
        holder.expandableLayout2.setVisibility(View.GONE);
        holder.containerToSchool.setVisibility(View.GONE);
        holder.containerToHome.setVisibility(View.GONE);

    }

    @Override
    public int getItemCount() {

        return list.size();

    }


    public static class MyViewHolder extends RecyclerView.ViewHolder{

        RelativeLayout expandableLayout, expandableLayout2;
        LinearLayout containerToSchool, containerToHome;

        ImageView arrowDate;
        ImageView arrowToSchool;
        ImageView arrowToHome;
        TextView tvDate;

        TextView schoolDriverLName;
        TextView schoolDriverFName;
        TextView schoolDriverCNumber;
        TextView schoolDriverPickup;
        TextView schoolDriverArrival;

        TextView homeDriverLName;
        TextView homeDriverFName;
        TextView homeDriverCNumber;
        TextView homeDriverPickup;
        TextView homeDriverArrival;

        Boolean isExpandableOn = false;
        Boolean isExpandable2On = false;
        Boolean isContainerToHomeOn = false;
        Boolean isContainerToSchoolOn = false;

        public MyViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);

            expandableLayout = itemView.findViewById(R.id.expandable_layout);
            expandableLayout2 = itemView.findViewById(R.id.expandable_layout2);

            containerToSchool = itemView.findViewById(R.id.containerToSchool);
            containerToHome = itemView.findViewById(R.id.containerToHome);

            arrowDate = itemView.findViewById(R.id.arrowDate);
            arrowToSchool = itemView.findViewById(R.id.arrowToSchool);
            arrowToHome = itemView.findViewById(R.id.arrowToHome);

            tvDate = itemView.findViewById(R.id.tvDate);

            schoolDriverLName = itemView.findViewById(R.id.schoolDriverLName);
            schoolDriverFName = itemView.findViewById(R.id.schoolDriverFName);
            schoolDriverCNumber = itemView.findViewById(R.id.schoolDriverCNumber);
            schoolDriverPickup = itemView.findViewById(R.id.schoolDriverPickup);
            schoolDriverArrival = itemView.findViewById(R.id.schoolDriverArrival);

            homeDriverLName = itemView.findViewById(R.id.homeDriverLName);
            homeDriverFName = itemView.findViewById(R.id.homeDriverFName);
            homeDriverCNumber = itemView.findViewById(R.id.homeDriverCNumber);
            homeDriverPickup = itemView.findViewById(R.id.homeDriverPickup);
            homeDriverArrival = itemView.findViewById(R.id.homeDriverArrival);

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

            arrowDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isExpandableOn && isExpandable2On){
                        //close expandables
                        expandableLayout.setVisibility(View.GONE);
                        expandableLayout2.setVisibility(View.GONE);
                        arrowDate.setImageResource(R.drawable.icon_arrow_down);
                    } else {
                        //open
                        expandableLayout.setVisibility(View.VISIBLE);
                        expandableLayout2.setVisibility(View.VISIBLE);
                        arrowDate.setImageResource(R.drawable.icon_arrow_up);
                    }
                    isExpandableOn = !isExpandableOn;
                    isExpandable2On = !isExpandable2On;

                }
            });

            arrowToHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isContainerToHomeOn) {
                        //close
                        containerToHome.setVisibility(View.GONE);
                        arrowToHome.setImageResource(R.drawable.icon_arrow_down);
                    } else {
                        //open
                        containerToHome.setVisibility(View.VISIBLE);
                        arrowToHome.setImageResource(R.drawable.icon_arrow_down);
                    }

                    isContainerToHomeOn = !isContainerToHomeOn;
                }
            });

            arrowToSchool.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(isContainerToSchoolOn) {
                        //close
                        containerToSchool.setVisibility(View.GONE);
                        arrowToSchool.setImageResource(R.drawable.icon_arrow_down);
                    } else {
                        //open
                        containerToSchool.setVisibility(View.VISIBLE);
                        arrowToSchool.setImageResource(R.drawable.icon_arrow_down);
                    }

                    isContainerToSchoolOn = !isContainerToSchoolOn;

                }
            });


        }

    }
}
