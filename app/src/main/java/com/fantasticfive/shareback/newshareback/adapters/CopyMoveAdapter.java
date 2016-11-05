package com.fantasticfive.shareback.newshareback.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.fantasticfive.shareback.R;
import com.fantasticfive.shareback.newshareback.beans.DirContentsBean;

import java.util.ArrayList;

/**
 * Created by sagar on 14/7/16.
 */
public class CopyMoveAdapter extends BaseAdapter implements View.OnClickListener {

    Context context;
    ArrayList<String> dirs,files;
    int dirsSize=0, filesSize=0;
    LayoutInflater inflater;
    Callback callback;

    public CopyMoveAdapter(Context context, DirContentsBean bean, Callback callback) {
        this.context = context;
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

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            vi = inflater.inflate(R.layout.inner_copymove, null);
            holder = new ViewHolder();
            holder.fileImage = (ImageView) vi.findViewById(R.id.file_dir_icon);
            holder.fileName = (TextView) vi.findViewById(R.id.dir_name);
            if(!isFile(position)){
                holder.fileImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.folder_icon_small));
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
        return (position < dirsSize) ? dirs.get(position) : files.get(position - dirsSize);
    }

    @Override
    public void onClick(View view) {
        String name = ((TextView)view.findViewById(R.id.dir_name)).getText().toString();

        Log.e("My Tag", "Position: "+ view.getId());
        if(!isFile(view.getId()))
            callback.onDirClicked(name);
    }

    public boolean isFile(int position){
        return (position < dirsSize) ? false : true;
    }

    class ViewHolder{
        ImageView fileImage;
        TextView fileName;
    }

    public interface Callback{
        void onDirClicked(String item);
    }
}
