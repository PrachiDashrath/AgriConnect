package com.example.agriconnect;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.*;
import java.util.List;
import com.example.agriconnect.MarketPrice; // Ensure correct import

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
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_dropdown_item, grades);
        holder.spinnerGrade.setAdapter(adapter);

        holder.btnAccept.setOnClickListener(v -> {
            String selectedGrade = holder.spinnerGrade.getSelectedItem().toString();
            String feedback = holder.etFeedback.getText().toString().trim();
            if (feedback.isEmpty()) {
                Toast.makeText(context, "Feedback is required", Toast.LENGTH_SHORT).show();
                return;
            }
            // Use the reliable approval logic
            approveCrop(crop, selectedGrade, feedback);
        });

        holder.btnReject.setOnClickListener(v -> {
            DatabaseReference db = FirebaseDatabase.getInstance(DB_URL).getReference();
            // Reliable rejection logic from your old code
            db.child("pending_crops").child(crop.getCropId()).removeValue()
                    .addOnSuccessListener(aVoid -> {
                        updateUI(crop);
                        Toast.makeText(context, "Crop Rejected", Toast.LENGTH_SHORT).show();
                    });
        });
    }

    private void approveCrop(Crop crop, String grade, String feedback) {
        DatabaseReference db = FirebaseDatabase.getInstance(DB_URL).getReference();

        // ✅ THE FIX: Use the data already inside the 'Crop' object
        // We don't need to search the 'farmers' table and risk the "Not Found" error
        MarketPrice marketItem = new MarketPrice(
                crop.getCropName(),
                crop.getPrice(),
                crop.getLocation(),
                crop.getCategory(),
                crop.getQuantity(),
                grade,
                feedback,
                crop.getFarmerName()
        );

        // Save directly to market_prices using the CropId as the key
        db.child("market_prices").child(crop.getCropId()).setValue(marketItem)
                .addOnSuccessListener(aVoid -> {
                    // Remove from pending once saved to marketplace
                    db.child("pending_crops").child(crop.getCropId()).removeValue();
                    updateUI(crop);
                    Toast.makeText(context, "Approved & Listed in Marketplace!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
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
        TextView tvCropName;
        Spinner spinnerGrade;
        EditText etFeedback;
        Button btnAccept, btnReject;

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