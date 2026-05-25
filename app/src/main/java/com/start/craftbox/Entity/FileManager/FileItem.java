package com.start.craftbox.Entity.FileManager;

import java.io.File;

public class FileItem {
    private String name;
    private String path;
    private boolean isDirectory;
    private long size;
    private long lastModified;

    public FileItem(File file) {
        this.name = file.getName();
        this.path = file.getAbsolutePath();
        this.isDirectory = file.isDirectory();
        this.size = file.length();
        this.lastModified = file.lastModified();
    }


    public String getName() { return name; }
    public String getPath() { return path; }
    public boolean isDirectory() { return isDirectory; }
    public long getSize() { return size; }
    public long getLastModified() {return lastModified;}
    public void setLastModified(long lastModified) {this.lastModified = lastModified;}
}