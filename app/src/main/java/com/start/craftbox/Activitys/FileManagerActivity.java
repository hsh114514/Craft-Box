package com.start.craftbox.Activitys;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Environment;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.start.craftbox.Entity.FileManager.FileAdapter;
import com.start.craftbox.Entity.FileManager.FileItem;
import com.start.craftbox.Entity.FileManager.FilePanel;
import com.start.craftbox.R;

import java.io.File;

public class FileManagerActivity extends AppCompatActivity {
    FilePanel leftPanel;
    FilePanel rightPanel;
    FilePanel activePanel;
    Toolbar toolbar;
    long lastBackPressedTime = 0;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_file_manager);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.file_manager_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        File primaryStorage = Environment.getExternalStorageDirectory();
        toolbar = findViewById(R.id.file_manager_toolbar);
        TextView tvPath = toolbar.findViewById(R.id.file_manager_toolbar_tv_path);
        tvPath.setText(primaryStorage.getAbsolutePath());

        leftPanel = new FilePanel(findViewById(R.id.rv_file_left),toolbar);
        rightPanel = new FilePanel(findViewById(R.id.rv_file_right),toolbar);

        leftPanel.adapter = new FileAdapter(leftPanel.fileList);
        leftPanel.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        leftPanel.recyclerView.setAdapter(leftPanel.adapter);

        rightPanel.adapter = new FileAdapter(rightPanel.fileList);
        rightPanel.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        rightPanel.recyclerView.setAdapter(rightPanel.adapter);
        rightPanel.updateDir(primaryStorage);
        leftPanel.updateDir(primaryStorage);

        findViewById(R.id.rv_file_left).setOnTouchListener((v, event) -> {
            if (activePanel != leftPanel) activePanel = leftPanel;
            tvPath.setText(activePanel.currentDir.getAbsolutePath());
            return false;
        });

        findViewById(R.id.rv_file_right).setOnTouchListener((v, event) -> {
            if (activePanel != rightPanel) activePanel = rightPanel;
            tvPath.setText(activePanel.currentDir.getAbsolutePath());
            return false;
        });

        leftPanel.adapter.setOnItemClickListener((item, position) -> {
            activePanel = leftPanel;
            if (item.isDirectory()) {
                leftPanel.updateDir(new File(item.getPath()));
            }
        });

        leftPanel.adapter.setOnItemLongClickListener((item, position) -> {
            activePanel = leftPanel;
            showActionMenu(item, rightPanel.currentDir);
        });

        rightPanel.adapter.setOnItemClickListener((item, position) -> {
            activePanel = rightPanel;
            if (item.isDirectory()) {
                rightPanel.updateDir(new File(item.getPath()));
            }
        });

        rightPanel.adapter.setOnItemLongClickListener((item, position) -> {
            activePanel = rightPanel;
            showActionMenu(item, leftPanel.currentDir);
        });


        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                File rootDir = Environment.getExternalStorageDirectory();

                if (activePanel != null) {
                    File currentDir = activePanel.currentDir;
                    if (currentDir != null && !currentDir.equals(rootDir) && currentDir.getParentFile() != null) {
                        activePanel.updateDir(currentDir.getParentFile());
                        return;
                    }
                }
                long currentTime = System.currentTimeMillis();

                if (currentTime - lastBackPressedTime > 2000) {
                    Toast.makeText(FileManagerActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
                    lastBackPressedTime = currentTime;
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }

            }
        });
    }

    private void showActionMenu(FileItem item, File targetDir) {
        // TODO: 显示操作菜单
        Toast.makeText(this, item.getName(), Toast.LENGTH_SHORT).show();
    }
}