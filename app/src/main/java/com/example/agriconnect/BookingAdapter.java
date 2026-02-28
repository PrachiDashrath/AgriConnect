package com.example.agriconnect;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private Context context;
    private List<Booking> bookingList;

    public BookingAdapter(Context context, List<Booking> bookingList) {
        this.context = context;
        this.bookingList = bookingList;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookingList.get(position);
        holder.tvCrop.setText(booking.cropName);
        holder.tvStatus.setText(booking.status.toUpperCase());
        holder.tvTransporter.setText("🚛 " + booking.transporterName);
        holder.tvTotal.setText("Total Paid: ₹" + booking.totalAmount);
        holder.tvLocation.setText("📍 Delivery to: " + booking.buyerLocation);
    }

    @Override
    public int getItemCount() { return bookingList.size(); }

    static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView tvCrop, tvStatus, tvTransporter, tvTotal, tvLocation;
        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCrop = itemView.findViewById(R.id.tvOrderCrop);
            tvStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvTransporter = itemView.findViewById(R.id.tvOrderTransporter);
            tvTotal = itemView.findViewById(R.id.tvOrderTotal);
            // Add this ID to your item_booking.xml if not there
            tvLocation = itemView.findViewById(R.id.tvLocation);
        }
    }
}