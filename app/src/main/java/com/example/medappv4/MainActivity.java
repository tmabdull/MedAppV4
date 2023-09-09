package com.example.medappv4;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

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

        // Add button
        FloatingActionButton fab = findViewById(R.id.add_medicine_fab);
        fab.setOnClickListener(v -> {
            showAddMedicineDialog();
        });

        // Fetch medicine data in real-time
        fetchMedicineData();
    }

    /**
     * Displays a dialog to the user, allowing them to input details for a new medicine.
     * The dialog includes:
     * 1. A field to input the medicine's name.
     * 2. Checkboxes for selecting the days of the week.
     * 3. A time picker to set the medicine's intake time.
     * 4. A save button to store the entered details.
     *
     * Upon pressing the save button, the entered details are collected,
     * a new Medicine object is created, and it is sent to Firestore
     * via the addMedicineToFirestore method.
     */
    private void showAddMedicineDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edit_medicine, null);
        builder.setView(view);

        EditText medicineName = view.findViewById(R.id.edit_medicine_name);
        TimePicker timePicker = view.findViewById(R.id.time_picker_medicine);
        CheckBox[] dayChecks = {
                view.findViewById(R.id.check_sunday),
                view.findViewById(R.id.check_monday),
                view.findViewById(R.id.check_tuesday),
                view.findViewById(R.id.check_wednesday),
                view.findViewById(R.id.check_thursday),
                view.findViewById(R.id.check_friday),
                view.findViewById(R.id.check_saturday)
        };

        builder.setPositiveButton("Save", (dialog, which) -> {
            // Retrieve data
            String name = medicineName.getText().toString();
            List<Boolean> days = new ArrayList<>();
            for (CheckBox check : dayChecks) {
                days.add(check.isChecked());
            }
            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();

            // Create a new Medicine instance
            Medicine newMedicine = new Medicine(days, minute, hour, name);

            // Save this to Firestore
            addMedicineToFirestore(newMedicine);
        });

        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Adds a Medicine object to the Firestore collection named 'medicines'.
     *
     * @param medicine The Medicine object to be added to Firestore.
     *
     * If the medicine is successfully saved to Firestore, the Medicine object's ID
     * attribute is set to reflect its Firestore document ID.
     * On failure, an error message is shown to the user.
     */
    private void addMedicineToFirestore(Medicine medicine) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("medicines")
                .add(medicine)
                .addOnSuccessListener(documentReference -> {
                    Log.d("MainActivity", "DocumentSnapshot added with ID: " + documentReference.getId());

                    // Set the document ID as the Medicine's ID
                    medicine.setId(documentReference.getId());

                    // Optionally, you can update the medicine in Firestore with the ID, but it's often kept client-side
                })
                .addOnFailureListener(e -> {
                    Log.w("MainActivity", "Error adding document", e);
                    Toast.makeText(MainActivity.this, "Error saving medicine!", Toast.LENGTH_SHORT).show();
                });
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

