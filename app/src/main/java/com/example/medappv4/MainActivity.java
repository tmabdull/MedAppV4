package com.example.medappv4;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MedicineAdapter adapter;
    private FirebaseFirestore db;
    private ListenerRegistration registration;

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

        db = FirebaseFirestore.getInstance();

        // Fetch medicine data in real-time
        fetchMedicineData();
    }

    // Utility function to find the index of a medicine in the local list using its ID.
    // Returns -1 if not found.
    private int findMedicineIndexById(String id) {
        for (int i = 0; i < adapter.getMedicines().size(); i++) {
            if (adapter.getMedicines().get(i).getId().equals(id)) {
                return i;
            }
        }
        return -1; // Not found
    }

    // Function to fetch medicine data from Firestore and populate the RecyclerView.
    // It also sets up real-time updates to listen for changes in the Firestore database.
    private void fetchMedicineData() {
        registration = db.collection("medicines")
                .addSnapshotListener((QuerySnapshot snapshots, FirebaseFirestoreException e) -> {
                    if (e != null) {
                        // Handle error
                        Log.d("MedAppV4", "e is not null -> addSnapshotListener");
                        return;
                    }
                    if (snapshots != null && !snapshots.isEmpty()) {
                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            Medicine medicine = dc.getDocument().toObject(Medicine.class);
                            medicine.setId(dc.getDocument().getId()); // Set the ID
                            int index;

                            switch (dc.getType()) {
                                case ADDED:
                                    adapter.addMedicine(medicine);
                                    break;
                                case MODIFIED:
                                    // Get the ID of the modified document
                                    String modifiedDocId = dc.getDocument().getId();

                                    // Find the index of this ID in the adapter's dataset
                                    index = findMedicineIndexById(modifiedDocId);

                                    if (index != -1) {
                                        adapter.updateMedicine(index, medicine);
                                    }
                                    break;
                                case REMOVED:
                                    index = findMedicineIndexById(dc.getDocument().getId());
                                    if (index != -1) {
                                        adapter.removeMedicine(index);
                                    }
                                    break;
                            }
                        }
                    } else {
                        Log.d("MedAppV4", "Snapshots is null or empty");
                    }
                });
    }

    // Properly detaches the Firestore listener to prevent memory leaks when the activity is destroyed.
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Detach listener when the activity is destroyed to prevent memory leaks
        if (registration != null) {
            registration.remove();
        }
    }
}
