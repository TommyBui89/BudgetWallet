package com.example.budgetapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import de.hdodenhof.circleimageview.CircleImageView;


import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Budget#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Budget extends Fragment {


    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String url, email, password, firstName, lastName, phone, budget, id, balance;
    PieChart pieChart;
    CircleImageView avatar;
    TextView name;
    Button nextMonthBTN, previousMonthBTN;
    TextView monthTextView, noDataTextView;

    Map<String, Float> categoryMap = new HashMap<>();

    Date date = new Date();
    SimpleDateFormat formatter = new SimpleDateFormat("MM-yyyy");
    String formattedDate = formatter.format(date);
    int currentMonth = Integer.parseInt(formattedDate.substring(0, 2));
    int currentYear = Integer.parseInt(formattedDate.substring(3, 7));

    int queryMonth = currentMonth;
    int queryYear = currentYear;
    String queryMonthString;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_budget, container, false);


        Bundle bundle = getArguments();
        if (bundle != null) {
            url = bundle.getString("url");
            firstName = bundle.getString("firstName");
            lastName = bundle.getString("lastName");
            password = bundle.getString("password");
            email = bundle.getString("email");
            budget = bundle.getString("budget");
            balance = bundle.getString("balance");
            phone = bundle.getString("phone");
            id = bundle.getString("id");
        }


        monthTextView = view.findViewById(R.id.CurrentMonth);
        previousMonthBTN = view.findViewById(R.id.PreviousMonth);
        nextMonthBTN = view.findViewById(R.id.NextMonth);
        noDataTextView = view.findViewById(R.id.noDataTextView);
        pieChart = view.findViewById(R.id.pieChart);

        // prepairing the month query
        queryMonthString = queryMonth + "-" + queryYear;
        monthTextView.setText(queryMonthString);



        getCategory(new CategoryCallback() {
            @Override
            public void onCategories(List<String> categories) {
                for (String category : categories) {
                    getTotal(queryMonthString, category, new TotalCallback() {
                        @Override
                        public void onTotal(float total) {
                            categoryMap.put(category, total);
                            if (categoryMap.size() == categories.size()) {
                                createPieChart(categoryMap);
                            }
                        }
                    });
                }
            }
        });

        // Button click even to change month query
        previousMonthBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //logic update month
                if (queryMonth == 1) {
                    queryMonth = 12;
                    queryYear--;
                } else {
                    queryMonth--;
                }
                queryMonthString = queryMonth + "-" + queryYear;
                monthTextView.setText(queryMonthString);
                categoryMap.clear();

                getCategory(new CategoryCallback() {
                    @Override
                    public void onCategories(List<String> categories) {
                        for (String category : categories) {
                            getTotal(queryMonthString, category, new TotalCallback() {
                                @Override
                                public void onTotal(float total) {
                                    categoryMap.put(category, total);
                                    if (categoryMap.size() == categories.size()) {
                                        createPieChart(categoryMap);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });

        nextMonthBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //logic update month
                if (queryMonth == 12) {
                    queryMonth = 1;
                    queryYear++;
                } else {
                    queryMonth++;
                }
                queryMonthString = queryMonth + "-" + queryYear;
                monthTextView.setText(queryMonthString);
                categoryMap.clear();
                getCategory(new CategoryCallback() {
                    @Override
                    public void onCategories(List<String> categories) {
                        for (String category : categories) {
                            getTotal(queryMonthString, category, new TotalCallback() {
                                @Override
                                public void onTotal(float total) {
                                    categoryMap.put(category, total);
                                    if (categoryMap.size() == categories.size()) {
                                        createPieChart(categoryMap);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });


        return view;
    }

    public void getTotal(String queryMonthString, String category, TotalCallback callback) {
        DocumentReference transactionsCollectionRef = db.collection("transactions").document(id);
        // Query the collection
        Query transactionsQuery = transactionsCollectionRef.collection(queryMonthString).whereEqualTo("category", category);
        transactionsQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot querySnapshot, FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore error", error.getMessage());
                    callback.onTotal(0); // Notify the callback with the default value in case of an error
                    return;
                }
                float total = 0;
                for (QueryDocumentSnapshot doc : querySnapshot) {
                    if (doc.get("amount") != null) {
                        total += Float.parseFloat(doc.get("amount").toString());
                    }
                }
                callback.onTotal(total); // Notify the callback with the calculated total value
            }
        });
    }

    public interface TotalCallback {
        void onTotal(float total);
    }

    private void getCategory(CategoryCallback callback) {
        DocumentReference categoryCollectionRef = db.collection("category").document(id);
        categoryCollectionRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        List<String> categories = (List<String>) documentSnapshot.get("category");
                        callback.onCategories(categories);
                    }
                } else {
                    Log.d("Firestore error", task.getException().getMessage());
                }
            }
        });
    }

    public interface CategoryCallback {
        void onCategories(List<String> categories);
    }

    private void createPieChart(Map<String, Float> categoryMap) {
        // Create a new PieDataSet and set its properties
        List<PieEntry> pieEntries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();
        List<LegendEntry> legendEntries = new ArrayList<>();
        float totalExpense = 0;


        for (Map.Entry<String, Float> entry : categoryMap.entrySet()) {
            String category = entry.getKey();
            Float value = Math.abs(entry.getValue());
            if (!category.equalsIgnoreCase("Income") && value > 0) {
                pieEntries.add(new PieEntry(value, category));
                // Add a corresponding color for each legend entry
                int color = ColorTemplate.COLORFUL_COLORS[colors.size() % ColorTemplate.COLORFUL_COLORS.length];
                colors.add(color);
                totalExpense += value;
            }
        }

        if (pieEntries.isEmpty()) {
            // Display the noDataTextView when there is no data
            noDataTextView.setText("No data for " + queryMonthString + "");
            noDataTextView.setVisibility(View.VISIBLE);
            pieChart.clear();
            pieChart.invalidate();
            return;
        } else {
            // Hide the noDataTextView when there is data
            noDataTextView.setText("$ "+ totalExpense);
            noDataTextView.setVisibility(View.VISIBLE);
        }


        PieDataSet dataSet = new PieDataSet(pieEntries, "Categories");
        dataSet.setColors(colors);
        dataSet.setValueTextSize(20f);


        PieData data = new PieData(dataSet);
        pieChart.setHoleRadius(20f); // Decrease the radius of the hole (center of the pie)
        pieChart.setTransparentCircleRadius(20f); // Decrease the radius of the transparent circle around the pie
        pieChart.getDescription().setEnabled(false); // Hide the chart description
        pieChart.setDrawEntryLabels(false); // Hide the labels on the pie slices


        for (int i = 0; i < pieEntries.size(); i++) {
            PieEntry entry = pieEntries.get(i);
            String category = entry.getLabel();
            float value = entry.getValue();
            if (!category.equalsIgnoreCase("Income") && value > 0.001) {
                LegendEntry legendEntry = new LegendEntry();
                legendEntry.label = category + " -  $" + value;
                legendEntry.formColor = colors.get(i); // Set the color for the legend entry
                legendEntries.add(legendEntry);
            }
        }

        Legend legend = pieChart.getLegend();
        legend.setCustom(legendEntries); // Set the legend entries manually
        legend.setFormSize(18f); // Increase the size of the color squares

        legend.setTextSize(15f); // Increase the size of the legend labels
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL); // Set the legend orientation to vertical
        legend.setWordWrapEnabled(true); // Enable word wrap for legend labels


        // Configure the pieChart
        pieChart.setData(data);
        pieChart.animateXY(1000, 1000);
        pieChart.invalidate();
    }

}
