package com.ksu.nafea.ui.nafea_views.recycler_view;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GeneralRecyclerViewHolder extends RecyclerView.ViewHolder
{
    public View itemView;

    public GeneralRecyclerViewHolder(@NonNull View itemView)
    {
        super(itemView);
        this.itemView = itemView;
    }
}
