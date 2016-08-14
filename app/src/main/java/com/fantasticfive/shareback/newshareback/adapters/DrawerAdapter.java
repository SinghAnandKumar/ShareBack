package com.fantasticfive.shareback.newshareback.adapters;

import android.content.Context;
import android.media.Image;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fantasticfive.shareback.R;
import com.fantasticfive.shareback.newshareback.ShareBucket;

/**
 * Created by sagar on 12/8/16.
 */
public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.ViewHolder> {

    ShareBucket shareBucket;
    Context context;
    public DrawerAdapter(Context context, ShareBucket shareBucket){
        this.context = context;
        this.shareBucket = shareBucket;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.inner_drawer_file_view_instructor, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //holder.txtFile.setText(shareBucket.getFileNameAt(position));
        Toast.makeText(context, "I am Here", Toast.LENGTH_SHORT).show();
        holder.txtFile.setText("File"+position);
        holder.imgFile.setImageDrawable(
                ContextCompat.getDrawable(context, R.drawable.ic_insert_drive_file_black_24dp)
        );
    }

    @Override
    public int getItemCount() {
        //return shareBucket.getOpenedFileSet().size();
        return 5;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtFile;
        public ImageView imgFile;

        public ViewHolder(View view) {
            super(view);
            txtFile = (TextView) view.findViewById(R.id.text_file);
            imgFile = (ImageView) view.findViewById(R.id.img_file);
        }
    }
}
