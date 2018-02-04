package com.example.amr.onair.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.amr.onair.R;
import com.example.amr.onair.activities.ProfileActivity;
import com.example.amr.onair.models.Staff;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StaffAdapter extends RecyclerView.Adapter<StaffAdapter.MyViewHolder> {

    private Context mContext;
    private List<Staff> staffList;
    private ArrayList<Staff> staffListFiltration;
    private ArrayList<Staff> staffListRetrieve;

    public StaffAdapter(Context mContext, List<Staff> staffList) {
        this.mContext = mContext;
        this.staffList = staffList;
        this.staffListRetrieve = new ArrayList<Staff>();
        this.staffListRetrieve.addAll(staffList);
        this.staffListFiltration = new ArrayList<Staff>();
        this.staffListFiltration.addAll(staffList);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_in_users, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final Staff staff = staffList.get(position);
        holder.name.setText(staff.getName());
        char c = staff.getPhone().charAt(2);
        if (c == '0') {
            holder.company.setText("Vodafone");
            holder.company.setTextColor(mContext.getResources().getColor(R.color.colorRed));
        } else if (c == '1') {
            holder.company.setText("Etisalat");
            holder.company.setTextColor(mContext.getResources().getColor(R.color.colorGreen));
        } else {
            holder.company.setText("Orange");
            holder.company.setTextColor(mContext.getResources().getColor(R.color.colorOrange));
        }

        holder.Lin1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext, ProfileActivity.class);
                Bundle b = new Bundle();
                b.putSerializable("sampleObject", staffList.get(position));
                b.putBoolean("staff", true);
                i.putExtras(b);
                mContext.startActivity(i);
            }
        });

        //in some cases, it will prevent unwanted situations
        holder.checkbox.setOnCheckedChangeListener(null);

        //if true, your checkbox will be selected, else unselected
        holder.checkbox.setChecked(staff.isSelected());

        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                staffList.get(holder.getAdapterPosition()).setSelected(isChecked);
            }
        });
//        String img = staff.getImagreURL();
//        if (!"".equals(img))
//            Picasso.with(mContext).load(img).into(holder.imageUpload);
    }

    @Override
    public int getItemCount() {
        if (staffList == null)
            return 0;
        else
            return staffList.size();
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        staffList.clear();
        if (charText.length() == 0) {
            staffList.addAll(staffListFiltration);
        } else {
            for (Staff wp : staffListFiltration) {
                if (wp.getName().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    staffList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void filterDepartment(String s) {
        staffList.clear();
        for (Staff wp : staffListRetrieve) {
            if (wp.getDepartment().equals(s)) {
                staffList.add(wp);
            }
        }
        staffListFiltration.clear();
        staffListFiltration.addAll(staffList);
        notifyDataSetChanged();
    }

    public void RetrieveArray() {
        staffList.clear();
        staffListFiltration.clear();
        staffList.addAll(staffListRetrieve);
        staffListFiltration.addAll(staffListRetrieve);
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, company;
        public CheckBox checkbox;
        public LinearLayout Lin1;
//        public de.hdodenhof.circleimageview.CircleImageView imageUpload;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            company = view.findViewById(R.id.company);
            checkbox = view.findViewById(R.id.checkbox);
            Lin1 = view.findViewById(R.id.Lin1);
//            imageUpload = view.findViewById(R.id.imageUpload);
        }
    }
}