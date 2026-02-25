package com.example.agriconnect;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SchemeAdapter extends RecyclerView.Adapter<SchemeAdapter.SchemeViewHolder> {

    private Context context;
    private List<Scheme> schemes;

    public SchemeAdapter(Context context, List<Scheme> schemes) {
        this.context = context;
        this.schemes = schemes;
    }

    @NonNull
    @Override
    public SchemeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_scheme, parent, false);
        return new SchemeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SchemeViewHolder holder, int position) {
        Scheme scheme = schemes.get(position);
        holder.tvName.setText(scheme.getName());
        holder.tvDesc.setText(scheme.getDescription());

        holder.itemView.setOnClickListener(v -> {
            // Open scheme URL in browser
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(scheme.getUrl()));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return schemes.size();
    }

    static class SchemeViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDesc;

        public SchemeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvSchemeName);
            tvDesc = itemView.findViewById(R.id.tvSchemeDesc);
        }
    }
}
