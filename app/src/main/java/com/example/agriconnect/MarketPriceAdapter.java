package com.example.agriconnect;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MarketPriceAdapter extends RecyclerView.Adapter<MarketPriceAdapter.MarketViewHolder> {

    private Context context;
    private List<MarketPrice> marketList;

    public MarketPriceAdapter(Context context, List<MarketPrice> marketList) {
        this.context = context;
        this.marketList = marketList;
    }

    @NonNull
    @Override
    public MarketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_market, parent, false);
        return new MarketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MarketViewHolder holder, int position) {
        MarketPrice market = marketList.get(position);

        // Standard Fields
        holder.tvCropName.setText(market.getCropName());
        holder.tvPrice.setText("₹" + market.getPrice() + "/kg");
        holder.tvLocation.setText("📍 " + market.getLocation());
        holder.tvCategory.setText(market.getCategory());
        holder.tvQuantity.setText("Qty: " + market.getQuantity() + " kg");

        // Grade and Feedback Fields
        // We use a fallback "N/A" just in case data is missing in Firebase
        String grade = market.getGrade();
        holder.tvGrade.setText("Grade: " + (grade != null && !grade.isEmpty() ? grade : "Pending"));

        String feedback = market.getInspectorFeedback();
        if (holder.tvFeedback != null) {
            holder.tvFeedback.setText("Note: " + (feedback != null && !feedback.isEmpty() ? feedback : "No inspector notes"));
        }
    }

    @Override
    public int getItemCount() {
        return marketList.size();
    }

    static class MarketViewHolder extends RecyclerView.ViewHolder {
        TextView tvCropName, tvPrice, tvLocation, tvCategory, tvQuantity, tvGrade, tvFeedback;

        public MarketViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ensure these IDs match your item_market.xml exactly
            tvCropName = itemView.findViewById(R.id.tvMarketCropName);
            tvPrice = itemView.findViewById(R.id.tvMarketPrice);
            tvLocation = itemView.findViewById(R.id.tvMarketLocation);
            tvCategory = itemView.findViewById(R.id.tvMarketCategory);
            tvQuantity = itemView.findViewById(R.id.tvMarketQuantity);
            tvGrade = itemView.findViewById(R.id.tvMarketGrade);
            tvFeedback = itemView.findViewById(R.id.tvMarketFeedback); // Optional field
        }
    }
}