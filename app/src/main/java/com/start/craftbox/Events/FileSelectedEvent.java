package com.start.craftbox.Events;

import android.net.Uri;

public class FileSelectedEvent {
    public final Uri uri;
    public final String postname;
    public FileSelectedEvent(Uri uri, String postname) { this.uri = uri; this.postname = postname; }
    public FileSelectedEvent(Uri uri) { this(uri, "null"); }
}