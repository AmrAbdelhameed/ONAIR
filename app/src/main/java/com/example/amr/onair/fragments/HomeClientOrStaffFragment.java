package com.example.amr.onair.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.amr.onair.Others.NetworkUtil;
import com.example.amr.onair.R;
import com.example.amr.onair.adapters.ChatAdapter;
import com.example.amr.onair.models.ChatIndividual;
import com.example.amr.onair.models.Client;
import com.example.amr.onair.models.Staff;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeClientOrStaffFragment extends Fragment {

    EditText textMessage;
    ImageView ic_message;
    boolean _staff;
    Staff staff;
    Client client;
    String _id, _Email, current_id = "knHPyfXcMHX3s3mGtW9s1aLeNYy1", current_email = "admin@onair.com";
    FirebaseAuth firebaseAuth;
    DatabaseReference mDatabase;
    DatabaseReference databaseReference;
    FirebaseDatabase database;
    RecyclerView recycler_view;
    RecyclerView.LayoutManager mLayoutManager;
    List<ChatIndividual> chatIndividuals;
    ChatAdapter chatAdapter;
    ProgressDialog pdialog;
    Gson gson;
    String CacheDataChat;

    public HomeClientOrStaffFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home_client_or_staff, container, false);

        textMessage = v.findViewById(R.id.textMessage);
        ic_message = v.findViewById(R.id.ic_message);
        recycler_view = v.findViewById(R.id.recycler_view);

        chatIndividuals = new ArrayList<>();
        gson = new Gson();

        pdialog = new ProgressDialog(getActivity());
        pdialog.setIndeterminate(true);
        pdialog.setCancelable(false);
        pdialog.setMessage("Loading. Please wait...");
        pdialog.show();

        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        Bundle sentBundle = getArguments();
        assert sentBundle != null;
        _staff = sentBundle.getBoolean("staff");

        if (_staff) {
            staff = (Staff) sentBundle.getSerializable("sampleObject");
            assert staff != null;
            _id = staff.getId();
            _Email = staff.getEmail();
        } else {
            client = (Client) sentBundle.getSerializable("sampleObject");
            assert client != null;
            _id = client.getId();
            _Email = client.getEmail();
        }

        ic_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!NetworkUtil.isConnected(getActivity()))
                    Toast.makeText(getActivity(), "Connection Failed", Toast.LENGTH_SHORT).show();
                else {
                    if (!textMessage.getText().toString().isEmpty()) {
                        String Message_id = mDatabase.push().getKey();
                        ChatIndividual chatIndividual = new ChatIndividual();
                        chatIndividual.setMessage_id(Message_id);
                        chatIndividual.setSender(_Email);
                        chatIndividual.setSenderUid(_id);
                        chatIndividual.setReceiver(current_email);
                        chatIndividual.setReceiverUid(current_id);
                        chatIndividual.setMessage(textMessage.getText().toString());
                        mDatabase.child("chat").child("individual").child(current_id + "_" + _id).child(Message_id).setValue(chatIndividual);
                        textMessage.setText("");
                    }
                }
            }
        });

        if (!NetworkUtil.isConnected(getActivity())) {
            Toast.makeText(getActivity(), "Connection Failed", Toast.LENGTH_LONG).show();

            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("sharedPreferencesName", Context.MODE_PRIVATE);
            CacheDataChat = sharedPreferences.getString("CacheDataChat", "");

            Type type = new TypeToken<List<ChatIndividual>>() {
            }.getType();
            chatIndividuals = gson.fromJson(CacheDataChat, type);

            chatAdapter = new ChatAdapter(getActivity(), chatIndividuals, _id);
            mLayoutManager = new GridLayoutManager(getActivity(), 1);
            mLayoutManager.scrollToPosition(chatIndividuals.size() - 1);
            recycler_view.setLayoutManager(mLayoutManager);
//                recycler_view.addItemDecoration(new MyDividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL, 0));
            recycler_view.setHasFixedSize(true);
            recycler_view.setItemAnimator(new DefaultItemAnimator());
            recycler_view.setAdapter(chatAdapter);

            pdialog.dismiss();
        } else {
            database = FirebaseDatabase.getInstance();
            databaseReference = database.getReference();
            databaseReference.child("chat").child("individual").child(current_id + "_" + _id).addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    chatIndividuals.clear();
                    Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                    for (DataSnapshot child : children) {
                        ChatIndividual chatIndividual = child.getValue(ChatIndividual.class);
                        chatIndividuals.add(chatIndividual);
                    }

                    chatAdapter = new ChatAdapter(getActivity(), chatIndividuals, _id);
                    mLayoutManager = new GridLayoutManager(getActivity(), 1);
                    mLayoutManager.scrollToPosition(chatIndividuals.size() - 1);
                    recycler_view.setLayoutManager(mLayoutManager);
//                recycler_view.addItemDecoration(new MyDividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL, 0));
                    recycler_view.setHasFixedSize(true);
                    recycler_view.setItemAnimator(new DefaultItemAnimator());
                    recycler_view.setAdapter(chatAdapter);

                    CacheDataChat = gson.toJson(chatIndividuals);
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("sharedPreferencesName", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("CacheDataChat", CacheDataChat);
                    editor.apply();

                    pdialog.dismiss();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        return v;
    }
}
