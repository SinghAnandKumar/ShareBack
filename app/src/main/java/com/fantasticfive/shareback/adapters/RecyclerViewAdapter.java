package com.fantasticfive.shareback.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fantasticfive.shareback.R;
import com.fantasticfive.shareback.beans.ItemHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sagar on 7/5/2016.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<ItemHolder> {
    private List<String> itemsName;
    private OnItemClickListener onItemClickListener;
    private LayoutInflater layoutInflater;

    public RecyclerViewAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
        itemsName = new ArrayList<>();
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.inner_item, parent, false);
        return new ItemHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        holder.setItemName(itemsName.get(position));
    }

    @Override
    public int getItemCount() {
        return itemsName.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(ItemHolder item, int position);
    }

    public void add(int location, String iName) {
        itemsName.add(location, iName);
        notifyItemInserted(location);
    }

    public void remove(int location) {
        if (location >= itemsName.size())
            return;

        itemsName.remove(location);
        notifyItemRemoved(location);
    }
}