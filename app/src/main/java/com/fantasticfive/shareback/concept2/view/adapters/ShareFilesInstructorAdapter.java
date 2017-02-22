package com.fantasticfive.shareback.concept2.view.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fantasticfive.shareback.R;
import com.fantasticfive.shareback.concept2.bean.SharedFile;

import java.util.ArrayList;

/**
 * Created by sagar on 18/2/17.
 */
public class ShareFilesInstructorAdapter extends BaseAdapter{

    Context context;
    ArrayList<SharedFile> fileList;
    public ShareFilesInstructorAdapter(Context context, ArrayList<SharedFile> fileList){
        this.context = context;
        this.fileList = fileList;
    }

    @Override
    public int getCount() {
        return fileList.size();
    }

    @Override
    public Object getItem(int i) {
        return fileList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if(view == null){
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.c2_inner_shared_files_instructor, null);
            holder.name = (TextView) view.findViewById(R.id.name);
            view.setTag(holder);
        }
        else{
            holder = (ViewHolder) view.getTag();
        }
        SharedFile file = fileList.get(position);
        holder.name.setText(file.getName());
        return view;
    }

    class ViewHolder{
        TextView name;
    }
}
