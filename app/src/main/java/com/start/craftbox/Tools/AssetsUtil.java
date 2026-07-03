package com.start.craftbox.Tools;

import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class AssetsUtil {
    public static String readStringFromAssets(Context context, String fileName) {
        try (InputStream is = context.getAssets().open(fileName)) {
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            return new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static byte[] readBytesFromAssets(Context context, String fileName) {
        try (InputStream is = context.getAssets().open(fileName);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[4096];
            int length;
            while ((length = is.read(buffer)) != -1) {
                bos.write(buffer, 0, length);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static File getFileFromAssets(Context context, String fileName) {
        return getFileFromAssets(context, fileName, true);
    }

    public static File getFileFromAssets(Context context, String fileName, boolean forceOverride) {
        File cacheFile = new File(context.getCacheDir(), fileName);
        if (cacheFile.exists() && !forceOverride) return cacheFile;

        File parentDir = cacheFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) parentDir.mkdirs();

        try (InputStream is = context.getAssets().open(fileName);
             FileOutputStream fos = new FileOutputStream(cacheFile)) {

            byte[] buffer = new byte[8192];
            int length;
            while ((length = is.read(buffer)) != -1) {
                fos.write(buffer, 0, length);
            }
            fos.flush();
            return cacheFile;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }
}