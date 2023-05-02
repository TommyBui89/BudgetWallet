package com.example.budgetapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Home#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Home extends Fragment {



    String url,firstName, lastName,email,phone,password;
    CircleImageView avatar;
    TextView name;
    public Home() {
    }

    public static Home newInstance() {
        Home fragment = new Home();
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

        avatar=view.findViewById(R.id.avt);
        name=view.findViewById(R.id.UsernameText);

        if(url!=null) {
            Picasso.get().load(url).into(avatar);
        }
        name.setText(firstName+' '+lastName);

        return view;
    }
}