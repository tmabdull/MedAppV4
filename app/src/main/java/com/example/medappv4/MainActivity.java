package com.example.medappv4;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medappv4.R;
import com.example.medappv4.models.Medicine;
import com.example.medappv4.adapters.MedicineAdapter;
import com.example.medappv4.database.DatabaseHelper;
import com.example.medappv4.utils.DialogHelper;
import com.example.medappv4.utils.TimeUtils;
import com.example.medappv4.constants.Constants;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MedicineAdapter adapter;
    private DatabaseHelper dbHelper = new DatabaseHelper();

    // This function sets up the initial state of the activity. This includes setting up the RecyclerView,
    // initializing Firestore, and fetching the medicine data.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize adapter with an empty list
        adapter = new MedicineAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // Fetch medicine data in real-time
        dbHelper.fetchMedicineData(new DatabaseHelper.MedicineDataCallback() {
            @Override
            public void onMedicineAdded(Medicine medicine) {
                adapter.addMedicine(medicine);
            }

            @Override
            public void onMedicineUpdated(Medicine medicine) {
                int position = adapter.findMedicineIndexById(medicine.getId()); // Assuming each medicine has a unique ID
                if (position != -1) {
                    adapter.updateMedicine(position, medicine);
                }
            }

            @Override
            public void onMedicineRemoved(Medicine medicine) {
                int position = adapter.findMedicineIndexById(medicine.getId());
                if (position != -1) {
                    adapter.removeMedicine(position);
                }
            }
        });

    }

    // Properly detaches the Firestore listener to prevent memory leaks when the activity is destroyed.
    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.detachListener();
    }

}
