package com.example.medappv4;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.MedicineViewHolder> {
    private List<Medicine> medicines;

    public MedicineAdapter(List<Medicine> medicines) {
        this.medicines = medicines;
    }

    @NonNull
    @Override
    public MedicineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medicine, parent, false);
        return new MedicineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicineViewHolder holder, int position) {
        Medicine medicine = medicines.get(position);
        holder.medicineName.setText(medicine.getName());
        holder.medicineTime.setText(String.format("%02d:%02d", medicine.getHourOfDay(), medicine.getMinute()));

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

    @Override
    public int getItemCount() {
        return medicines.size();
    }

    class MedicineViewHolder extends RecyclerView.ViewHolder {
        TextView medicineName, medicineTime, medicineDays;

        MedicineViewHolder(@NonNull View itemView) {
            super(itemView);
            medicineName = itemView.findViewById(R.id.medicine_name);
            medicineTime = itemView.findViewById(R.id.medicine_time);
            medicineDays = itemView.findViewById(R.id.medicine_days);
        }
    }
}
