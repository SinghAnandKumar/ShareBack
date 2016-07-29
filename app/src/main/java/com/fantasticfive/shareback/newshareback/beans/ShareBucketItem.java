package com.fantasticfive.shareback.newshareback.beans;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import com.fantasticfive.shareback.newshareback.Constants;

/**
 * Created by sagar on 07-Jul-16.
 */
public class ShareBucketItem {
    Drawable thumbnail;
    String fileName;
    String filePath;
    int pageNo = 1;
    View view = null;
    boolean downloadFlag = false;

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public Drawable getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Drawable thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getAbsolutePath() { return Constants.DIR_ROOT + filePath; }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public boolean getDownloadFlag() {
        return downloadFlag;
    }

    public void setDownloadFlag(boolean downloadFlag) {
        this.downloadFlag = downloadFlag;
    }
}
