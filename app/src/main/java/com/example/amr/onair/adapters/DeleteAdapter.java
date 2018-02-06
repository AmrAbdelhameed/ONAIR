package com.example.amr.onair.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.amr.onair.R;

import java.util.List;

public class DeleteAdapter extends RecyclerView.Adapter<DeleteAdapter.MyViewHolder> {

    private Context mContext;
    private List<String> stringList;

    public DeleteAdapter(Context mContext, List<String> stringList) {
        this.mContext = mContext;
        this.stringList = stringList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_delete, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        holder.name.setText(stringList.get(position));

        holder.img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stringList.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        if (stringList == null)
            return 0;
        else
            return stringList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageView img_delete;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            img_delete = view.findViewById(R.id.img_delete);
        }
    }
}