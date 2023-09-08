package com.example.medappv4.utils;

import android.content.Context;

import com.example.medappv4.models.Medicine;

public class DialogHelper {

    // Example method to show a dialog to add a new medicine
    public static void showAddMedicineDialog(Context context, MedicineAddListener listener) {
        // Create and show a dialog here
        // Use listener to send data back to MainActivity
    }

    // Add more methods for editing, deleting, etc.

    public interface MedicineAddListener {
        void onMedicineAdded(Medicine medicine);
    }
}
