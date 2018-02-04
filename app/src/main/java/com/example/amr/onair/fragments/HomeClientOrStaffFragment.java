package com.example.amr.onair.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.amr.onair.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeClientOrStaffFragment extends Fragment {


    public HomeClientOrStaffFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_client_or_staff, container, false);
    }

}
