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

public class BuyerAdapter extends RecyclerView.Adapter<BuyerAdapter.ViewHolder> {

    private Context context;
    private List<Crop> approvedList;

    // Constructor
    public BuyerAdapter(Context context, List<Crop> approvedList) {
        this.context = context;
        this.approvedList = approvedList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // We will create item_market_crop.xml next
        View view = LayoutInflater.from(context).inflate(R.layout.item_market, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Crop crop = approvedList.get(position);

        // Setting data from your Crop.java class
        holder.tvCropName.setText(crop.getCropName());
        holder.tvCategory.setText(crop.getCategory());
        holder.tvPrice.setText("₹" + crop.getPrice() + " /kg");
        holder.tvQuantity.setText("Available: " + crop.getQuantity() + " kg");
        holder.tvLocation.setText("📍 " + crop.getLocation());

        holder.btnBuy.setOnClickListener(v -> {
            // Logic for buying or contacting the farmer
            Toast.makeText(context, "Order placed for " + crop.getCropName(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return approvedList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCropName, tvCategory, tvPrice, tvQuantity, tvLocation;
        Button btnBuy;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCropName = itemView.findViewById(R.id.tvMarketCropName);
            tvCategory = itemView.findViewById(R.id.tvMarketCategory);
            tvPrice = itemView.findViewById(R.id.tvMarketPrice);
            tvQuantity = itemView.findViewById(R.id.tvMarketQuantity);
            tvLocation = itemView.findViewById(R.id.tvMarketLocation);
            btnBuy = itemView.findViewById(R.id.btnBuyNow);
        }
    }
}