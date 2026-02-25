package com.example.agriconnect;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.stream.Collectors;

public class FarmerAdapter extends RecyclerView.Adapter<FarmerAdapter.FarmerViewHolder> {

    private List<Farmer> farmers;

    public FarmerAdapter(List<Farmer> farmers) {
        this.farmers = farmers;
    }

    @NonNull
    @Override
    public FarmerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_farmer, parent, false);
        return new FarmerViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull FarmerViewHolder holder, int position) {
        Farmer farmer = farmers.get(position);
        holder.txtName.setText("Name: " + farmer.getName());
        holder.txtContact.setText("Contact: " + farmer.getContact());
        holder.txtLocation.setText("Location: " + farmer.getCity());

        // ✅ Fetch crops added by this farmer
        List<Crop> farmerCrops = DummyDatabase.getAllCrops().stream().filter(crop -> crop.getCropName().equalsIgnoreCase(farmer.getName()))
                .collect(Collectors.toList());

        if (farmerCrops.isEmpty()) {
            holder.txtCrops.setText("Crops: No crops added yet");
        } else {
            StringBuilder cropsList = new StringBuilder();
            for (Crop crop : farmerCrops) {
                cropsList.append(crop.getCropName())
                        .append(" - ₹")
                        .append(crop.getPrice())
                        .append("\n");
            }
            holder.txtCrops.setText("Crops:\n" + cropsList.toString().trim());
        }
    }

    @Override
    public int getItemCount() {
        return farmers.size();
    }

    static class FarmerViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtContact, txtLocation, txtCrops;

        public FarmerViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtFarmerName);
            txtContact = itemView.findViewById(R.id.txtFarmerContact);
            txtLocation = itemView.findViewById(R.id.txtFarmerCity);
            txtCrops = itemView.findViewById(R.id.txtFarmerCrops);
        }
    }
}
