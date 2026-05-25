package com.start.craftbox.Entity.FileManager;

import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.start.craftbox.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FilePanel {
    public File currentDir;
    public RecyclerView recyclerView;
    public FileAdapter adapter;
    public Toolbar toolbar;
    public List<FileItem> fileList = new ArrayList<>();
    public FilePanel(RecyclerView rv, Toolbar tb) {
        this.recyclerView = rv;
        this.toolbar = tb;
    }

    public void updateDir(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory()) return;
        this.currentDir = dir;
        TextView tvPath = toolbar.findViewById(R.id.file_manager_toolbar_tv_path);
        tvPath.setText(dir.getAbsolutePath());
        //this.tvPath.setText(dir.getAbsolutePath());
        File[] files = dir.listFiles();
        fileList.clear();
        if (files != null) {
            Arrays.sort(files, (f1, f2) -> {
                boolean isDir1 = f1.isDirectory();
                boolean isDir2 = f2.isDirectory();
                if (isDir1 != isDir2) {
                    return isDir1 ? -1 : 1;
                }
                return Long.compare(f2.lastModified(), f1.lastModified());
            });
            for (File file : files) {
                if (!file.getName().startsWith(".")) {
                    fileList.add(new FileItem(file));
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}