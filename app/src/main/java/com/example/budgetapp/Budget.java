package com.example.budgetapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Budget#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Budget extends Fragment {

    String url,firstName, lastName,email,phone,password;
    CircleImageView avatar;
    TextView name;
    public Budget() {
        // Required empty public constructor
    }

        public static Budget newInstance() {
        Budget fragment = new Budget();
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
        return inflater.inflate(R.layout.fragment_budget, container, false);
    }
}