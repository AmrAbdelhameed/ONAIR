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
import com.example.amr.onair.models.Client;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ClientsAdapter extends RecyclerView.Adapter<ClientsAdapter.MyViewHolder> {

    private Context mContext;
    private List<Client> clientList;
    private ArrayList<Client> clientListFilter;

    public ClientsAdapter(Context mContext, List<Client> clientList) {
        this.mContext = mContext;
        this.clientList = clientList;
        this.clientListFilter = new ArrayList<Client>();
        this.clientListFilter.addAll(clientList);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_in_users, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final Client client = clientList.get(position);
        holder.name.setText(client.getName());
        char c = client.getPhone().charAt(2);
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
                b.putSerializable("sampleObject", clientList.get(position));
                b.putBoolean("staff", false);
                i.putExtras(b);
                mContext.startActivity(i);
            }
        });

        //in some cases, it will prevent unwanted situations
        holder.checkbox.setOnCheckedChangeListener(null);

        //if true, your checkbox will be selected, else unselected
        holder.checkbox.setChecked(client.isSelected());

        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                clientList.get(holder.getAdapterPosition()).setSelected(isChecked);
            }
        });

//        String img = client.getImagreURL();
//        if (!"".equals(img))
//            Picasso.with(mContext).load(img).into(holder.imageUpload);
    }

    @Override
    public int getItemCount() {
        if (clientList == null)
            return 0;
        else
            return clientList.size();
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        clientList.clear();
        if (charText.length() == 0) {
            clientList.addAll(clientListFilter);
        } else {
            for (Client wp : clientListFilter) {
                if (wp.getName().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    clientList.add(wp);
                }
            }
        }
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