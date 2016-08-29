package com.fantasticfive.shareback.newshareback.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.PopupWindowCompat;
import android.support.v7.widget.PopupMenu;
import android.view.ActionMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fantasticfive.shareback.R;
import com.fantasticfive.shareback.newshareback.activities.ManageFilesActivity;
import com.fantasticfive.shareback.newshareback.beans.DirContentsBean;

import java.util.ArrayList;
import java.util.HashSet;

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
    HashSet<View> selectedViews = new HashSet<>();
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
            holder.imgOptions = (ImageButton) vi.findViewById(R.id.list_options);
            if(!isFile(position)){
                holder.fileImage.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.folder_icon_small));
            }

            holder.imgOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(activity, "Clicked", Toast.LENGTH_SHORT).show();
                    showOptions(view);
                }
            });

            vi.setTag(holder);
            vi.setId(position);
        }
        else{
            holder = (ViewHolder) vi.getTag();
        }

        holder.fileName.setText(getContent(position));
        vi.setOnClickListener(this);
        vi.setOnLongClickListener(this);
        return vi;
    }

    class ViewHolder{
        ImageView fileImage;
        TextView fileName;
        ImageButton imgOptions;
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
            toggleSelection(view);
        }
        Log.e("My Tag", "Position: "+ view.getId());
    }

    public boolean isFile(int position){
        return (position < dirsSize) ? false : true;
    }

    @Override
    public boolean onLongClick(View view) {
        Toast.makeText(activity, "Long Clicked", Toast.LENGTH_SHORT).show();
        activity.startActionMode(new ActionBarCallBack());
        isLongClickEnabled = true;
        view.setSelected(true);
        return false;
    }

    private void toggleSelection(View view){
        if(view.isSelected()){
            selectedViews.remove(view);
            view.setSelected(false);
        }
        else{
            selectedViews.add(view);
            view.setSelected(true);
        }
    }

    private void showOptions(View anchor){
        PopupMenu popup = new PopupMenu(activity, anchor);
        popup.getMenuInflater().inflate(R.menu.file_operations, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                activity.onOptionsItemSelected(item);
                return false;
            }
        });
        popup.show();
    }

    public interface Callback{

    }

    class ActionBarCallBack implements ActionMode.Callback {

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            switch(item.getItemId()){
                case R.id.delete:
                    Toast.makeText(activity, "Delete "+selectedViews.size()+" Items", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.copy:
                    Toast.makeText(activity, "Copy "+selectedViews.size()+" Items" , Toast.LENGTH_SHORT).show();
            }
            // TODO Auto-generated method stub
            mode.finish();
            return false;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // TODO Auto-generated method stub
            mode.getMenuInflater().inflate(R.menu.multiple_file_menu, menu);
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            // TODO Auto-generated method stub
            for(View view: selectedViews){
                view.setSelected(false);
            }
            selectedViews.clear();
            isLongClickEnabled = false;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            // TODO Auto-generated method stub

            mode.setTitle("Choose Files");
            return false;
        }
    }
}