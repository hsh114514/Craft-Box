package com.start.craftbox.Entity;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.icu.text.SimpleDateFormat;

import java.util.Date;

public class MdDocEntity {
    public MdDocEntity() {}
    public MdDocEntity(String title, Drawable image, long time, String author, String description, String[] tags, String path) {
        this.title = title;
        this.image = image;
        this.time = time;
        this.author = author;
        this.description = description;
        this.tags = tags;
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }

    public long getTime() {
        return time;
    }

    @SuppressLint("SimpleDateFormat")
    public String getTimeString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(new Date(time));
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    private String title;
    private Drawable image;
    private long time;
    private String author;
    private String description;
    private String[] tags;
    private String path;


}
