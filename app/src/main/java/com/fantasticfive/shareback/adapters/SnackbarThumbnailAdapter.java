package com.fantasticfive.shareback.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fantasticfive.shareback.R;
import com.fantasticfive.shareback.newshareback.beans.ShareBucketItem;

import java.util.ArrayList;

/**
 * Created by sagar on 07-Jul-16.
 */
public class SnackbarThumbnailAdapter extends BaseAdapter {

    Context context;
    ArrayList<ShareBucketItem> thumbnailArray;
    private LayoutInflater inflater = null;

    public SnackbarThumbnailAdapter(Context context, ArrayList<ShareBucketItem> thumbnailArray){
        this.context = context;
        this.thumbnailArray = thumbnailArray;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return thumbnailArray.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder{

        ImageView thumbnail;
        TextView fileName;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;

        if(convertView == null){
            vi = inflater.inflate(R.layout.inner_snackbar_item, null);
            holder = new ViewHolder();
            holder.thumbnail = (ImageView) vi.findViewById(R.id.image);
            holder.fileName = (TextView) vi.findViewById(R.id.inner_snackbar_filename);
            vi.setTag(holder);
        }
        else{
            holder = (ViewHolder) vi.getTag();
        }

        ShareBucketItem item = thumbnailArray.get(position);
        holder.fileName.setText(item.getFileName());
        holder.thumbnail.setImageDrawable(item.getThumbnail());

        return vi;
    }

}
