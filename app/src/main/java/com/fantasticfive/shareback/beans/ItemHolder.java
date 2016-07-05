package com.fantasticfive.shareback.beans;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.fantasticfive.shareback.R;
import com.fantasticfive.shareback.adapters.RecyclerViewAdapter;

/**
 * Created by sagar on 7/5/2016.
 */
public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    private RecyclerViewAdapter parent;
    TextView textItemName;

    public ItemHolder(View itemView, RecyclerViewAdapter parent) {
        super(itemView);
        itemView.setOnClickListener(this);
        this.parent = parent;
        textItemName = (TextView) itemView.findViewById(R.id.item_name);
    }

    public void setItemName(CharSequence name){
        textItemName.setText(name);
    }

    public CharSequence getItemName(){
        return textItemName.getText();
    }

    @Override
    public void onClick(View v) {
        final RecyclerViewAdapter.OnItemClickListener listener = parent.getOnItemClickListener();
        if(listener != null){
            listener.onItemClick(this, getPosition());
        }
    }
}