package com.example.agriconnect;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class FarmerCropAdapter extends RecyclerView.Adapter<FarmerCropAdapter.FarmerViewHolder> {

    private List<Farmer> farmers;
    private Map<String, List<Crop>> farmerCropMap;

    public FarmerCropAdapter(List<Farmer> farmers, Map<String, List<Crop>> farmerCropMap) {
        this.farmers = farmers;
        this.farmerCropMap = farmerCropMap;
    }

    @NonNull
    @Override
    public FarmerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_farmer_crop, parent, false);
        return new FarmerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FarmerViewHolder holder, int position) {
        Farmer farmer = farmers.get(position);
        holder.txtName.setText("👨‍🌾 Name: " + farmer.getName());
        holder.txtContact.setText("📞 Contact: " + farmer.getContact());
        holder.txtCity.setText("📍 City: " + farmer.getCity());

        List<Crop> crops = farmerCropMap.get(farmer.getName());
        if (crops == null || crops.isEmpty()) {
            holder.txtCrops.setText("🌾 Crops: No crops added yet");
        } else {
            StringBuilder cropList = new StringBuilder("🌾 Crops: ");
            for (Crop c : crops) {
                cropList.append(c.getCropName()).append(", ");
            }
            cropList.setLength(cropList.length() - 2); // remove last comma
            holder.txtCrops.setText(cropList.toString());
        }
    }

    @Override
    public int getItemCount() {
        return farmers.size();
    }

    public static class FarmerViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtContact, txtCity, txtCrops;

        public FarmerViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtFarmerName);
            txtContact = itemView.findViewById(R.id.txtFarmerContact);
            txtCity = itemView.findViewById(R.id.txtFarmerCity);
            txtCrops = itemView.findViewById(R.id.txtFarmerCrops);
        }
    }
}
