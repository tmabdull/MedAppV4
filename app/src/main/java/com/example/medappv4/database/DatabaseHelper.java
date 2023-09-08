package com.example.medappv4.database;

import static com.example.medappv4.constants.Constants.MEDICINES_COLLECTION;

import android.util.Log;

import androidx.annotation.Nullable;

import com.example.medappv4.models.Medicine;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

public class DatabaseHelper {
    private FirebaseFirestore db;
    private ListenerRegistration registration;

    public DatabaseHelper() {
        db = FirebaseFirestore.getInstance();
    }

    public enum MedicineChangeType {
        ADDED, MODIFIED, REMOVED
    }

    public interface MedicineDataCallback {
        void onMedicineAdded(Medicine medicine);
        void onMedicineUpdated(Medicine medicine);
        void onMedicineRemoved(Medicine medicine);
    }

    // Function to fetch medicine data from Firestore and populate the RecyclerView.
    // It also sets up real-time updates to listen for changes in the Firestore database.
    public void fetchMedicineData(final MedicineDataCallback callback) {
        // Assuming you have a Firestore collection named "medicines"
        CollectionReference medicinesRef = FirebaseFirestore.getInstance().collection(MEDICINES_COLLECTION);
        registration = medicinesRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    // Handle the error
                    return;
                }
                if (value == null) {
                    return; // Add this check here to prevent NPE.
                }

                for (DocumentChange docChange : value.getDocumentChanges()) {
                    Medicine medicine = docChange.getDocument().toObject(Medicine.class);
                    medicine.setId(docChange.getDocument().getId()); // Set ID manually if it's not being set by default

                    switch (docChange.getType()) {
                        case ADDED:
                            callback.onMedicineAdded(medicine);
                            break;
                        case MODIFIED:
                            callback.onMedicineUpdated(medicine);
                            break;
                        case REMOVED:
                            callback.onMedicineRemoved(medicine);
                            break;
                    }
                }
            }
        });
    }

    // Add future methods for adding, editing, and deleting medicines

    // To handle listener deregistration
    public void detachListener() {
        if (registration != null) {
            registration.remove();
        }
    }
}
