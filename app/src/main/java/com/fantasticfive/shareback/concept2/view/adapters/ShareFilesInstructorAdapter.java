package com.fantasticfive.shareback.concept2.view.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fantasticfive.shareback.R;
import com.fantasticfive.shareback.concept2.bean.SharedFile;
import com.fantasticfive.shareback.concept2.bean.UploadingFile;
import com.fantasticfive.shareback.concept2.exception.NotImplementedException;
import com.fantasticfive.shareback.concept2.helper.FirebaseUploadHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by sagar on 18/2/17.
 */
public class ShareFilesInstructorAdapter extends BaseAdapter implements FirebaseUploadHelper.Callback{

    Context context;
    ArrayList<SharedFile> fileList;
    FirebaseUploadHelper uploadHelper;
    HashMap<SharedFile, Uri> map;
    Callback callback;
    public ShareFilesInstructorAdapter(Context context, ArrayList<SharedFile> fileList, Callback callback){
        this.context = context;
        this.fileList = fileList;
        this.callback = callback;
        uploadHelper = new FirebaseUploadHelper(context, this);
        map = new HashMap<>();
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
            view = inflater.inflate(R.layout.c2_inner_shared_files, null);
            holder.name = (TextView) view.findViewById(R.id.session_name);
            view.setTag(holder);
        }
        else{
            holder = (ViewHolder) view.getTag();
        }
        SharedFile file = fileList.get(position);
        holder.name.setText(file.getName());
        if((file instanceof UploadingFile) && !map.containsKey(file)){
            uploadHelper.upload((UploadingFile)file,view);
            map.put(file, ((UploadingFile)file).getLocalUri());
        }
        else{
            ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setIndeterminate(false);
            progressBar.setProgress(100);
        }

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try{
                            throw  new NotImplementedException();
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
        return view;
    }

    @Override
    public void onUploadProgress(View view, double pcage) {
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress((int)pcage);
        if(pcage>99)
            progressBar.setIndeterminate(true);
    }

    @Override
    public void onUploadComplete(View view, String downloadPath) {
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        progressBar.setIndeterminate(false);
        progressBar.setProgress(100);
        callback.onUploadComplete(downloadPath);
    }

    class ViewHolder{
        TextView name;
    }

    public interface Callback{
        void onUploadComplete(String downloadPath);
    }
}
