package com.example.budgetapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class add extends Fragment {
    String url,firstName, lastName,email,phone,password;
    CircleImageView avatar;
    TextView name;
    public add() {
        // Required empty public constructor
    }
    public static add newInstance(String param1, String param2) {
        add fragment = new add();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        Bundle bundle = getArguments();
        if (bundle != null) {
            url = bundle.getString("url");
            firstName = bundle.getString("firstName");
            lastName = bundle.getString("lastName");
            password = bundle.getString("password");
            phone = bundle.getString("phone");
            email = bundle.getString("email");
        }


        return view;
    }
}