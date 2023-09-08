package com.example.medappv4;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.MedicineViewHolder> {
    private List<Medicine> medicines;

    // Constructor for the adapter. Initializes the local medicines list.
    public MedicineAdapter(List<Medicine> medicines) {
        this.medicines = medicines;
    }

    // Getter method for the list of medicines, can be used elsewhere to fetch the current state of the medicines list.
    public List<Medicine> getMedicines() {
        return medicines;
    }

    // Returns the number of medicines in the list. Used by RecyclerView to determine the number of items.
    @Override
    public int getItemCount() {
        return medicines.size();
    }

    // Called when RecyclerView needs a new ViewHolder to represent an item. This inflates the item layout and initializes the ViewHolder.
    @NonNull
    @Override
    public MedicineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medicine, parent, false);
        return new MedicineViewHolder(view);
    }

    // Binds the data from a specific medicine to a ViewHolder. This is where we populate our item views with data.
    @Override
    public void onBindViewHolder(@NonNull MedicineViewHolder holder, int position) {
        Medicine medicine = medicines.get(position);
        holder.medicineName.setText(medicine.getName());
        holder.medicineTime.setText(String.format(Locale.US, "%02d:%02d",
                                    medicine.getHourOfDay(), medicine.getMinute()));

        // Convert day boolean list to a string
        String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        StringBuilder medicineDays = new StringBuilder();
        for (int i = 0; i < medicine.getDaysOfWeek().size(); i++) {
            if (medicine.getDaysOfWeek().get(i)) {
                medicineDays.append(days[i]).append(", ");
            }
        }
        if (medicineDays.length() > 0) {
            medicineDays.delete(medicineDays.length() - 2, medicineDays.length());
        }
        holder.medicineDays.setText(medicineDays.toString());
    }

    // Inner ViewHolder class. Holds references to individual item views to avoid frequent lookups.
    public static class MedicineViewHolder extends RecyclerView.ViewHolder {
        TextView medicineName, medicineTime, medicineDays;

        // Constructor for the ViewHolder. Initializes item view references.
        MedicineViewHolder(@NonNull View itemView) {
            super(itemView);
            medicineName = itemView.findViewById(R.id.medicine_name);
            medicineTime = itemView.findViewById(R.id.medicine_time);
            medicineDays = itemView.findViewById(R.id.medicine_days);
        }
    }

    // Method to add a new medicine to the list and notify RecyclerView of the change.
    public void addMedicine(Medicine medicine) {
        this.medicines.add(medicine);
        notifyItemInserted(medicines.size() - 1);
    }

    // Method to update an existing medicine in the list at a specific position and notify RecyclerView of the change.
    public void updateMedicine(int position, Medicine updatedMedicine) {
        this.medicines.set(position, updatedMedicine);
        notifyItemChanged(position);
    }

    // Method to remove a medicine from the list at a specific position and notify RecyclerView of the change.
    public void removeMedicine(int position) {
        this.medicines.remove(position);
        notifyItemRemoved(position);
    }

}
