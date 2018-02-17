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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.amr.onair.Others.MyDividerItemDecoration;
import com.example.amr.onair.Others.NetworkUtil;
import com.example.amr.onair.R;
import com.example.amr.onair.activities.SendMailGroupActivity;
import com.example.amr.onair.activities.SendMessageGroupActivity;
import com.example.amr.onair.adapters.StaffAdapter;
import com.example.amr.onair.models.Staff;
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
public class StaffFragment extends Fragment {

    DatabaseReference databaseReference;
    FirebaseDatabase database;
    List<Staff> staffList;
    List<Staff> staffListFilter;
    RecyclerView recycler_view;
    RecyclerView.LayoutManager mLayoutManager;
    StaffAdapter staffAdapter;
    ProgressDialog pdialog;
    EditText text_search;
    ArrayList<String> DepartmenttypeArray;
    String DepartmenttypeString;
    boolean checkVisit = false;
    Gson gson;
    String CacheDataStaff;

    public StaffFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_staff, container, false);

        setHasOptionsMenu(true);

        gson = new Gson();

        pdialog = new ProgressDialog(getActivity());
        pdialog.setIndeterminate(true);
        pdialog.setCancelable(false);
        pdialog.setMessage("Loading. Please wait...");
        pdialog.show();

        staffList = new ArrayList<>();
        staffListFilter = new ArrayList<>();

        text_search = view.findViewById(R.id.text_search);
        recycler_view = view.findViewById(R.id.recycler_view);

        if (!NetworkUtil.isConnected(getActivity())) {
            Toast.makeText(getActivity(), "Connection Failed", Toast.LENGTH_LONG).show();

            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("sharedPreferencesName", Context.MODE_PRIVATE);
            CacheDataStaff = sharedPreferences.getString("CacheDataStaff", "");

            Type type = new TypeToken<List<Staff>>() {
            }.getType();
            staffList = gson.fromJson(CacheDataStaff, type);

            staffAdapter = new StaffAdapter(getActivity(), staffList);
            mLayoutManager = new GridLayoutManager(getActivity(), 1);
            recycler_view.setLayoutManager(mLayoutManager);
            recycler_view.addItemDecoration(new MyDividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL, 0));
            recycler_view.setHasFixedSize(true);
            recycler_view.setItemAnimator(new DefaultItemAnimator());
            recycler_view.setAdapter(staffAdapter);

            pdialog.dismiss();
        } else {
            database = FirebaseDatabase.getInstance();
            databaseReference = database.getReference();
            databaseReference.child("users").child("staff").addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    staffList.clear();
                    Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                    for (DataSnapshot child : children) {
                        String staffID = child.getKey();
                        Staff staff = child.getValue(Staff.class);

                        staffList.add(staff);
                    }

                    staffAdapter = new StaffAdapter(getActivity(), staffList);
                    mLayoutManager = new GridLayoutManager(getActivity(), 1);
                    recycler_view.setLayoutManager(mLayoutManager);
                    recycler_view.addItemDecoration(new MyDividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL, 0));
                    recycler_view.setHasFixedSize(true);
                    recycler_view.setItemAnimator(new DefaultItemAnimator());
                    recycler_view.setAdapter(staffAdapter);

                    CacheDataStaff = gson.toJson(staffList);
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("sharedPreferencesName", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("CacheDataStaff", CacheDataStaff);
                    editor.apply();

                    pdialog.dismiss();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        DepartmenttypeArray = new ArrayList<>();
        DepartmenttypeArray.add("Filter by department");
        DepartmenttypeArray.add("Sales");
        DepartmenttypeArray.add("IT");
        DepartmenttypeArray.add("HR");
        DepartmenttypeArray.add("PR");

        final Spinner Departmenttype = view.findViewById(R.id.Departmenttype);
        ArrayAdapter AdapterDepartmenttype = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, DepartmenttypeArray);
        AdapterDepartmenttype.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Departmenttype.setAdapter(AdapterDepartmenttype);

        Departmenttype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

//                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorWhite));
                DepartmenttypeString = DepartmenttypeArray.get(position);

                if (DepartmenttypeString.equals("Filter by department")) {
                    DepartmenttypeString = "";
                }

                if (checkVisit) {
                    if (!"".equals(DepartmenttypeString))
                        ((StaffAdapter) staffAdapter).filterDepartment(DepartmenttypeString);
                    else
                        ((StaffAdapter) staffAdapter).RetrieveArray();
                }

                checkVisit = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        text_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                String text = text_search.getText().toString().toLowerCase(Locale.getDefault());
                ((StaffAdapter) staffAdapter).filter(text);
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
                staffListFilter.clear();
                for (Staff staff : staffList) {
                    if (staff.isSelected()) {
                        staffListFilter.add(staff);
                    }
                }
                if (staffListFilter.size() > 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("What do you want ?");
                    builder.setItems(new CharSequence[]
                                    {"Create Group ChatIndividual", "Send with email", "Send with SMS"},
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    String staffListFilterString = gson.toJson(staffListFilter);
                                    switch (which) {
                                        case 0:
                                            break;
                                        case 1:
                                            Intent i2 = new Intent(getActivity(), SendMailGroupActivity.class);
                                            Bundle b2 = new Bundle();
                                            b2.putString("ListFilterString", staffListFilterString);
                                            b2.putBoolean("staff", true);
                                            i2.putExtras(b2);
                                            startActivity(i2);
                                            break;
                                        case 2:
                                            Intent i3 = new Intent(getActivity(), SendMessageGroupActivity.class);
                                            Bundle b3 = new Bundle();
                                            b3.putString("ListFilterString", staffListFilterString);
                                            b3.putBoolean("staff", true);
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
