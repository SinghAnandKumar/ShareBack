package com.fantasticfive.shareback.newshareback.helpers;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fantasticfive.shareback.R;
import com.fantasticfive.shareback.beans.BucketItem;
import com.fantasticfive.shareback.newshareback.ShareBucket;
import com.fantasticfive.shareback.newshareback.utils.FileRenderer;

import java.io.File;
import java.util.LinkedHashSet;

/**
 * Created by sagar on 24/7/16.
 */
public class PdfViewHelper implements FileRenderer.PdfViewCallback{

    ShareBucket bucket;
    FileRenderer renderer;
    PdfHelperCallback callback;

    Activity activity;
    LinearLayout pdfParent;
    LinearLayout thumbnailParent;

    public PdfViewHelper(Activity activity, ShareBucket bucket, PdfHelperCallback callback){

        this.pdfParent = (LinearLayout) activity.findViewById(R.id.fullscreen_content);
        this.thumbnailParent = (LinearLayout) activity.findViewById(R.id.horizontalScrollview);
        this.activity = activity;
        this.callback = callback;
        this.bucket = bucket;

        renderer = new FileRenderer(activity, this);

    }

    public void addFile(String relLocation){

        if(!bucket.contains(relLocation)) {
            //add to bucket and thumbnail
            BucketItem item = new BucketItem();
            item.setFileName((new File(relLocation)).getName());
            item.setFilePath(relLocation);
            View view = addFileThumbnail(item);
            item.setView(view);

            bucket.add(relLocation, item);
            //-- add to bucket and thumbnail
        }
    }

    public void addFileS(String relLocation){

        if(!bucket.contains(relLocation)) {
            //add to bucket and thumbnail
            BucketItem item = new BucketItem();
            item.setFileName((new File(relLocation)).getName());
            item.setFilePath(relLocation);
            bucket.add(relLocation, item);
            //-- add to bucket and thumbnail
        }
    }

    public void removeFile(String file){
        //remove from bucket and thumbnail
        bucket.popFile(file);
        thumbnailParent.removeView(bucket.getView(file));
        //-- remove from bucket and thumbnail
    }

    public void setDownloadFlag(String filePath){
        bucket.setDownloadFlag(filePath);
    }

    private View addFileThumbnail( final BucketItem item){
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.inner_snackbar_item, null);

        ImageView ivThumbnail = (ImageView) view.findViewById(R.id.inner_snackbar_thumbnail);
        TextView tvFileName = (TextView) view.findViewById(R.id.inner_snackbar_filename);

        //Set Image and text
        if(item.getThumbnail() != null){
            ivThumbnail.setImageDrawable(item.getThumbnail());
        }
        tvFileName.setText(item.getFileName());
        //-- Set Image and text

        ivThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isDownloaded(item)) {

                    //Check if current file is changed
                    if(!bucket.getCurrentFile().equals(item.getFilePath()))
                        callback.onFileChanged(item.getFilePath(), item.getPageNo());
                    //-- Check if current file is changed

                    //set current file and render
                    bucket.setCurrentFile(item.getFilePath());
                    renderer.renderS(item.getFilePath(), bucket.getCurrFilePage());
                    //-- set current file and render
                }
                else{
                    Toast.makeText(activity, "Please Wait", Toast.LENGTH_SHORT).show();
                }
            }
        });

        thumbnailParent.addView(view);
        return view;
    }

    public boolean isDownloaded(BucketItem item){
        return item.getDownloadFlag();
    }

    //Event Receiving Methods
    public void onPageChangedS(String fileName, int pageNo){

        if(fileName.equals(bucket.getCurrentFile())){//If Same file then JumpTo page else render
            //renderer.jumpTo(pageNo);
            renderer.renderS(fileName, pageNo);
        }
        else{
            renderer.renderS(fileName, pageNo);
        }

        bucket.setCurrentFile(fileName, pageNo);//Changing page no in bucket
    }

    public void onFileChangedS(String fileName, int pageNo){
        if(bucket.isFileDownloaded(fileName)) {
            //Add to bucket and show
            bucket.setCurrentFile(fileName, pageNo);
            renderer.renderS(fileName, pageNo);
            //-- Add to bucket and show
        }
        else{
            Log.e("My Tag", "File Still Downloading....");
        }
    }

    public void onFilesAddedS(LinkedHashSet<String> arrFiles){
        for(String filePath : arrFiles)
            addFileS(filePath);
    }

    public void onSessionClosedS(){
        bucket.deleteData();
    }
    //-- Event Receiving Methods

    @Override
    public void onPageChanged(int pageNo) {

        //change page in bucket
        bucket.updatePageNo(bucket.getCurrentFile(), pageNo);
        //-- change page in bucket

        //callback to parent
        callback.onPageChanged(bucket.getCurrentFile(), pageNo);
        //-- callback to parent
    }

    public interface PdfHelperCallback{
        void onPageChanged(String filePath, int pageNo);
        void onFileChanged(String filePath, int pageNo);
    }
}
