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

import androidx.annotation.Nullable;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class MainActivity extends AppCompatActivity implements MedicineAdapter.MedicineEditListener {
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

        // Pass 'this' as the second parameter to MedicineAdapter since MainActivity implements MedicineEditListener
        adapter = new MedicineAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        // Add button
        FloatingActionButton fab = findViewById(R.id.add_medicine_fab);
        fab.setOnClickListener(v -> {
            showAddEditDialog(Optional.empty());
        });

        // Fetch medicine data in real-time
        fetchMedicineData();
    }

    /**
     * Displays a dialog allowing users to add or edit a medicine.
     *
     * @param medicineToEditOptional The medicine to edit. If null, the user is adding a new medicine.
     */
    public void showAddEditDialog(Optional<Medicine> medicineToEditOptional) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_edit_medicine, null);

        // Link UI components
        EditText editName = view.findViewById(R.id.edit_medicine_name);

        CheckBox checkSunday = view.findViewById(R.id.check_sunday);
        CheckBox checkMonday = view.findViewById(R.id.check_monday);
        CheckBox checkTuesday = view.findViewById(R.id.check_tuesday);
        CheckBox checkWednesday = view.findViewById(R.id.check_wednesday);
        CheckBox checkThursday = view.findViewById(R.id.check_thursday);
        CheckBox checkFriday = view.findViewById(R.id.check_friday);
        CheckBox checkSaturday = view.findViewById(R.id.check_saturday);

        TimePicker timePicker = view.findViewById(R.id.time_picker_medicine);

        if (medicineToEditOptional.isPresent()) {
            // This means we're editing, so populate fields with current data
            Medicine medicineToEdit = medicineToEditOptional.get();

            editName.setText(medicineToEdit.getName());

            checkSunday.setChecked(medicineToEdit.getDaysOfWeek().get(0));
            checkMonday.setChecked(medicineToEdit.getDaysOfWeek().get(1));
            checkTuesday.setChecked(medicineToEdit.getDaysOfWeek().get(2));
            checkWednesday.setChecked(medicineToEdit.getDaysOfWeek().get(3));
            checkThursday.setChecked(medicineToEdit.getDaysOfWeek().get(4));
            checkFriday.setChecked(medicineToEdit.getDaysOfWeek().get(5));
            checkSaturday.setChecked(medicineToEdit.getDaysOfWeek().get(6));

            timePicker.setHour(medicineToEdit.getHourOfDay());
            timePicker.setMinute(medicineToEdit.getMinute());
        }

        builder.setView(view)
                .setTitle(medicineToEditOptional.orElse(null) == null ? "Add Medicine" : "Edit Medicine")
                .setPositiveButton("Save", (dialog, which) -> {
                    String name = editName.getText().toString();
                    List<Boolean> days = Arrays.asList(
                            checkSunday.isChecked(),
                            checkMonday.isChecked(),
                            checkTuesday.isChecked(),
                            checkWednesday.isChecked(),
                            checkThursday.isChecked(),
                            checkFriday.isChecked(),
                            checkSaturday.isChecked()
                    );
                    int hour = timePicker.getHour();
                    int minute = timePicker.getMinute();
                    Medicine medicine = new Medicine(days, hour, minute, name);
                    medicine.setName(name);

                    if (medicineToEditOptional.orElse(null) == null) {
                        addMedicineToFirestore(medicine);
                    } else {
                        medicine.setId(medicineToEditOptional.orElse(null).getId());  // Important: Set ID for edited medicine
                        updateMedicineInFirestore(medicine);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
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

    /**
     * Updates an existing medicine document in Firestore.
     *
     * @param updatedMedicine The edited medicine with updated details.
     */
    private void updateMedicineInFirestore(Medicine updatedMedicine) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("medicines").document(updatedMedicine.getId())
                .set(updatedMedicine)
                .addOnSuccessListener(aVoid -> {
                    Log.d("MainActivity", "DocumentSnapshot successfully updated!");
                })
                .addOnFailureListener(e -> {
                    Log.w("MainActivity", "Error updating document", e);
                    Toast.makeText(MainActivity.this, "Error updating medicine!", Toast.LENGTH_SHORT).show();
                });
    }

    // Implementation of the method from MedicineEditListener
    @Override
    public void onEditRequested(Medicine medicine) {
        showAddEditDialog(Optional.of(medicine));
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

