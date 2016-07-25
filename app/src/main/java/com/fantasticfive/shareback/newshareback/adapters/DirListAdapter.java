package com.fantasticfive.shareback.newshareback.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fantasticfive.shareback.R;
import com.fantasticfive.shareback.newshareback.beans.DirContentsBean;

import java.util.ArrayList;

/**
 * Created by sagar on 14/7/16.
 */
public class DirListAdapter extends BaseAdapter implements View.OnClickListener {

    Context context;
    ArrayList<String> dirs,files;
    int dirsSize=0, filesSize=0;
    LayoutInflater inflater;
    Callback callback;

    public DirListAdapter(Context context, DirContentsBean bean, Callback callback) {
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
            vi = inflater.inflate(R.layout.inner_dirlist, null);
            holder = new ViewHolder();
            holder.fileName = (TextView) vi.findViewById(R.id.dir_name);
            holder.checkBox = (CheckBox) vi.findViewById(R.id.checkBox);
            if(!isFile(position)){
                holder.checkBox.setVisibility(View.INVISIBLE);
            }
            vi.setTag(holder);
            vi.setId(position);
        }
        else{
            holder = (ViewHolder) vi.getTag();
        }

        holder.fileName.setText(getContent(position));
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isChecked = ((CheckBox)view).isChecked();
                callback.onFileClicked(getContent(position), isChecked);
            }
        });
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
        boolean isChecked = ((CheckBox)view.findViewById(R.id.checkBox)).isChecked();

        ((CheckBox)view.findViewById(R.id.checkBox)).setChecked(!isChecked);

        Log.e("My Tag", "Position: "+ view.getId());
        if(isFile(view.getId()))
            callback.onFileClicked(name, !isChecked);
        else
            callback.onDirClicked(name);
    }

    public boolean isFile(int position){
        return (position < dirsSize) ? false : true;
    }

    class ViewHolder{
        TextView fileName;
        CheckBox checkBox;
    }

    public interface Callback{
        void onFileClicked(String item, boolean isChecked);
        void onDirClicked(String item);
    }
}
