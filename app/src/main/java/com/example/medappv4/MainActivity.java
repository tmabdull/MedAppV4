package com.example.medappv4;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MedicineAdapter medicineAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fetchMedicineData();
    }

    private void fetchMedicineData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("medicines").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Medicine> medicineList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Medicine medicine = document.toObject(Medicine.class);
                    medicineList.add(medicine);
                }
                medicineAdapter = new MedicineAdapter(medicineList);
                recyclerView.setAdapter(medicineAdapter);
            } else {
                // Handle the error
                Log.d("MedAppV4", "Fetch Else");
            }
        });
    }
}
