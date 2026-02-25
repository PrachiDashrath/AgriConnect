package com.example.agriconnect;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.List;

public class InspectorAdapter extends RecyclerView.Adapter<InspectorAdapter.InspectorViewHolder> {

    private Context context;
    private List<Crop> cropList;
    private final String DB_URL = "https://agriconnect-5cd4a-default-rtdb.asia-southeast1.firebasedatabase.app/";

    public InspectorAdapter(Context context, List<Crop> cropList) {
        this.context = context;
        this.cropList = cropList;
    }

    @NonNull
    @Override
    public InspectorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_inspection, parent, false);
        return new InspectorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InspectorViewHolder holder, int position) {
        Crop crop = cropList.get(position);
        holder.tvCropName.setText(crop.getCropName());

        String[] grades = {"Grade A+", "Grade A", "Grade B", "Grade C"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, grades);
        holder.spinnerGrade.setAdapter(adapter);

        holder.btnAccept.setOnClickListener(v -> {
            String selectedGrade = holder.spinnerGrade.getSelectedItem().toString();
            String feedback = holder.etFeedback.getText().toString().trim();
            if (feedback.isEmpty()) {
                Toast.makeText(context, "Feedback is required", Toast.LENGTH_SHORT).show();
                return;
            }
            processAction(crop, "approved", selectedGrade, feedback);
        });

        holder.btnReject.setOnClickListener(v -> {
            String feedback = holder.etFeedback.getText().toString().trim();
            if (feedback.isEmpty()) {
                Toast.makeText(context, "Please explain the rejection", Toast.LENGTH_SHORT).show();
                return;
            }
            processAction(crop, "rejected", "N/A", feedback);
        });
    }

    private void processAction(Crop crop, String status, String grade, String feedback) {
        DatabaseReference db = FirebaseDatabase.getInstance(DB_URL).getReference();

        if (status.equals("approved")) {
            // Construct the object with all fields including grade and feedback
            MarketPrice marketItem = new MarketPrice(
                    crop.getCropName(),
                    crop.getPrice(),
                    crop.getLocation(),
                    crop.getCategory(),
                    crop.getQuantity(),
                    grade,          // <--- Ensuring this is passed
                    feedback,       // <--- Ensuring this is passed
                    crop.getFarmerName(),
                    crop.getFarmerPhone()
            );

            // Save to market_prices
            db.child("market_prices").child(crop.getCropId()).setValue(marketItem)
                    .addOnSuccessListener(aVoid -> {
                        db.child("pending_crops").child(crop.getCropId()).removeValue();
                        updateUI(crop);
                        Toast.makeText(context, "Crop Listed with Grade!", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void updateUI(Crop crop) {
        int index = cropList.indexOf(crop);
        if (index != -1) {
            cropList.remove(index);
            notifyItemRemoved(index);
        }
    }

    @Override
    public int getItemCount() { return cropList.size(); }

    static class InspectorViewHolder extends RecyclerView.ViewHolder {
        TextView tvCropName; Spinner spinnerGrade; EditText etFeedback; Button btnAccept, btnReject;
        public InspectorViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCropName = itemView.findViewById(R.id.tvCropName);
            spinnerGrade = itemView.findViewById(R.id.spinnerInspectorGrade);
            etFeedback = itemView.findViewById(R.id.etInspectorFeedback);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}