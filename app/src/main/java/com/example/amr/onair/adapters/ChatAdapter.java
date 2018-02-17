package com.example.amr.onair.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.amr.onair.R;
import com.example.amr.onair.models.ChatIndividual;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {

    private static final int VIEW_TYPE_ME = 1;
    private static final int VIEW_TYPE_OTHER = 2;
    private Context mContext;
    private List<ChatIndividual> chatIndividuals;
    private String CurrentID;

    public ChatAdapter(Context mContext, List<ChatIndividual> chatIndividuals, String CurrentID) {
        this.mContext = mContext;
        this.chatIndividuals = chatIndividuals;
        this.CurrentID = CurrentID;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == VIEW_TYPE_ME) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.rc_item_message_user, parent, false);
            return new MyViewHolder(itemView);
        } else {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.rc_item_message_friend, parent, false);
            return new MyViewHolder(itemView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (TextUtils.equals(chatIndividuals.get(position).getSenderUid(), CurrentID)) {
            return VIEW_TYPE_ME;
        } else {
            return VIEW_TYPE_OTHER;
        }
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        ChatIndividual chatIndividual = chatIndividuals.get(position);
        holder.textContentUser.setText(chatIndividual.getMessage());
    }

    @Override
    public int getItemCount() {
        if (chatIndividuals == null)
            return 0;
        else
            return chatIndividuals.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textContentUser;

        public MyViewHolder(View view) {
            super(view);
            textContentUser = view.findViewById(R.id.textContentUser);
        }
    }
}