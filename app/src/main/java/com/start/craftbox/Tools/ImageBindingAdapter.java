package com.start.craftbox.Tools;

import android.widget.ImageView;
import androidx.databinding.BindingAdapter;
public class ImageBindingAdapter {
    @BindingAdapter("android:src")
    public static void setImageResource(ImageView imageView, int resource) {
        if (resource != 0) {
            imageView.setImageResource(resource);
        }
    }
}