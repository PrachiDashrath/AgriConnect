package com.example.agriconnect;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;


// Import the activity specifically to avoid path errors
import com.example.agriconnect.BillActivity;

public class MarketPriceAdapter extends RecyclerView.Adapter<MarketPriceAdapter.ViewHolder> {

    private Context context;
    private List<MarketPrice> marketList;

    public MarketPriceAdapter(Context context, List<MarketPrice> marketList) {
        this.context = context;
        this.marketList = marketList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_market, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MarketPrice market = marketList.get(position);

        // Basic Data Binding
        holder.tvCropName.setText(market.getCropName());
        holder.tvPrice.setText("₹" + market.getPrice() + "/kg");

        // Display Grade (Quality)
        if (holder.tvGrade != null) {
            String grade = market.getGrade();
            holder.tvGrade.setText("Quality: " + (grade != null ? grade : "Pending"));
        }

        // Display Inspector Feedback
        if (holder.tvFeedback != null) {
            String feedback = market.getInspectorFeedback();
            if (feedback != null && !feedback.isEmpty()) {
                holder.tvFeedback.setText("Inspector Note: " + feedback);
                holder.tvFeedback.setVisibility(View.VISIBLE);
            } else {
                holder.tvFeedback.setVisibility(View.GONE);
            }
        }

        // ✅ FIXED: BUY NOW Redirect Logic
        holder.btnBuyNow.setOnClickListener(v -> {
            try {
                Log.d("MARKET_ADAPTER", "Buy Now clicked for: " + market.getCropName());

                Intent intent = new Intent(context, CheckoutActivity.class);

                // Passing essential data to the form
                intent.putExtra("cropName", market.getCropName());
                intent.putExtra("price", market.getPrice());
                intent.putExtra("farmerName", market.getName());
                intent.putExtra("location", market.getLocation());

                // Ensure the context is valid for starting activity
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            } catch (Exception e) {
                Log.e("MARKET_ADAPTER", "Redirect failed: " + e.getMessage());
                Toast.makeText(context, "Unable to open booking form", Toast.LENGTH_SHORT).show();
            }
        });

        // Certificate Button Logic
        holder.btnCertificate.setOnClickListener(v -> {
            Intent intent = new Intent(context, CertificateActivity.class);
            intent.putExtra("marketObject", market);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return marketList != null ? marketList.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCropName, tvPrice, tvGrade, tvFeedback;
        Button btnCertificate, btnBuyNow; // ✅ Added btnBuyNow here

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCropName = itemView.findViewById(R.id.tvMarketCropName);
            tvPrice = itemView.findViewById(R.id.tvMarketPrice);
            tvGrade = itemView.findViewById(R.id.tvMarketGrade);
            tvFeedback = itemView.findViewById(R.id.tvMarketFeedback);

            btnCertificate = itemView.findViewById(R.id.btnViewCertificate);
            // ✅ Initialize the Buy Now button from your XML ID
            btnBuyNow = itemView.findViewById(R.id.btnBuyNow);
        }
    }
}