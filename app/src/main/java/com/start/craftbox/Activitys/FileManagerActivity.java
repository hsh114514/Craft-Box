package com.start.craftbox.Activitys;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.start.craftbox.Entity.FileManager.FileAdapter;
import com.start.craftbox.Entity.FileManager.FileItem;
import com.start.craftbox.Entity.FileManager.FilePanel;
import com.start.craftbox.R;

import java.io.File;
import java.util.List;

public class FileManagerActivity extends AppCompatActivity {
    FilePanel filePanel;
    Toolbar toolbar;
    ExtendedFloatingActionButton fabAction;
    long lastBackPressedTime = 0;
    public static final String EXTRA_CHOICE_MODE = "extra_choice_mode";
    public static final int MODE_MANAGER = 0;
    public static final int MODE_PICK_SINGLE = 1;
    public static final int MODE_PICK_MULTIPLE = 2;
    public static final int MODE_PICK_FOLDER = 3;
    public static final String EXTRA_RESULT_PATHS = "extra_result_paths";
    int currentMode = MODE_MANAGER;
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

        currentMode = getIntent().getIntExtra(EXTRA_CHOICE_MODE, MODE_MANAGER);

        File primaryStorage = Environment.getExternalStorageDirectory();
        toolbar = findViewById(R.id.file_manager_toolbar);
        fabAction = findViewById(R.id.fab_action);

        TextView tvPath = toolbar.findViewById(R.id.file_manager_toolbar_tv_path);
        tvPath.setText(primaryStorage.getAbsolutePath());

        RecyclerView recyclerView = findViewById(R.id.rv_file_list);
        filePanel = new FilePanel(recyclerView, toolbar);

        filePanel.adapter = new FileAdapter(filePanel.fileList);
        filePanel.adapter.setChoiceMode(currentMode);
        filePanel.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        filePanel.recyclerView.setAdapter(filePanel.adapter);

        filePanel.updateDir(primaryStorage);

        setupFabAndAdapterMode();

        filePanel.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        if (!fabAction.isExtended()) {
                            fabAction.extend();
                        }
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                    case RecyclerView.SCROLL_STATE_SETTLING:

                        if (fabAction.isExtended()) {
                            fabAction.shrink();
                        }
                        break;
                }
            }
        });

        filePanel.adapter.setOnItemClickListener((item, position) -> {
            if (item.isDirectory()) {
                filePanel.updateDir(new File(item.getPath()));
                updateFabText();
                if (currentMode == MODE_PICK_FOLDER) {
                    fabAction.setText("选择: " + item.getName());
                }
            } else {
                if (currentMode == MODE_PICK_SINGLE) {
                    finishWithResult(new String[]{item.getPath()});
                } else if (currentMode == MODE_PICK_MULTIPLE) {
                    int selectCount = filePanel.adapter.getSelectedItems().size();
                    fabAction.setText(String.format("确认选择 (%d)", selectCount));
                } else {
                    Toast.makeText(this, "打开文件: " + item.getName(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (currentMode == MODE_MANAGER) {
            filePanel.adapter.setOnItemLongClickListener((item, position) -> {
                showActionMenu(item);
            });

            ItemTouchHelper.SimpleCallback swipeCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    int position = viewHolder.getBindingAdapterPosition();
                    filePanel.adapter.toggleItemSelection(position);
                    int selectCount = filePanel.adapter.getSelectedItems().size();
                    if (selectCount == 0) {
                        filePanel.adapter.setMultiSelectMode(false);
                        filePanel.adapter.notifyDataSetChanged();
                    }
                }
            };
            new ItemTouchHelper(swipeCallback).attachToRecyclerView(filePanel.recyclerView);
        }

        toolbar.setNavigationOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (filePanel.adapter.isMultiSelectMode()) {
                    filePanel.adapter.setMultiSelectMode(false);
                    filePanel.adapter.notifyDataSetChanged();
                    toolbar.setTitle(filePanel.currentDir.getName());
                    return;
                }

                File rootDir = Environment.getExternalStorageDirectory();
                if (filePanel != null && filePanel.currentDir != null) {
                    File currentDir = filePanel.currentDir;
                    if (!currentDir.equals(rootDir) && currentDir.getParentFile() != null) {
                        filePanel.updateDir(currentDir.getParentFile());
                        updateFabText();
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


    private void setupFabAndAdapterMode() {
        switch (currentMode) {
            case MODE_PICK_SINGLE:
                fabAction.setVisibility(View.GONE);
                break;

            case MODE_PICK_MULTIPLE:
                filePanel.adapter.setMultiSelectMode(true);
                fabAction.setText("确认选择 (0)");
                fabAction.setIconResource(R.drawable.check_circle_24px);
                fabAction.setOnClickListener(v -> {
                    List<File> selected = filePanel.adapter.getSelectedFiles();
                    if (selected.isEmpty()) {
                        Toast.makeText(this, "请至少选择一个文件", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String[] paths = new String[selected.size()];
                    for (int i = 0; i < selected.size(); i++) {
                        paths[i] = selected.get(i).getAbsolutePath();
                    }
                    finishWithResult(paths);
                });
                break;

            case MODE_PICK_FOLDER:
                fabAction.setText("选择当前文件夹");
                fabAction.setIconResource(R.drawable.folder_open_24px);
                fabAction.setOnClickListener(v -> {
                    if (filePanel.currentDir != null) {
                        finishWithResult(new String[]{filePanel.currentDir.getAbsolutePath()});
                    }
                });
                break;

            case MODE_MANAGER:
            default:
                fabAction.setText("功能菜单");
                fabAction.setIconResource(R.drawable.menu);
                fabAction.setOnClickListener(v -> showGlobalManagerMenu());
                break;
        }
    }

    private void updateFabText() {
        if (currentMode == MODE_PICK_FOLDER) {
            if (filePanel != null && filePanel.currentDir != null) {
                File rootDir = Environment.getExternalStorageDirectory();
                if (filePanel.currentDir.equals(rootDir)) {
                    fabAction.setText("选择当前文件夹");
                } else {
                    fabAction.setText("选择: " + filePanel.currentDir.getName());
                }
            }
        }
    }

    private void showGlobalManagerMenu() {
        Toast.makeText(this, "弹出功能菜单：创建新文件 / 打开书签 / 排序设置", Toast.LENGTH_SHORT).show();
    }

    private void showActionMenu(FileItem item) {
        Toast.makeText(this, "长按操作: " + item.getName(), Toast.LENGTH_SHORT).show();
    }

    private void finishWithResult(String[] paths) {
        Intent data = new Intent();
        data.putExtra(EXTRA_RESULT_PATHS, paths);
        setResult(RESULT_OK, data);
        finish();
    }
}