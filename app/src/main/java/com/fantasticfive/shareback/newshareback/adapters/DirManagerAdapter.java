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

        /*if(convertView == null){*/
            vi = inflater.inflate(R.layout.inner_dirmanage, null);
            holder = new ViewHolder();
            holder.fileName = (TextView) vi.findViewById(R.id.dir_name);
            holder.fileImage = (ImageView) vi.findViewById(R.id.file_dir_icon);
            holder.imgOptions = (ImageButton) vi.findViewById(R.id.list_options);
            if(!isFile(position)){
                Log.e("My Tag", getContent(position)+" is Dir");
                holder.fileImage.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.folder_icon_small));
            }else {
                Log.e("My Tag", getContent(position)+" is File");
            }

            holder.imgOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!isLongClickEnabled)
                        showOptions(position, view);
                }
            });

            vi.setTag(holder);
            vi.setId(position);
        /*}
        else{
            holder = (ViewHolder) vi.getTag();
        }*/

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

    /**
     * This method is used to return content
     * @param position
     * position on item in list
     * @return
     * Content based on position
     */
    private String getContent(int position){
        return ((position < dirsSize) ? dirs.get(position) : files.get(position - dirsSize));
    }

    public boolean isFile(int position){
        return (position < dirsSize) ? false : true;
    }

    @Override
    public void onClick(View view) {
        String name = ((TextView)view.findViewById(R.id.dir_name)).getText().toString();
        if(isLongClickEnabled){
            toggleSelection(view);
        }
        else if(!isFile(view.getId())){
            callback.onDirClicked(name);
        }
        Log.e("My Tag", "Position: "+ view.getId() +"Left Actions");
    }

    @Override
    public boolean onLongClick(View view) {
        Log.e("My Tag", "Long Clicked Position: "+view.getId());
        activity.startActionMode(new ActionBarCallBack());
        view.setSelected(false);
        isLongClickEnabled = true;
        return false;
    }

    /**
     * Toggle UI Selection
     * @param view
     */
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

    /**
     * Individual File Operation Menu
     * @param position
     * Position in List
     * @param anchor
     */
    private void showOptions(final int position, View anchor){
        PopupMenu popup = new PopupMenu(activity, anchor);
        popup.getMenuInflater().inflate(R.menu.file_operations, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                callback.onListOptionSelected(getContent(position), item);
                return false;
            }
        });
        popup.show();
    }

    class ActionBarCallBack implements ActionMode.Callback {

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            ArrayList<String> alFileName = new ArrayList<>();
            for(View view: selectedViews){
                alFileName.add(((TextView)view.findViewById(R.id.dir_name)).getText().toString());
            }
            callback.onMultiOptionSelected(alFileName, item);

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

    public interface Callback{
        void onListOptionSelected(String fileName, MenuItem item);
        void onDirClicked(String dirName);
        void onMultiOptionSelected(ArrayList<String> filesName, MenuItem item);
    }
}
