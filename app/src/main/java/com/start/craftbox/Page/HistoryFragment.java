package com.start.craftbox.Page;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.start.craftbox.Events.FileSelectedEvent;
import com.start.craftbox.R;
import com.start.craftbox.Tools.TgaDecoder;
import com.start.craftbox.Views.AdvancedPixelEditorView;
import com.start.craftbox.Views.Palette.ColorPicker;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.io.InputStream;

public class HistoryFragment extends Fragment {
    public HistoryFragment() {}
    AdvancedPixelEditorView pixelEditorView;
    Bitmap bitmap;
    Drawable pencil;
    Drawable eye;

    private final ActivityResultLauncher<String[]> filePickerLauncher = registerForActivityResult(new ActivityResultContracts.OpenDocument(), uri -> {
        if (uri != null) {
            EventBus.getDefault().post(new FileSelectedEvent(uri));
        }
    });

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onFileSelected(FileSelectedEvent event) {
        Context context = getContext();
        if (context == null) return;
        try (InputStream is = context.getContentResolver().openInputStream(event.uri)) {
            byte[] bytes = TgaDecoder.inputStreamToByteArray(is);
            Bitmap bmp = TgaDecoder.decodeTGA(bytes);
            final Bitmap mutableBmp = bmp.copy(Bitmap.Config.ARGB_8888, true);
            new Handler(Looper.getMainLooper()).post(() -> {
                pixelEditorView.setImage(mutableBmp);
            });
        } catch (IOException e) {
            Log.e("DebugError", "文件处理失败", e);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.pixel_editor_layout, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onViewCreated(@NonNull View root, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);
        pixelEditorView = root.findViewById(R.id.pixel_editor);
        TextView tips = root.findViewById(R.id.tips);
        String rawtips = "Tips: 按住按钮可以查看功能介绍";
        MaterialButton select = root.findViewById(R.id.btn_load);
        select.setOnLongClickListener(view -> {
            tips.setText("选择图片文件");
            return true;
        });
        select.setOnClickListener(view -> {
            filePickerLauncher.launch(new String[]{"*/*"});
        });

        MaterialButton undo = root.findViewById(R.id.btn_undo);
        undo.setOnLongClickListener(view -> {
            tips.setText("撤销上一步");
            return true;
        });
        undo.setOnClickListener(view -> {
            pixelEditorView.undo();
        });

        MaterialButton edmode = root.findViewById(R.id.btn_edmode);
        edmode.setOnLongClickListener(view -> {
            tips.setText("切换编辑模式");
            return true;
        });
        edmode.setOnClickListener(view -> {
            if (pencil == null || eye == null) {
                pencil = AppCompatResources.getDrawable(requireContext(), R.drawable.pencil);
                eye = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_eye);
            }
            if (pixelEditorView.getEditMode() == AdvancedPixelEditorView.EditMode.DRAW) {
                pixelEditorView.setEditMode(AdvancedPixelEditorView.EditMode.PAN);
                edmode.setIcon(eye);
            } else {
                pixelEditorView.setEditMode(AdvancedPixelEditorView.EditMode.DRAW);
                edmode.setIcon(pencil);
            }
        });

        MaterialButton tools = root.findViewById(R.id.btn_tools);
        tools.setOnLongClickListener(view -> {
            tips.setText("选择工具");
            return true;
        });
        tools.setOnClickListener(view -> {
            //pixelEditorView.setToolMode()
        });

        ColorPicker colorPicker = new ColorPicker(requireContext(), pixelEditorView.getSelectedColor());
        MaterialButton palette = root.findViewById(R.id.btn_palette);
        palette.setOnLongClickListener(view -> {
            tips.setText("选择画笔颜色");
            return true;
        });
        palette.setOnClickListener(view -> {
            colorPicker.show(color -> {
                pixelEditorView.setPaintColor(color);
            });
        });

        MaterialButton save = root.findViewById(R.id.btn_save);
        save.setOnLongClickListener(view -> {
            tips.setText("保存图片");
            return true;
        });
        save.setOnClickListener(view -> {
            //pixelEditorView.save();
        });


    }
}