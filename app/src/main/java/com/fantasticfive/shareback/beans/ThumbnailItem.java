package com.fantasticfive.shareback.beans;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

/**
 * Created by sagar on 07-Jul-16.
 */
public class ThumbnailItem {
    Drawable thumbnail;
    String fileName;
    String filePath;

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
}
