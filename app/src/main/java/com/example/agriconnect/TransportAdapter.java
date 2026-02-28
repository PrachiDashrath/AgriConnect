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

import com.example.agriconnect.models.Transporter;

import java.util.List;

public class TransportAdapter extends RecyclerView.Adapter<TransportAdapter.TransportViewHolder> {

    private Context context;
    private List<Transporter> list;

    public TransportAdapter(Context context, List<Transporter> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public TransportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TransportViewHolder(LayoutInflater.from(context).inflate(R.layout.item_transport, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TransportViewHolder holder, int position) {
        Transporter t = list.get(position);
        holder.price.setText(t.getVehicleType());
        holder.price.setText(t.getPricePerKm());
        holder.price.setText("₹ " + t.pricePerKm + "/km");

        holder.btnBook.setOnClickListener(v -> {
            Toast.makeText(context, "Booking requested from " + t.getName(), Toast.LENGTH_SHORT).show();
            // In a real app, you would save this booking to a "bookings" node
        });
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class TransportViewHolder extends RecyclerView.ViewHolder {
        TextView name, type, price;
        Button btnBook;
        public TransportViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvTransporterName);
            type = itemView.findViewById(R.id.tvVehicleType);
            price = itemView.findViewById(R.id.tvTransporterPrice);
            btnBook = itemView.findViewById(R.id.btnTransport);
        }
    }
}