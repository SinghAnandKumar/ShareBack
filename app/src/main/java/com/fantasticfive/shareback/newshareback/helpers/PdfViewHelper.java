package com.fantasticfive.shareback.newshareback.helpers;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fantasticfive.shareback.R;
import com.fantasticfive.shareback.newshareback.activities.FileViewInstructor;
import com.fantasticfive.shareback.newshareback.beans.ShareBucketItem;
import com.fantasticfive.shareback.newshareback.ShareBucket;
import com.fantasticfive.shareback.newshareback.fileoperation.FileRenderer;

import java.io.File;
import java.util.LinkedHashSet;

/**
 * Created by sagar on 24/7/16.
 */
public class PdfViewHelper implements FileRenderer.PdfViewCallback{
    ShareBucket bucket;
    FileRenderer renderer;
    PdfHelperCallback callback;
    MyAdapter adapter;

    Activity activity;
    LinearLayout pdfParent;
    ListView listView;
    DrawerLayout drawer;

    public PdfViewHelper(Activity activity, ShareBucket bucket, PdfHelperCallback callback){

        this.pdfParent = (LinearLayout) activity.findViewById(R.id.fullscreen_content);
        this.listView = (ListView) activity.findViewById(R.id.nav_list);
        this.drawer = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        this.activity = activity;
        this.callback = callback;

        this.bucket = bucket;
        renderer = new FileRenderer(activity, this);
        adapter = new MyAdapter();
        listView.setAdapter(adapter);
    }

    public void addFile(String relLocation){

        if(!bucket.contains(relLocation)) {
            //add to bucket and thumbnail
            ShareBucketItem item = new ShareBucketItem();
            item.setFileName((new File(relLocation)).getName());
            item.setFilePath(relLocation);

            bucket.add(relLocation, item);

            adapter.notifyDataSetChanged();
            //-- add to bucket and thumbnail
        }
    }

    public void addFileS(String relLocation){

        if(!bucket.contains(relLocation)) {
            //add to bucket and thumbnail
            ShareBucketItem item = new ShareBucketItem();
            item.setFileName(   (new File(relLocation)).getName()   );
            item.setFilePath(relLocation);
            bucket.add(relLocation, item);
            //-- add to bucket and thumbnail
        }
    }

    public void removeFile(String file){
        //remove from bucket and thumbnail
        bucket.popFile(file);
        //-- remove from bucket and thumbnail
    }

    public void setDownloadFlag(String filePath){
        bucket.setDownloadFlag(filePath);
    }

    public boolean isDownloaded(ShareBucketItem item){
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

        //callback to parent
        callback.onPageChanged(bucket.getCurrentFile(), pageNo);

    }

    private void fileClicked(ShareBucketItem item){
        if(isDownloaded(item)) {

            //Check if current file is changed
            if(!bucket.getCurrentFile().equals(item.getFilePath()))
                callback.onFileChanged(item.getFilePath(), item.getPageNo());

            //set current file and render
            bucket.setCurrentFile(item.getFilePath());
            renderer.renderS(item.getFilePath(), bucket.getCurrFilePage());

            drawer.closeDrawer(Gravity.LEFT);
        }
        else{
            Toast.makeText(activity, "Please Wait", Toast.LENGTH_SHORT).show();
        }
    }

    class MyAdapter extends BaseAdapter {
        View prevView = null;
        @Override
        public int getCount() {
            return bucket.getFiles().size()+1;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {

            if(view == null) {
                View v = LayoutInflater.from(activity)
                        .inflate(R.layout.inner_drawer_file_view_instructor, null);
                TextView tv = (TextView) v.findViewById(R.id.text_file);
                ImageView img = (ImageView) v.findViewById(R.id.img_file);

                if(position == 0){
                    tv.setText("Add Files");
                    img.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_library_add_white_24dp));
                    img.setColorFilter(R.color.dark_grey);
                    v.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            drawer.closeDrawer(Gravity.LEFT);
                            ((FileViewInstructor)activity).showAddFileDialog();
                        }
                    });
                    return v;
                }

                final ShareBucketItem item = bucket.getItemAt(position-1);
                tv.setText(item.getFileName());
                img.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.logo_pdf));

                v.setBackground(ContextCompat.getDrawable(activity, R.drawable.ripple_selector));
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setSelected(view);
                        fileClicked(item);
                    }
                });
                return v;
            }
            return view;
        }

        private void setSelected(View view){
            if(prevView != null){
                prevView.setEnabled(true);
            }

            view.setEnabled(false);
            prevView = view;
        }
    }

    public interface PdfHelperCallback{
        void onPageChanged(String filePath, int pageNo);
        void onFileChanged(String filePath, int pageNo);
    }
}
