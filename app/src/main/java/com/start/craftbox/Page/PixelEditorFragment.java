package com.start.craftbox.Page;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.start.craftbox.Events.FileSelectedEvent;
import com.start.craftbox.R;
import com.start.craftbox.Tools.TgaDecoder;
import com.start.craftbox.Views.AdvancedPixelEditorView;
import com.start.craftbox.Views.EditorWindow;
import com.start.craftbox.Views.Palette.ColorPicker;
import com.start.craftbox.databinding.FragmentPixelEditorBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class PixelEditorFragment extends Fragment {
    private FragmentPixelEditorBinding binding;
    private final List<EditorWindow> windows = new ArrayList<>();
    private EditorWindow currentWindow;
    ColorPicker colorPicker;
    Bitmap bitmap;
    String bitmapName;
    Drawable pencil;
    Drawable eye;
    AdvancedPixelEditorView.EditMode curreditMode = AdvancedPixelEditorView.EditMode.PAN;
    ActivityResultLauncher<String[]> filePickerLauncher;
    ActivityResultLauncher<String> outputLauncher;
    boolean isSaved = false;

    private void writeFile(Uri uri, Bitmap bitmap) {
        try {
            OutputStream outputStream = requireContext().getContentResolver().openOutputStream(uri);
            TgaDecoder.encodeTGA(bitmap, outputStream);
            outputStream.close();
            setTips("保存成功!");
            isSaved = true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onFileSelected(FileSelectedEvent event) {
        Context context = getContext();
        if (context == null) return;
        if (!event.postname.equals("imagepost"))return;
        try (InputStream is = context.getContentResolver().openInputStream(event.uri)) {
            //bitmapName = event.uri.getPath().substring(event.uri.getPath().lastIndexOf("/") + 1);
            byte[] bytes = TgaDecoder.inputStreamToByteArray(is);
            Bitmap bmp = TgaDecoder.decodeTGA(bytes);
            final Bitmap mutableBmp = bmp.copy(Bitmap.Config.ARGB_8888, true);
            new Handler(Looper.getMainLooper()).post(() -> {
                currentWindow.editorView.setImage(mutableBmp);
            });
        } catch (IOException e) {
            Log.e("DebugError", "文件处理失败", e);
        }
    }

    private TabLayout getTabLayout() {
        Activity activity = getActivity();
        if (activity == null) return null;
        return activity.findViewById(R.id.editor_tab_layout);
    }

    private void createNewWindow(String name) {
        EditorWindow newWin = new EditorWindow(requireContext(), name);
        windows.add(newWin);
        binding.pixelEditor.addView(newWin.editorView);
        currentWindow = newWin;
        if (colorPicker == null)
            colorPicker = new ColorPicker(requireContext(), currentWindow.editorView.getSelectedColor());
        addWindowTab(newWin);
        int newIndex = windows.size() - 1;
        getTabLayout().getTabAt(newIndex).select();
    }

    private void switchWindow(int index) {
        if (index < 0 || index >= windows.size()) return;
        for (int i = 0; i < windows.size(); i++) {
            windows.get(i).editorView.setVisibility(i == index ? View.VISIBLE : View.GONE);
        }
        currentWindow = windows.get(index);
        currentWindow.editorView.setEditMode(curreditMode);
        setTips("已切换到: " + currentWindow.name);
    }

//    private void updateTabStyle(TabLayout.Tab tab, boolean isSelected) {
//        View customView = tab.getCustomView();
//        if (customView == null) return;
//        MaterialCardView card = customView.findViewById(R.id.tab_card);
//        TextView titleTv = customView.findViewById(R.id.tab_title);
//        if (isSelected) {
//        } else {
//        }
//    }

    private void setTips(String tips) {
        TextView tipsTv = this.binding.tips;
        tipsTv.setText((tips == null) ? "Tips: 按住按钮可以查看功能介绍" : tips);
    }

    private void handleCloseWindow(EditorWindow window, TabLayout.Tab tab) {
        if (window.editorView.isModifying()) {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("未保存的更改")
                    .setMessage("确定要关闭 \"" + window.name + "\" 吗？")
                    .setPositiveButton("保存并关闭", (d, w) -> {
                        outputLauncher.launch("image.tga");
                        if (isSaved) {
                            forceClose(window, tab);
                            isSaved = false;
                        }
                    })
                    .setNegativeButton("直接关闭", (d, w) -> forceClose(window, tab))
                    .setNeutralButton("取消", null).show();
        } else {
            forceClose(window, tab);
        }
    }

    private void forceClose(EditorWindow window, TabLayout.Tab tab) {
        int index = windows.indexOf(window);
        if (index != -1) {
            binding.pixelEditor.removeView(window.editorView);
            windows.remove(index);
            getTabLayout().removeTab(tab);
            if (!windows.isEmpty()) {
                switchWindow(windows.size() - 1);
            }
        }
    }

    private void addWindowTab(EditorWindow window) {
        TabLayout tabLayout = getTabLayout();
        TabLayout.Tab tab = tabLayout.newTab();
        tabLayout.addTab(tab);
        View tabView = LayoutInflater.from(getContext()).inflate(R.layout.item_editor_tab, null);
        TextView titleTv = tabView.findViewById(R.id.tab_title);
        ImageView closeIv = tabView.findViewById(R.id.btn_tab_close);
        MaterialCardView card = tabView.findViewById(R.id.tab_card);

        titleTv.setText(window.name);
        tab.setCustomView(tabView);
        tabView.setPadding(0, 0, 0, 0);

        tabView.setOnLongClickListener(v -> {
            showRenameDialog(window, titleTv);
            return true;
        });

        tabView.setOnClickListener(view -> {
            if (!tab.isSelected()) {
                tab.select();
            }
        });

        closeIv.setOnClickListener(v -> {
            long currentTime = System.currentTimeMillis();
            if (currentTime - window.lastCloseClickTime < 500) {
                handleCloseWindow(window, tab);
            } else {
                window.lastCloseClickTime = currentTime;
                Toast.makeText(getContext(), "再按一次关闭窗口", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showRenameDialog(EditorWindow window, TextView titleTv) {
        EditText et = new EditText(getContext());
        et.setText(window.name);
        new MaterialAlertDialogBuilder(requireContext()).setTitle("重命名窗口").setView(et).setPositiveButton("确定", (d, w) -> {
            window.name = et.getText().toString();
            titleTv.setText(window.name);
        }).show();
    }


    @Override
    public void onViewCreated(@NonNull View root, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);
        binding.pixelEditor.removeAllViews();
        for (EditorWindow win : windows) {
            View parent = (View) win.editorView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(win.editorView);
            }
            binding.pixelEditor.addView(win.editorView);
        }

        TabLayout tabLayout = getTabLayout();
        if (tabLayout != null) {
            tabLayout.clearOnTabSelectedListeners();
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    switchWindow(tab.getPosition());
                    //updateTabStyle(tab, true);
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                    //updateTabStyle(tab, false);
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                }
            });
        }

        MaterialButton select = root.findViewById(R.id.btn_load);
        select.setOnLongClickListener(view -> {
            setTips("选择图片");
            return true;
        });

        select.setOnClickListener(view -> {
            if (currentWindow == null) {
                Toast.makeText(getContext(), "没有选择画板,请先选择一个画板", Toast.LENGTH_SHORT).show();
                return;
            }
            filePickerLauncher.launch(new String[]{"*/*"});
        });

        MaterialButton undo = root.findViewById(R.id.btn_undo);
        undo.setOnLongClickListener(view -> {
            setTips("撤销上一步");
            return true;
        });
        undo.setOnClickListener(view -> {
            if (currentWindow == null) {
                Toast.makeText(getContext(), "没有选择画板,请先选择一个画板", Toast.LENGTH_SHORT).show();
                return;
            }
            currentWindow.editorView.undo();
        });

        MaterialButton edmode = root.findViewById(R.id.btn_edmode);
        edmode.setOnLongClickListener(view -> {
            setTips("切换编辑模式");
            return true;
        });
        edmode.setOnClickListener(view -> {
            if (currentWindow == null) {
                Toast.makeText(getContext(), "没有选择画板,请先选择一个画板", Toast.LENGTH_SHORT).show();
                return;
            }
            if (pencil == null || eye == null) {
                pencil = AppCompatResources.getDrawable(requireContext(), R.drawable.pencil);
                eye = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_eye);
            }
            if (curreditMode == AdvancedPixelEditorView.EditMode.DRAW) {
                curreditMode = AdvancedPixelEditorView.EditMode.PAN;
                currentWindow.editorView.setEditMode(AdvancedPixelEditorView.EditMode.PAN);
                edmode.setIcon(eye);
            } else {
                curreditMode = AdvancedPixelEditorView.EditMode.DRAW;
                currentWindow.editorView.setEditMode(AdvancedPixelEditorView.EditMode.DRAW);
                edmode.setIcon(pencil);
            }
        });

        MaterialButton tools = root.findViewById(R.id.btn_tools);
        tools.setOnLongClickListener(view -> {
            setTips("选择工具");
            return true;
        });
        tools.setOnClickListener(view -> {
            if (currentWindow == null) {
                Toast.makeText(getContext(), "没有选择画板,请先选择一个画板", Toast.LENGTH_SHORT).show();
                return;
            }
            PopupMenu popupMenu = new PopupMenu(getContext(), view);
            popupMenu.getMenuInflater().inflate(R.menu.list1, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.menu_bucket) {
                    currentWindow.editorView.setTool(AdvancedPixelEditorView.Tool.BUCKET);
                } else if (itemId == R.id.menu_pencil) {
                    currentWindow.editorView.setTool(AdvancedPixelEditorView.Tool.PENCIL);
                } else if (itemId == R.id.menu_eraser) {
                    currentWindow.editorView.setTool(AdvancedPixelEditorView.Tool.ERASER);
                }
                return true;
            });
            popupMenu.show();
            //currentWindow.editorView.setTool(AdvancedPixelEditorView.Tool.BUCKET);
        });


        MaterialButton palette = root.findViewById(R.id.btn_palette);
        palette.setOnLongClickListener(view -> {
            setTips("选择画笔颜色");
            return true;
        });
        palette.setOnClickListener(view -> {
            if (currentWindow == null) {
                Toast.makeText(getContext(), "没有选择画板,请先选择一个画板", Toast.LENGTH_SHORT).show();
                return;
            }
            colorPicker.show(color -> {
                currentWindow.editorView.setPaintColor(color);
            });
        });

        MaterialButton save = root.findViewById(R.id.btn_save);
        save.setOnLongClickListener(view -> {
            setTips("保存图片");
            return true;
        });
        save.setOnClickListener(view -> {
            if (currentWindow == null) {
                Toast.makeText(getContext(), "没有选择画板,请先选择一个画板", Toast.LENGTH_SHORT).show();
                return;
            }
            bitmap = currentWindow.editorView.getImage();
            outputLauncher.launch((bitmapName == null) ? "image.tga" : bitmapName);

        });

        setupEditorToolbar(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPixelEditorBinding.inflate(inflater, container, false);

        filePickerLauncher = registerForActivityResult(new ActivityResultContracts.OpenDocument(), uri -> {
            if (uri != null) {
                EventBus.getDefault().post(new FileSelectedEvent(uri, "imagepost"));
            }
        });

        outputLauncher = registerForActivityResult(new ActivityResultContracts.CreateDocument(), uri -> {
            if (uri != null && bitmap != null) {
                writeFile(uri, bitmap);
            }
        });
        return binding.getRoot();
    }

    private void setupEditorToolbar(boolean isEditing) {
        Activity activity = getActivity();
        if (activity == null) return;
        View title = activity.findViewById(R.id.toolbar_title);
        TabLayout tabs = activity.findViewById(R.id.editor_tab_layout);
        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        ImageView addBtn = activity.findViewById(R.id.btn_tab_add);
        addBtn.setOnClickListener(v -> createNewWindow("画板" + (windows.size() + 1)));
        if (isEditing) {
            title.setVisibility(View.GONE);
            tabs.setVisibility(View.VISIBLE);
            addBtn.setVisibility(View.VISIBLE);
        } else {
            title.setVisibility(View.VISIBLE);
            tabs.setVisibility(View.GONE);
            addBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setupEditorToolbar(true);
    }


    @Override
    public void onPause() {
        super.onPause();
        setupEditorToolbar(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        setupEditorToolbar(false);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }


}