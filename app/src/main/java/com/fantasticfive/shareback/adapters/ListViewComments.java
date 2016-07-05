package com.fantasticfive.shareback.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fantasticfive.shareback.R;

import java.util.ArrayList;

/**
 * Created by Sagar on 22-04-2016.
 */
public class ListViewComments extends BaseAdapter {

    Context context;
    ArrayList<String> comments;
    public ListViewComments(Context context, ArrayList<String> comments){
        this.comments = comments;
        this.context = context;
    }

    @Override
    public int getCount() {
        return comments.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vi = inflate.inflate(R.layout.inner_comments_session_details, null);
        TextView tv = (TextView) vi.findViewById(R.id.tvComment);
        tv.setText(comments.get(position));
        return vi;

    }
}
