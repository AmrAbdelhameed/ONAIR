package com.example.amr.onair.fragments;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.amr.onair.R;
import com.example.amr.onair.models.Client;
import com.example.amr.onair.models.Staff;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    ConstraintLayout Cons, Cons1;
    de.hdodenhof.circleimageview.CircleImageView image;
    TextView name, txtPhone, txtEmail, txtNationalID, txtDate, department;
    String _id, _name, _image, _email, _nationalID, _phone, _hireDate = "", _department = "";
    boolean _staff;
    Staff staff;
    Client client;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        Cons = view.findViewById(R.id.Cons);
        Cons1 = view.findViewById(R.id.Cons1);
        image = view.findViewById(R.id.image);
        name = view.findViewById(R.id.name);
        txtPhone = view.findViewById(R.id.txtPhone);
        txtEmail = view.findViewById(R.id.txtEmail);
        txtNationalID = view.findViewById(R.id.txtNationalID);
        txtDate = view.findViewById(R.id.txtDate);
        department = view.findViewById(R.id.department);

        Bundle sentBundle = getArguments();
        assert sentBundle != null;
        _staff = sentBundle.getBoolean("staff");

        if (_staff) {
            staff = (Staff) sentBundle.getSerializable("sampleObject");
            assert staff != null;
            _id = staff.getId();
            _image = staff.getImagreURL();
            _name = staff.getName();
            _email = staff.getEmail();
            _nationalID = staff.getNationalID();
            _phone = staff.getPhone();
            _hireDate = staff.getHiringDate();
            _department = staff.getDepartment();

            Cons.setVisibility(View.VISIBLE);
            Cons1.setVisibility(View.VISIBLE);
            txtDate.setText("Date of Hiring : " + _hireDate);
            department.setText("Department of " + _department);
        } else {
            client = (Client) sentBundle.getSerializable("sampleObject");
            assert client != null;
            _id = client.getId();
            _image = client.getImagreURL();
            _name = client.getName();
            _email = client.getEmail();
            _nationalID = client.getNationalID();
            _phone = client.getPhone();
        }

        if (!"".equals(_image))
            Picasso.with(getActivity()).load(_image).placeholder(R.drawable.ic_account_circle_black_48dp).into(image);

//        setTitle(_name);
        name.setText(_name);
        txtPhone.setText(_phone);
        txtEmail.setText(_email);
        txtNationalID.setText("National ID : " + _nationalID);

        return view;
    }
}
