package com.fantasticfive.shareback.concept2.bean;

import android.net.Uri;

/**
 * Created by sagar on 5/3/17.
 */
public class UploadingFile extends SharedFile {
    boolean uploaded = false;
    Uri localUri;

    public boolean getUploaded() {
        return uploaded;
    }

    public void setUploaded(boolean uploaded) {
        this.uploaded = uploaded;
    }

    public Uri getLocalUri() {
        return localUri;
    }

    public void setLocalUri(Uri localUri) {
        this.localUri = localUri;
    }
}
