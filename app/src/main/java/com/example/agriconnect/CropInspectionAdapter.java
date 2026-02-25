package com.example.agriconnect;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CropInspectionAdapter
        extends RecyclerView.Adapter<CropInspectionAdapter.ViewHolder> {

    private Context context;
    private List<com.example.agriconnect.CropInspectionRequest> list;

    public CropInspectionAdapter(Context context,
                                 List<com.example.agriconnect.CropInspectionRequest> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                         int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_inspector_crop, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,
                                 int position) {

        com.example.agriconnect.CropInspectionRequest request = list.get(position);

        holder.tvCropName.setText("🌾 Crop: " + request.getCropName());
        holder.tvFarmerName.setText("Farmer: " + request.getFarmerName());
        holder.tvLocation.setText("Location: " + request.getLocation());
        holder.tvCategory.setText("Category: " + request.getCategory());

        holder.btnApprove.setOnClickListener(v -> {
            request.setStatus(InspectionStatus.APPROVED.name());

            com.example.agriconnect.DummyDatabase.notifyFarmer(
                    request.getFarmerName(),
                    "✅ Your crop " + request.getCropName() + " has been approved"
            );

            Toast.makeText(context, "Crop Approved", Toast.LENGTH_SHORT).show();
            removeItem(holder.getAdapterPosition());
        });

        holder.btnReject.setOnClickListener(v -> {
            request.setStatus(InspectionStatus.REJECTED.name());

            com.example.agriconnect.DummyDatabase.notifyFarmer(
                    request.getFarmerName(),
                    "❌ Your crop " + request.getCropName() + " has been rejected"
            );

            Toast.makeText(context, "Crop Rejected", Toast.LENGTH_SHORT).show();
            removeItem(holder.getAdapterPosition());
        });
    }

    private void removeItem(int position) {
        if (position != RecyclerView.NO_POSITION) {
            list.remove(position);
            notifyItemRemoved(position);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvCropName, tvFarmerName, tvLocation, tvCategory;
        Button btnApprove, btnReject;

        ViewHolder(View itemView) {
            super(itemView);

            tvCropName = itemView.findViewById(R.id.tvCropName);
            tvFarmerName = itemView.findViewById(R.id.tvFarmerName);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}
