package com.ksu.nafea.ui.nafea_views.recycler_view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GeneralRecyclerAdapter extends RecyclerView.Adapter<GeneralRecyclerViewHolder>
{
    private Context context;
    private ListAdapter listAdapter;


    public GeneralRecyclerAdapter(Context context, ListAdapter listAdapter)
    {
        this.context = context;
        this.listAdapter = listAdapter;
    }



    @NonNull
    @Override
    public GeneralRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(listAdapter.getResourceLayout(), parent, false);
        return new GeneralRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GeneralRecyclerViewHolder holder, int position)
    {
        listAdapter.onBind(holder.itemView, position);
    }

    @Override
    public int getItemCount()
    {
        return listAdapter.getItemCount();
    }
}
