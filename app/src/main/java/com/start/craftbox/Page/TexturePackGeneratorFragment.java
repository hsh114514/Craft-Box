package com.start.craftbox.Page;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.start.craftbox.Events.FileSelectedEvent;
import com.start.craftbox.R;
import com.start.craftbox.Tools.TexturePack.ItemsAtlasGenerator;
import com.start.craftbox.Tools.TexturePack.ZipFileLister;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class TexturePackGeneratorFragment extends Fragment {
    private static final int REQUEST_CODE_SELECT_FILE = 1;
    private static final int REQUEST_STORAGE_PERMISSION = 100;
    private TextView tvSelectedFile, tvOutputPath, tvStatus;
    private Button btnSelectFile, btnGenerate, btnListPaths;
    private File selectedFile;
    private File outputDir;
    ActivityResultLauncher<String[]> filePickerLauncher;
    ActivityResultLauncher<String> outputLauncher;
    private String getPathFromUri(Uri uri) {
        if ("file".equals(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    private void generateItemsAtlas(final File zipFile) {
        final ProgressDialog progress = new ProgressDialog(requireContext());
        progress.setMessage("正在生成物品图集...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setCancelable(false);
        progress.show();
        new Thread(() -> {
            try {
                ItemsAtlasGenerator generator = new ItemsAtlasGenerator(requireContext(), outputDir);
                generator.generate(zipFile);
                new Handler(Looper.getMainLooper()).post(() -> {
                    progress.dismiss();
                    tvStatus.setText("生成完成！\n输出文件: " + outputDir.getAbsolutePath() + "/items-opaque.tga");
                    Toast.makeText(requireContext(), "物品图集生成成功", Toast.LENGTH_LONG).show();
                });
            } catch (final Exception e) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    progress.dismiss();
                    tvStatus.setText("生成失败: " + e.getMessage());
                    Toast.makeText(requireContext(), "生成失败", Toast.LENGTH_SHORT).show();
                });
                e.printStackTrace();
            }
        }).start();
    }

    private void exportPaths() {
        final ProgressDialog progress = new ProgressDialog(requireContext());
        progress.setMessage("正在遍历物品路径...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setCancelable(false);
        progress.show();
        new Thread(() -> {
            final ZipFileLister.ListResult result = ZipFileLister.listItemsPaths(selectedFile);
            new Handler(Looper.getMainLooper()).post(() -> {
                progress.dismiss();
                if (result.success) {
                    tvStatus.setText("导出成功！共 " + result.count + " 个物品\n保存位置: " + result.outputPath);
                    Toast.makeText(requireContext(), "导出成功，共 " + result.count + " 个物品", Toast.LENGTH_LONG).show();
                } else {
                    tvStatus.setText("导出失败: " + result.message);
                    Toast.makeText(requireContext(), "导出失败: " + result.message, Toast.LENGTH_SHORT).show();
                }
            });

        }).start();
    }

    private void selectFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        String[] mimeTypes = {"application/zip", "application/vnd.android.package-archive", "application/x-zip-compressed"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(Intent.createChooser(intent, "选择材质包文件"), REQUEST_CODE_SELECT_FILE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        filePickerLauncher = registerForActivityResult(new ActivityResultContracts.OpenDocument(), uri -> {
            if (uri != null) {
                String path = getPathFromUri(uri);
                if (path != null) {
                    selectedFile = new File(path);
                    tvSelectedFile.setText("已选择: " + selectedFile.getName());
                    btnGenerate.setEnabled(true);
                    btnListPaths.setEnabled(true);
                } else {
                    try {
                        InputStream is = requireContext().getContentResolver().openInputStream(uri);
                        File tempFile = new File(requireContext().getCacheDir(), "temp_mcaddon.zip");
                        FileOutputStream fos = new FileOutputStream(tempFile);
                        byte[] buffer = new byte[4096];
                        int len;
                        while ((len = is.read(buffer)) != -1) {
                            fos.write(buffer, 0, len);
                        }
                        fos.close();
                        is.close();
                        selectedFile = tempFile;
                        tvSelectedFile.setText("已选择: " + uri.getLastPathSegment());
                        btnGenerate.setEnabled(true);
                        btnListPaths.setEnabled(true);
                    } catch (Exception e) {
                        tvStatus.setText("无法读取文件: " + e.getMessage());
                        btnGenerate.setEnabled(false);
                        btnListPaths.setEnabled(false);
                    }
                }
            }
        });

        outputLauncher = registerForActivityResult(new ActivityResultContracts.CreateDocument(), uri -> {

        });
        return inflater.inflate(R.layout.fragment_texture_pack_generator, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View root, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);
        tvSelectedFile = root.findViewById(R.id.tv_selected_file);
        tvOutputPath = root.findViewById(R.id.tv_output_path);
        tvStatus = root.findViewById(R.id.tv_status);
        btnSelectFile = root.findViewById(R.id.btn_select_file);
        btnGenerate = root.findViewById(R.id.btn_generate);
        btnListPaths = root.findViewById(R.id.btn_list_paths);

        //outputDir = new File(Environment.getExternalStorageDirectory(), "MirrorDevBox/GeneratedPack");
        outputDir = new File(requireContext().getExternalFilesDir(null), "MirrorDevBox/GeneratedPack");
        tvOutputPath.setText("输出目录: " + outputDir.getAbsolutePath());

        btnSelectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //selectFile();
                filePickerLauncher.launch(new String[]{"application/zip", "application/vnd.android.package-archive", "application/x-zip-compressed"});
            }
        });

        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedFile != null && selectedFile.exists()) {
                    generateItemsAtlas(selectedFile);
                    Toast.makeText(requireContext(), "请先选择有效的文件", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnListPaths.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedFile != null && selectedFile.exists()) {
                    exportPaths();
                    Toast.makeText(requireContext(), "请先选择材质包文件", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}