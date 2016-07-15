package com.fantasticfive.shareback.newshareback.beans;

import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Created by sagar on 13/7/16.
 */
public class DirContentsBean {

    private TreeSet<String> dirs = new TreeSet<>();
    private TreeSet<String> files = new TreeSet<>();

    public void addDir(String dir) {
        dirs.add(dir);
    }

    public void addFile(String file) {
        files.add(file);
    }

    public TreeSet<String> getDirs() {
        return dirs;
    }

    public TreeSet<String> getFiles() {
        return files;
    }
}
