package com.example.amr.onair.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.amr.onair.Others.MyDividerItemDecoration;
import com.example.amr.onair.Others.NetworkUtil;
import com.example.amr.onair.R;
import com.example.amr.onair.activities.SendMailGroupActivity;
import com.example.amr.onair.activities.SendMessageGroupActivity;
import com.example.amr.onair.adapters.ClientsAdapter;
import com.example.amr.onair.models.Client;
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
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class ClientsFragment extends Fragment {

    DatabaseReference databaseReference;
    FirebaseDatabase database;
    RecyclerView recycler_view;
    RecyclerView.LayoutManager mLayoutManager;
    List<Client> clientList, clientListFilter;
    ClientsAdapter clientsAdapter;
    ProgressDialog pdialog;
    EditText text_search;
    Gson gson;
    String CacheDataClient;

    public ClientsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_clients, container, false);

        setHasOptionsMenu(true);

        clientList = new ArrayList<>();
        clientListFilter = new ArrayList<>();
        gson = new Gson();

        pdialog = new ProgressDialog(getActivity());
        pdialog.setIndeterminate(true);
        pdialog.setCancelable(false);
        pdialog.setMessage("Loading. Please wait...");
        pdialog.show();

        text_search = view.findViewById(R.id.text_search);
        recycler_view = view.findViewById(R.id.recycler_view);

        if (!NetworkUtil.isConnected(getActivity())) {
            Toast.makeText(getActivity(), "Connection Failed", Toast.LENGTH_LONG).show();

            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("sharedPreferencesName", Context.MODE_PRIVATE);
            CacheDataClient = sharedPreferences.getString("CacheDataClient", "");

            Type type = new TypeToken<List<Client>>() {
            }.getType();
            clientList = gson.fromJson(CacheDataClient, type);

            clientsAdapter = new ClientsAdapter(getActivity(), clientList);
            mLayoutManager = new GridLayoutManager(getActivity(), 1);
            recycler_view.setLayoutManager(mLayoutManager);
            recycler_view.addItemDecoration(new MyDividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL, 0));
            recycler_view.setHasFixedSize(true);
            recycler_view.setItemAnimator(new DefaultItemAnimator());
            recycler_view.setAdapter(clientsAdapter);

            pdialog.dismiss();
        } else {
            database = FirebaseDatabase.getInstance();
            databaseReference = database.getReference();
            databaseReference.child("users").child("clients").addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    clientList.clear();
                    Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                    for (DataSnapshot child : children) {
                        String clientID = child.getKey();
                        Client client = child.getValue(Client.class);

                        clientList.add(client);
                    }

                    clientsAdapter = new ClientsAdapter(getActivity(), clientList);
                    mLayoutManager = new GridLayoutManager(getActivity(), 1);
                    recycler_view.setLayoutManager(mLayoutManager);
                    recycler_view.addItemDecoration(new MyDividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL, 0));
                    recycler_view.setHasFixedSize(true);
                    recycler_view.setItemAnimator(new DefaultItemAnimator());
                    recycler_view.setAdapter(clientsAdapter);

                    CacheDataClient = gson.toJson(clientList);
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("sharedPreferencesName", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("CacheDataClient", CacheDataClient);
                    editor.apply();

                    pdialog.dismiss();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        text_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                String text = text_search.getText().toString().toLowerCase(Locale.getDefault());
                ((ClientsAdapter) clientsAdapter).filter(text);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                clientListFilter.clear();
                for (Client client : clientList) {
                    if (client.isSelected()) {
                        clientListFilter.add(client);
                    }
                }
                if (clientListFilter.size() > 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("What do you want ?");
                    builder.setItems(new CharSequence[]
                                    {"Create Group ChatIndividual", "Send with email", "Send with SMS"},
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    String staffListFilterString = gson.toJson(clientListFilter);
                                    switch (which) {
                                        case 0:
                                            break;
                                        case 1:
                                            Intent i2 = new Intent(getActivity(), SendMailGroupActivity.class);
                                            Bundle b2 = new Bundle();
                                            b2.putString("ListFilterString", staffListFilterString);
                                            b2.putBoolean("staff", false);
                                            i2.putExtras(b2);
                                            startActivity(i2);
                                            break;
                                        case 2:
                                            Intent i3 = new Intent(getActivity(), SendMessageGroupActivity.class);
                                            Bundle b3 = new Bundle();
                                            b3.putString("ListFilterString", staffListFilterString);
                                            b3.putBoolean("staff", false);
                                            i3.putExtras(b3);
                                            startActivity(i3);
                                            break;
                                    }
                                }
                            });
                    builder.create().show();
                } else
                    Toast.makeText(getActivity(), "Add at least two people to create group", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
