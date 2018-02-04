package com.example.amr.onair.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import com.example.amr.onair.MyDividerItemDecoration;
import com.example.amr.onair.R;
import com.example.amr.onair.adapters.ClientsAdapter;
import com.example.amr.onair.models.Client;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    List<Client> clientList;
    ClientsAdapter clientsAdapter;
    ProgressDialog pdialog;
    EditText text_search;

    public ClientsFragment() {
        // Required empty public constructor
    }

    public static boolean hasInternetConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetwork != null && wifiNetwork.isConnected()) {
            return true;
        }
        NetworkInfo mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileNetwork != null && mobileNetwork.isConnected()) {
            return true;
        }
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            return true;
        }
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_clients, container, false);

        setHasOptionsMenu(true);

        pdialog = new ProgressDialog(getActivity());
        pdialog.setIndeterminate(true);
        pdialog.setCancelable(false);
        pdialog.setMessage("Loading. Please wait...");
        pdialog.show();

        text_search = view.findViewById(R.id.text_search);
        recycler_view = view.findViewById(R.id.recycler_view);

        if (!hasInternetConnection(getActivity())) {
            Toast.makeText(getActivity(), "Connection Failed", Toast.LENGTH_LONG).show();
            pdialog.dismiss();
        } else {
            clientList = new ArrayList<>();
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
                StringBuilder stringBuilder = new StringBuilder();
                for (Client client : clientList) {
                    if (client.isSelected()) {
                        if (stringBuilder.length() > 0)
                            stringBuilder.append(", ");
                        stringBuilder.append(client.getName());
                    }
                }
                if (stringBuilder.length() > 0)
                    Toast.makeText(getActivity(), stringBuilder.toString(), Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
