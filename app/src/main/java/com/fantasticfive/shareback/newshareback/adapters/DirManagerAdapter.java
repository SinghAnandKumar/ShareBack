package com.fantasticfive.shareback.newshareback.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fantasticfive.shareback.R;
import com.fantasticfive.shareback.newshareback.beans.DirContentsBean;

import java.util.ArrayList;

/**
 * Created by sagar on 14/7/16.
 */
public class DirManagerAdapter extends BaseAdapter
        implements View.OnClickListener
        , View.OnLongClickListener{

    Activity activity;

    ArrayList<String> dirs,files;
    int dirsSize=0, filesSize=0;
    LayoutInflater inflater;
    Callback callback;
    boolean isLongClickEnabled = false;

    public DirManagerAdapter(Activity activity, DirContentsBean bean, Callback callback) {
        this.activity = activity;
        this.callback = callback;

        dirs = new ArrayList<>();
        files = new ArrayList<>();
        for (String dir : bean.getDirs()) {
            dirs.add(dir);
        }

        for(String file: bean.getFiles()){
            files.add(file);
        }

        dirsSize = bean.getDirs().size();
        filesSize = bean.getFiles().size();

        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return dirsSize+filesSize;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        View vi = convertView;
        ViewHolder holder;

        if(convertView == null){
            vi = inflater.inflate(R.layout.inner_dirmanage, null);
            holder = new ViewHolder();
            holder.fileName = (TextView) vi.findViewById(R.id.dir_name);
            holder.fileImage = (ImageView) vi.findViewById(R.id.file_dir_icon);
            if(!isFile(position)){
                holder.fileImage.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.folder_icon_small));
            }
            vi.setTag(holder);
            vi.setId(position);
        }
        else{
            holder = (ViewHolder) vi.getTag();
        }

        holder.fileName.setText(getContent(position));
        vi.setOnClickListener(this);
        return vi;
    }

    private String getContent(int position){
        String name = "";
        if(position < dirsSize)
            name = dirs.get(position);
        else
            name = files.get(position - dirsSize);
        return name;
    }

    @Override
    public void onClick(View view) {
        String name = ((TextView)view.findViewById(R.id.dir_name)).getText().toString();
        if(isLongClickEnabled){

        }

        Log.e("My Tag", "Position: "+ view.getId());
    }

    public boolean isFile(int position){
        return (position < dirsSize) ? false : true;
    }

    @Override
    public boolean onLongClick(View view) {
        isLongClickEnabled = true;
        return false;
    }

    class ViewHolder{
        ImageView fileImage;
        TextView fileName;
    }

    public interface Callback{

    }
}
