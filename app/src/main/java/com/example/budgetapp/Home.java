package com.example.budgetapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


public class Home extends Fragment {


    String url, firstName, lastName, email, phone, password;
    CircleImageView avatar;
    TextView name, date, dayLeft;

    public Home() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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

        avatar = view.findViewById(R.id.avt);
        name = view.findViewById(R.id.UsernameText);
        date = view.findViewById(R.id.Greeting);
        dayLeft = view.findViewById(R.id.DaysLeft);

        if (url != null) {
            Picasso.get().load(url).into(avatar);
        }
        name.setText(firstName + ' ' + lastName);


        // Get the current date
        Calendar calendar = Calendar.getInstance();
        Date time = calendar.getTime();

        // Set the desired date format
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE", Locale.getDefault());

        // Format the current date to get the day of the week
        String dayOfWeek = dateFormat.format(time);

        int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        date.setText("Happy " + dayOfWeek + "!");
        dayLeft.setText("There are " + (maxDays - currentDay) + " days left in this month");


        return view;
    }
}
