package com.ksu.nafea.ui.nafea_views.recycler_view;

import android.view.View;

import androidx.annotation.LayoutRes;

public interface ListAdapter
{
    public @LayoutRes int getResourceLayout();
    public int getItemCount();
    public void onBind(View itemView, int position);
}
