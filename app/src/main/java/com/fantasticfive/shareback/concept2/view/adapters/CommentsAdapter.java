package com.fantasticfive.shareback.concept2.view.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fantasticfive.shareback.R;
import com.fantasticfive.shareback.concept2.util.WordUtils;
import com.github.ivbaranov.mli.MaterialLetterIcon;

import java.util.ArrayList;

/**
 * Created by sagar on 5/11/16.
 */
public class CommentsAdapter extends BaseAdapter {

    ArrayList<String> comments;
    Context context;
    public CommentsAdapter(Context context, ArrayList<String> comments){
        this.comments = comments;
        this.context = context;
    }

    @Override
    public int getCount() {
        return comments.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        String comment = comments.get(position);
        comment = comment.trim();

        LayoutInflater inflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vi = inflate.inflate(R.layout.c2_inner_comment, null);
        TextView tv = (TextView) vi.findViewById(R.id.tvComment);
        TextView icon = (TextView) vi.findViewById(R.id.firstChar);
        comment = WordUtils.capitalizeFirstChar(comment);
        icon.setText(WordUtils.firstChar(comment)+"");
        tv.setText(comment);
        return vi;
    }
}
