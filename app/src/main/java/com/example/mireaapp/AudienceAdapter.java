package com.example.mireaapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AudienceAdapter extends RecyclerView.Adapter<AudienceAdapter.ViewHolder> {

    private ArrayList<Audience> aus;

    public AudienceAdapter(ArrayList<Audience> aus) {
        this.aus = aus;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.audience_item, parent,
                false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Audience au = aus.get(position);
        holder.nameView.setText(au.getNameOfClass());
        holder.buildingView.setText(au.getBuilding());
        holder.numberOfClassView.setText(au.getNumOfClass());
    }

    @Override
    public int getItemCount() {
        return aus.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView nameView;
        public final TextView buildingView;

        public final TextView numberOfClassView;

        public ViewHolder(@NonNull View view) {
            super(view);
            nameView = view.findViewById(R.id.name);
            buildingView = view.findViewById(R.id.building);
            numberOfClassView = view.findViewById(R.id.numberOfPara);
        }
    }

}
