package com.fantasticfive.shareback.concept2.view.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fantasticfive.shareback.R;
import com.fantasticfive.shareback.concept2.Constants;
import com.fantasticfive.shareback.concept2.bean.SharedFile;
import com.fantasticfive.shareback.concept2.helper.FirebaseDownloadHelper;
import com.fantasticfive.shareback.concept2.util.FileUtils;

import java.util.ArrayList;

/**
 * Created by sagar on 18/2/17.
 */
public class ShareFilesStudentAdapter extends BaseAdapter implements FirebaseDownloadHelper.Callback{

    Activity context;
    ArrayList<SharedFile> fileList;
    FirebaseDownloadHelper downloadHelper;
    public ShareFilesStudentAdapter(Activity context, ArrayList<SharedFile> fileList){
        this.context = context;
        this.fileList = fileList;
        downloadHelper = new FirebaseDownloadHelper(context, this);
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
    public View getView(final int position, View view, ViewGroup viewGroup) {
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
        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        if(FileUtils.exists(file.getName())) {
            progressBar.setProgress(100);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setIndeterminate(false);
        }
        else{
            progressBar.setProgress(0);
            progressBar.setVisibility(View.INVISIBLE);
            progressBar.setIndeterminate(false);
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedFile sharedFile = fileList.get(position);
                String name = sharedFile.getName();
                progressBar.setVisibility(View.VISIBLE);
                if(FileUtils.exists(name)){
                    progressBar.setProgress(100);
                    FileUtils.openFile(context, name);
                }
                else {
                    progressBar.setProgress(0);
                    downloadHelper.download(sharedFile, view);
                }
            }
        });
        return view;
    }

    @Override
    public void ondProgressUpdate(double downloadPC, View view) {
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        int progress = (int)Math.floor(downloadPC);
        progressBar.setProgress(progress);
        /*if (progress > 99)
            progressBar.setIndeterminate(true);*/
    }

    @Override
    public void onComplete(View view) {
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        progressBar.setIndeterminate(false);
        progressBar.setProgress(100);
        progressBar
                .getIndeterminateDrawable()
                .setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
    }

    @Override
    public void onFailure(SharedFile sharedFile, View view, Exception e) {
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.INVISIBLE);
        Toast.makeText(context, "Cannot Download: "+sharedFile.getName()+"\n"+e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    class ViewHolder{
        TextView name;
    }
}
