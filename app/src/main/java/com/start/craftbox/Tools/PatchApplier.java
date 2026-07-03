package com.start.craftbox.Tools;

import com.google.gson.Gson;
import com.start.craftbox.Entity.Patch.PatchNode;
import com.start.craftbox.Entity.Patch.PatchPackage;
import com.start.craftbox.Entity.Patch.SinglePatch;

import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.util.Arrays;

public class PatchApplier {

    public static PatchPackage parseJsonToPackage(String jsonStr) {
        Gson gson = new Gson();
        PatchPackage pack = gson.fromJson(jsonStr, PatchPackage.class);

        if (pack != null && pack.patches != null) {
            for (SinglePatch patch : pack.patches) {
                patch.isChecked = patch.enabled_by_default;
            }
        }
        return pack;
    }

    public static boolean applyCheckedPatches(File soFile, PatchPackage patchPackage) {
        File backupFile = new File(soFile.getAbsolutePath() + ".bak");
        try {
            copyFile(soFile, backupFile);

            try (RandomAccessFile raf = new RandomAccessFile(soFile, "rw")) {
                for (SinglePatch patch : patchPackage.patches) {
                    if (!patch.isChecked) {
                        continue;
                    }

                    for (PatchNode node : patch.nodes) {
                        long offset = Long.decode(node.offset);
                        byte[] oldBytes = hexToBytes(node.oldBytes);
                        byte[] newBytes = hexToBytes(node.newBytes);

                        raf.seek(offset);
                        byte[] currentBytes = new byte[oldBytes.length];
                        raf.readFully(currentBytes);

                        if (!Arrays.equals(currentBytes, oldBytes)) {
                            System.err.println("错误：补丁 [" + patch.name + "] 在偏移量 " + node.offset + " 处校验失败！");
                            raf.close();
                            copyFile(backupFile, soFile);
                            backupFile.delete();
                            return false;
                        }

                        raf.seek(offset);
                        raf.write(newBytes);
                    }
                }
            }
            backupFile.delete();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            if (backupFile.exists()) {
                try {
                    copyFile(backupFile, soFile);
                    backupFile.delete();
                } catch (Exception ignored) {
                }
            }
            return false;
        }
    }

    private static byte[] hexToBytes(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }
        return data;
    }

    private static void copyFile(File source, File dest) throws Exception {
        try (FileInputStream fis = new FileInputStream(source);
             java.io.FileOutputStream fos = new java.io.FileOutputStream(dest)) {
            byte[] buffer = new byte[8192];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
        }
    }
}