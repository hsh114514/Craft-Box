package com.start.craftbox.Views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;

public class EditorWindow {
    public String name;
    public AdvancedPixelEditorView editorView; // 每个窗口拥有独立的 View 实例
    public long lastCloseClickTime = 0;

    public EditorWindow(Context context, String name) {
        this.name = name;
        AdvancedPixelEditorView ev = new AdvancedPixelEditorView(context);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0,1);
        ev.setLayoutParams(lp);
        ev.setBackgroundColor(0xff444444);
        this.editorView = ev;
        this.editorView.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
    }
}