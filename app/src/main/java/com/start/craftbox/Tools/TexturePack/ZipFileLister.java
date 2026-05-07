package com.start.craftbox.Tools.TexturePack;

import android.os.Environment;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.FileHeader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
public class ZipFileLister {
    public static class ListResult {
        public boolean success;
        public String message;
        public int count;
        public String outputPath;

        ListResult(boolean success, String message, int count, String outputPath) {
            this.success = success;
            this.message = message;
            this.count = count;
            this.outputPath = outputPath;
        }
    }

    public static ListResult listItemsPaths(File zip) {
        if (zip == null || !zip.exists()) {
            return new ListResult(false, "文件不存在", 0, null);
        }
        List<String> paths = new ArrayList<>();
        try(ZipFile zipFile = new ZipFile(zip)) {
            List<FileHeader> fileHeaders = zipFile.getFileHeaders();
            for (FileHeader header : fileHeaders) {
                String fileName = header.getFileName();
                if (fileName.startsWith("textures/items/") && fileName.toLowerCase().endsWith(".png") && !header.isDirectory()) {
                    paths.add(fileName);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new ListResult(false, "读取失败: " + e.getMessage(), 0, null);
        }

        if (paths.isEmpty()) {
            return new ListResult(false, "未找到任何物品图片", 0, null);
        }

        // 保存到文件
        File outDir = new File(Environment.getExternalStorageDirectory(), "MirrorDevBox");
        if (!outDir.exists() && !outDir.mkdirs()) {
            return new ListResult(false, "无法创建输出目录", 0, null);
        }
        File outFile = new File(outDir, "item_paths.txt");
        try {
            try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(outFile)))) {
                for (String p : paths) {
                    writer.println(p);
                }
                return new ListResult(true, "导出成功", paths.size(), outFile.getAbsolutePath());
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new ListResult(false, "保存失败: " + e.getMessage(), 0, null);
        }
    }

//    public static ListResult listItemsPaths(File zipFile) {
//        if (zipFile == null || !zipFile.exists()) {
//            return new ListResult(false, "文件不存在", 0, null);
//        }
//        List<String> paths = new ArrayList<>();
//        try {
//            try (ZipFile zip = new ZipFile(zipFile)) {
//                Enumeration<? extends ZipEntry> entries = zip.entries();
//                while (entries.hasMoreElements()) {
//                    ZipEntry entry = entries.nextElement();
//                    String name = entry.getName();
//                    if (name.startsWith("textures/items/") && name.toLowerCase().endsWith(".png") && !entry.isDirectory()) {
//                        paths.add(name);
//                    }
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            return new ListResult(false, "读取失败: " + e.getMessage(), 0, null);
//        }
//
//        if (paths.isEmpty()) {
//            return new ListResult(false, "未找到任何物品图片", 0, null);
//        }
//
//        // 保存到文件
//        File outDir = new File(Environment.getExternalStorageDirectory(), "MirrorDevBox");
//        if (!outDir.exists() && !outDir.mkdirs()) {
//            return new ListResult(false, "无法创建输出目录", 0, null);
//        }
//        File outFile = new File(outDir, "item_paths.txt");
//        try {
//            try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(outFile)))) {
//                for (String p : paths) {
//                    writer.println(p);
//                }
//                return new ListResult(true, "导出成功", paths.size(), outFile.getAbsolutePath());
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            return new ListResult(false, "保存失败: " + e.getMessage(), 0, null);
//        }
//    }
}
