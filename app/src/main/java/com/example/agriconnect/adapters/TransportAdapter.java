package com.example.agriconnect.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agriconnect.R;
import com.example.agriconnect.activities.TransporterDetailActivity;
import com.example.agriconnect.models.Transporter;

import java.util.List;

public class TransportAdapter extends RecyclerView.Adapter<TransportAdapter.ViewHolder> {

    private final Context context;
    private final List<Transporter> transportList;

    public TransportAdapter(Context context, List<Transporter> transportList) {
        this.context = context;
        this.transportList = transportList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_transport, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transporter t = transportList.get(position);

        holder.tvName.setText(t.getName() != null ? t.getName() : "N/A");
        holder.tvVehicle.setText("🚚 " + (t.getVehicleType() != null ? t.getVehicleType() : "N/A"));
        holder.tvContact.setText("📞 " + (t.getContact() != null ? t.getContact() : "N/A"));
        holder.tvLocation.setText("📍 " + (t.getLocation() != null ? t.getLocation() : "N/A"));
        holder.tvPrice.setText("💰 ₹" + (t.getPricePerKm() != null ? t.getPricePerKm() : "0") + "/km");

        if (t.isVerified()) {
            holder.tvVerified.setVisibility(View.VISIBLE);
            holder.tvVerified.setText("✓ Verified");
        } else {
            holder.tvVerified.setVisibility(View.GONE);
        }

        // KEY FIX: use itemView instead of cardView
        holder.itemView.setOnClickListener(v -> {
            android.util.Log.d("TRANSPORT_CLICK", "Opening detail for: " + t.getName());
            Intent intent = new Intent(context, TransporterDetailActivity.class);
            intent.putExtra("transporterId",       t.getId());
            intent.putExtra("transporterName",     t.getName());
            intent.putExtra("transporterVehicle",  t.getVehicleType());
            intent.putExtra("transporterContact",  t.getContact());
            intent.putExtra("transporterLocation", t.getLocation());
            intent.putExtra("transporterPrice",    t.getPricePerKm());
            intent.putExtra("transporterVerified", t.isVerified());
            intent.putExtra("transporterRating",   t.getRating());
            intent.putExtra("transporterGst",      t.getGstNumber());
            intent.putExtra("transporterLicense",  t.getLicenseNumber());
            intent.putExtra("transporterTotalTrips", t.getTotalTrips());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return transportList != null ? transportList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvVehicle, tvContact, tvLocation, tvPrice, tvVerified;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName     = itemView.findViewById(R.id.tvTransporterName);
            tvVehicle  = itemView.findViewById(R.id.tvTransporterVehicle);
            tvContact  = itemView.findViewById(R.id.tvTransporterContact);
            tvLocation = itemView.findViewById(R.id.tvTransporterLocation);
            tvPrice    = itemView.findViewById(R.id.tvTransporterPrice);
            tvVerified = itemView.findViewById(R.id.tvVerified);
            cardView   = itemView.findViewById(R.id.cardTransporter);
        }
    }
}