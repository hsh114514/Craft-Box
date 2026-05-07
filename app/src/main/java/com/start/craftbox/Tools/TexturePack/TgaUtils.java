package com.start.craftbox.Tools.TexturePack;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * TGA 图像格式与 Android Bitmap 互转工具类
 *
 * 支持格式：
 * - 未压缩真彩色 TGA (类型 2, 24/32位)
 * - RLE 压缩真彩色 TGA (类型 10, 24/32位)
 * - 未压缩灰度 TGA (类型 3, 8位)
 *
 * @author Nalanko
 * @version 1.1
 * @since 2024
 */
public class TgaUtils {

    // TGA 头结构体大小
    private static final int TGA_HEADER_SIZE = 18;

    // 默认输出目录
    private static final String DEFAULT_OUTPUT_DIR = "CraftBox/Images";

    // 输出目录路径
    private String outputPath;

    /**
     * 构造函数，使用默认输出目录 /sdcard/CraftBox/Images
     */
    public TgaUtils() {
        this(DEFAULT_OUTPUT_DIR);
    }

    /**
     * 构造函数，自定义输出目录
     * @param outputDir 输出目录名（相对路径，位于外部存储根目录下）
     */
    public TgaUtils(String outputDir) {
        if (outputDir == null || outputDir.trim().isEmpty()) {
            outputDir = DEFAULT_OUTPUT_DIR;
        }
        // 确保目录名不以斜杠开头
        while (outputDir.startsWith("/")) {
            outputDir = outputDir.substring(1);
        }
        // 构建完整路径
        this.outputPath = Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + outputDir;

        // 确保输出目录存在
        File dir = new File(this.outputPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * 构造函数，使用完整路径
     * @param outputDir 完整输出目录路径
     * @param useFullPath true 表示使用完整路径，false 表示使用相对路径
     */
    public TgaUtils(String outputDir, boolean useFullPath) {
        if (useFullPath) {
            this.outputPath = outputDir;
        } else {
            if (outputDir == null || outputDir.trim().isEmpty()) {
                outputDir = DEFAULT_OUTPUT_DIR;
            }
            while (outputDir.startsWith("/")) {
                outputDir = outputDir.substring(1);
            }
            this.outputPath = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + File.separator + outputDir;
        }

        // 确保输出目录存在
        File dir = new File(this.outputPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * 获取当前输出目录路径
     * @return 输出目录完整路径
     */
    public String getOutputPath() {
        return outputPath;
    }

    /**
     * 设置输出目录
     * @param outputDir 新的输出目录（相对路径）
     */
    public void setOutputDir(String outputDir) {
        if (outputDir == null || outputDir.trim().isEmpty()) {
            outputDir = DEFAULT_OUTPUT_DIR;
        }
        while (outputDir.startsWith("/")) {
            outputDir = outputDir.substring(1);
        }
        this.outputPath = Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + outputDir;

        File dir = new File(this.outputPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * 设置输出目录（完整路径）
     * @param outputDir 完整输出目录路径
     * @param useFullPath true 表示使用完整路径
     */
    public void setOutputDir(String outputDir, boolean useFullPath) {
        if (useFullPath) {
            this.outputPath = outputDir;
        } else {
            setOutputDir(outputDir);
        }

        File dir = new File(this.outputPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    // ==================== TGA 转 PNG 方法 ====================

    /**
     * 将 TGA 文件转换为 PNG 文件
     * @param tgaFile TGA 源文件
     * @return 生成的 PNG 文件对象，如果失败返回 null
     * @throws IOException 读取或转换失败
     */
    public File tgaToPng(File tgaFile) throws IOException {
        if (!tgaFile.exists() || !tgaFile.isFile()) {
            throw new IOException("源文件不存在: " + tgaFile.getAbsolutePath());
        }

        // 生成输出文件名
        String fileName = tgaFile.getName();
        String pngName = fileName.substring(0, fileName.lastIndexOf('.')) + ".png";
        File pngFile = new File(outputPath, pngName);

        // 转换
        Bitmap bitmap = tgaToBitmap(tgaFile);
        if (bitmap == null) {
            throw new IOException("TGA 转 Bitmap 失败");
        }

        try (FileOutputStream fos = new FileOutputStream(pngFile)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            bitmap.recycle();
        }

        return pngFile;
    }

    /**
     * 将 TGA 文件转换为 PNG 并指定输出文件名
     * @param tgaFile TGA 源文件
     * @param outputFileName 输出文件名（不含路径）
     * @return 生成的 PNG 文件对象
     * @throws IOException 读取或转换失败
     */
    public File tgaToPng(File tgaFile, String outputFileName) throws IOException {
        if (!tgaFile.exists() || !tgaFile.isFile()) {
            throw new IOException("源文件不存在: " + tgaFile.getAbsolutePath());
        }

        // 确保文件名以 .png 结尾
        if (!outputFileName.toLowerCase().endsWith(".png")) {
            outputFileName += ".png";
        }

        File pngFile = new File(outputPath, outputFileName);

        Bitmap bitmap = tgaToBitmap(tgaFile);
        if (bitmap == null) {
            throw new IOException("TGA 转 Bitmap 失败");
        }

        try (FileOutputStream fos = new FileOutputStream(pngFile)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            bitmap.recycle();
        }

        return pngFile;
    }

    // ==================== PNG 转 TGA 方法 ====================

    /**
     * 将 PNG 文件转换为 TGA 文件
     * @param pngFile PNG 源文件
     * @return 生成的 TGA 文件对象
     * @throws IOException 读取或转换失败
     */
    public File pngToTga(File pngFile) throws IOException {
        if (!pngFile.exists() || !pngFile.isFile()) {
            throw new IOException("源文件不存在: " + pngFile.getAbsolutePath());
        }

        // 生成输出文件名
        String fileName = pngFile.getName();
        String tgaName = fileName.substring(0, fileName.lastIndexOf('.')) + ".tga";
        File tgaFile = new File(outputPath, tgaName);

        // 转换
        Bitmap bitmap = BitmapFactory.decodeFile(pngFile.getAbsolutePath());
        if (bitmap == null) {
            throw new IOException("PNG 解码失败");
        }

        try (FileOutputStream fos = new FileOutputStream(tgaFile)) {
            bitmapToTga(bitmap, fos);
            bitmap.recycle();
        }

        return tgaFile;
    }

    /**
     * 将 PNG 文件转换为 TGA 并指定输出文件名
     * @param pngFile PNG 源文件
     * @param outputFileName 输出文件名（不含路径）
     * @return 生成的 TGA 文件对象
     * @throws IOException 读取或转换失败
     */
    public File pngToTga(File pngFile, String outputFileName) throws IOException {
        if (!pngFile.exists() || !pngFile.isFile()) {
            throw new IOException("源文件不存在: " + pngFile.getAbsolutePath());
        }

        // 确保文件名以 .tga 结尾
        if (!outputFileName.toLowerCase().endsWith(".tga")) {
            outputFileName += ".tga";
        }

        File tgaFile = new File(outputPath, outputFileName);

        Bitmap bitmap = BitmapFactory.decodeFile(pngFile.getAbsolutePath());
        if (bitmap == null) {
            throw new IOException("PNG 解码失败");
        }

        try (FileOutputStream fos = new FileOutputStream(tgaFile)) {
            bitmapToTga(bitmap, fos);
            bitmap.recycle();
        }

        return tgaFile;
    }

    // ==================== 批量转换方法 ====================

    /**
     * 批量转换目录下的所有 TGA 文件为 PNG
     * @param sourceDir 源目录
     * @return 成功转换的文件数量
     * @throws IOException 转换失败
     */
    public int batchTgaToPng(File sourceDir) throws IOException {
        if (!sourceDir.exists() || !sourceDir.isDirectory()) {
            throw new IOException("源目录不存在: " + sourceDir.getAbsolutePath());
        }

        File[] tgaFiles = sourceDir.listFiles(new java.io.FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".tga");
            }
        });

        if (tgaFiles == null || tgaFiles.length == 0) {
            return 0;
        }

        int successCount = 0;
        for (File tgaFile : tgaFiles) {
            try {
                tgaToPng(tgaFile);
                successCount++;
            } catch (Exception e) {
                // 记录错误但不中断转换
                System.err.println("转换失败: " + tgaFile.getName() + " - " + e.getMessage());
            }
        }

        return successCount;
    }

    /**
     * 批量转换目录下的所有 PNG 文件为 TGA
     * @param sourceDir 源目录
     * @return 成功转换的文件数量
     * @throws IOException 转换失败
     */
    public int batchPngToTga(File sourceDir) throws IOException {
        if (!sourceDir.exists() || !sourceDir.isDirectory()) {
            throw new IOException("源目录不存在: " + sourceDir.getAbsolutePath());
        }

        File[] pngFiles = sourceDir.listFiles(new java.io.FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".png");
            }
        });

        if (pngFiles == null || pngFiles.length == 0) {
            return 0;
        }

        int successCount = 0;
        for (File pngFile : pngFiles) {
            try {
                pngToTga(pngFile);
                successCount++;
            } catch (Exception e) {
                System.err.println("转换失败: " + pngFile.getName() + " - " + e.getMessage());
            }
        }

        return successCount;
    }

    // ==================== 核心转换方法 ====================

    /**
     * 将 TGA 文件转换为 Bitmap
     * @param tgaFile TGA 文件
     * @return Bitmap 对象
     * @throws IOException 读取或解析失败
     */
    public Bitmap tgaToBitmap(File tgaFile) throws IOException {
        try (InputStream is = new FileInputStream(tgaFile)) {
            return tgaToBitmap(is);
        }
    }

    /**
     * 将 TGA 输入流转换为 Bitmap
     * @param inputStream TGA 数据流
     * @return Bitmap 对象
     * @throws IOException 读取或解析失败
     */
    public Bitmap tgaToBitmap(InputStream inputStream) throws IOException {
        // 读取头
        byte[] header = new byte[TGA_HEADER_SIZE];
        int read = inputStream.read(header);
        if (read < TGA_HEADER_SIZE) {
            throw new IOException("TGA header too short");
        }

        // 解析头字段
        int idLength = header[0] & 0xFF;
        int colorMapType = header[1] & 0xFF;
        int imageType = header[2] & 0xFF;

        // 图像信息
        int width = ((header[13] & 0xFF) << 8) | (header[12] & 0xFF);
        int height = ((header[15] & 0xFF) << 8) | (header[14] & 0xFF);
        int pixelDepth = header[16] & 0xFF;
        int descriptor = header[17] & 0xFF;

        // 跳过图像 ID
        if (idLength > 0) {
            long skipped = inputStream.skip(idLength);
            if (skipped != idLength) {
                throw new IOException("Failed to skip image ID");
            }
        }

        // 跳过颜色表数据（如果有）
        if (colorMapType == 1) {
            int colorMapLength = ((header[6] & 0xFF) << 8) | (header[5] & 0xFF);
            int colorMapEntrySize = header[7] & 0xFF;
            int colorMapBytes = colorMapLength * (colorMapEntrySize / 8);
            long skipped = inputStream.skip(colorMapBytes);
            if (skipped != colorMapBytes) {
                throw new IOException("Failed to skip color map");
            }
        }

        // 根据图像类型决定处理方式
        Bitmap bitmap;
        if (imageType == 2) { // 未压缩真彩色
            bitmap = readUncompressedTrueColor(inputStream, width, height, pixelDepth);
        } else if (imageType == 10) { // RLE 压缩真彩色
            bitmap = readRleTrueColor(inputStream, width, height, pixelDepth);
        } else if (imageType == 3) { // 未压缩灰度
            bitmap = readUncompressedGrayscale(inputStream, width, height, pixelDepth);
        } else {
            throw new IOException("Unsupported TGA image type: " + imageType);
        }

        // 根据描述符决定是否需要垂直翻转
        boolean isTopLeft = (descriptor & 0x20) != 0;
        if (!isTopLeft) {
            bitmap = flipVertical(bitmap);
        }

        return bitmap;
    }

    /**
     * 读取未压缩真彩色 TGA
     */
    private Bitmap readUncompressedTrueColor(InputStream is, int width, int height, int pixelDepth) throws IOException {
        int bytesPerPixel = pixelDepth / 8;
        if (bytesPerPixel != 3 && bytesPerPixel != 4) {
            throw new IOException("Unsupported pixel depth: " + pixelDepth);
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        int totalPixels = width * height;
        byte[] pixelBuffer = new byte[bytesPerPixel * totalPixels];
        int read = 0;
        while (read < pixelBuffer.length) {
            int r = is.read(pixelBuffer, read, pixelBuffer.length - read);
            if (r < 0) break;
            read += r;
        }
        if (read != pixelBuffer.length) {
            throw new IOException("Incomplete pixel data");
        }

        int[] pixels = new int[totalPixels];
        int idx = 0;
        for (int i = 0; i < totalPixels; i++) {
            int b = pixelBuffer[idx++] & 0xFF;
            int g = pixelBuffer[idx++] & 0xFF;
            int r = pixelBuffer[idx++] & 0xFF;
            int a = 0xFF;
            if (bytesPerPixel == 4) {
                a = pixelBuffer[idx++] & 0xFF;
            }
            pixels[i] = (a << 24) | (r << 16) | (g << 8) | b;
        }
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    /**
     * 读取 RLE 压缩真彩色 TGA
     */
    private Bitmap readRleTrueColor(InputStream is, int width, int height, int pixelDepth) throws IOException {
        int bytesPerPixel = pixelDepth / 8;
        if (bytesPerPixel != 3 && bytesPerPixel != 4) {
            throw new IOException("Unsupported pixel depth for RLE: " + pixelDepth);
        }

        int totalPixels = width * height;
        int[] pixels = new int[totalPixels];
        int pixelIndex = 0;
        byte[] pixel = new byte[bytesPerPixel];

        while (pixelIndex < totalPixels) {
            int header = is.read();
            if (header == -1) throw new IOException("Unexpected end of stream");
            int count = (header & 0x7F) + 1;
            boolean isRle = (header & 0x80) != 0;

            if (isRle) {
                int read = 0;
                while (read < bytesPerPixel) {
                    int r = is.read(pixel, read, bytesPerPixel - read);
                    if (r < 0) throw new IOException("Unexpected end of stream");
                    read += r;
                }
                int b = pixel[0] & 0xFF;
                int g = pixel[1] & 0xFF;
                int r = pixel[2] & 0xFF;
                int a = 0xFF;
                if (bytesPerPixel == 4) {
                    a = pixel[3] & 0xFF;
                }
                int argb = (a << 24) | (r << 16) | (g << 8) | b;
                for (int i = 0; i < count && pixelIndex < totalPixels; i++) {
                    pixels[pixelIndex++] = argb;
                }
            } else {
                for (int i = 0; i < count && pixelIndex < totalPixels; i++) {
                    int read = 0;
                    while (read < bytesPerPixel) {
                        int r = is.read(pixel, read, bytesPerPixel - read);
                        if (r < 0) throw new IOException("Unexpected end of stream");
                        read += r;
                    }
                    int b = pixel[0] & 0xFF;
                    int g = pixel[1] & 0xFF;
                    int r = pixel[2] & 0xFF;
                    int a = 0xFF;
                    if (bytesPerPixel == 4) {
                        a = pixel[3] & 0xFF;
                    }
                    pixels[pixelIndex++] = (a << 24) | (r << 16) | (g << 8) | b;
                }
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    /**
     * 读取未压缩灰度 TGA
     */
    private Bitmap readUncompressedGrayscale(InputStream is, int width, int height, int pixelDepth) throws IOException {
        if (pixelDepth != 8) {
            throw new IOException("Unsupported grayscale depth: " + pixelDepth);
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        int totalPixels = width * height;
        byte[] gray = new byte[totalPixels];
        int read = 0;
        while (read < totalPixels) {
            int r = is.read(gray, read, totalPixels - read);
            if (r < 0) break;
            read += r;
        }
        if (read != totalPixels) {
            throw new IOException("Incomplete grayscale data");
        }

        int[] pixels = new int[totalPixels];
        for (int i = 0; i < totalPixels; i++) {
            int g = gray[i] & 0xFF;
            pixels[i] = 0xFF000000 | (g << 16) | (g << 8) | g;
        }
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    /**
     * 垂直翻转 Bitmap
     */
    private Bitmap flipVertical(Bitmap src) {
        int width = src.getWidth();
        int height = src.getHeight();
        Bitmap flipped = Bitmap.createBitmap(width, height, src.getConfig());
        int[] pixels = new int[width * height];
        src.getPixels(pixels, 0, width, 0, 0, width, height);
        int[] flippedPixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            System.arraycopy(pixels, y * width, flippedPixels, (height - 1 - y) * width, width);
        }
        flipped.setPixels(flippedPixels, 0, width, 0, 0, width, height);
        return flipped;
    }

    /**
     * 将 Bitmap 写入 TGA 输出流（未压缩真彩色）
     * @param bitmap 源 Bitmap
     * @param os 输出流
     * @throws IOException 写入失败
     */
    public void bitmapToTga(Bitmap bitmap, OutputStream os) throws IOException {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        boolean hasAlpha = bitmap.hasAlpha();
        int bytesPerPixel = hasAlpha ? 4 : 3;
        int pixelDepth = bytesPerPixel * 8;

        // 写入 TGA 头
        byte[] header = new byte[TGA_HEADER_SIZE];
        header[0] = 0;                     // ID长度
        header[1] = 0;                     // 无颜色表
        header[2] = 2;                     // 未压缩真彩色
        header[3] = 0; header[4] = 0;      // 颜色表首索引
        header[5] = 0; header[6] = 0;      // 颜色表长度
        header[7] = 0;                     // 颜色表每项位数
        header[8] = 0; header[9] = 0;      // X 原点
        header[10] = 0; header[11] = 0;    // Y 原点
        header[12] = (byte) (width & 0xFF);
        header[13] = (byte) ((width >> 8) & 0xFF);
        header[14] = (byte) (height & 0xFF);
        header[15] = (byte) ((height >> 8) & 0xFF);
        header[16] = (byte) pixelDepth;
        header[17] = (byte) (hasAlpha ? 0x28 : 0x20); // 描述符：8位alpha + 原点在左上角

        os.write(header);

        // 获取像素数据
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        // 写入像素数据
        byte[] pixelBuffer = new byte[bytesPerPixel];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = pixels[y * width + x];
                int b = argb & 0xFF;
                int g = (argb >> 8) & 0xFF;
                int r = (argb >> 16) & 0xFF;
                int a = (argb >> 24) & 0xFF;
                pixelBuffer[0] = (byte) b;
                pixelBuffer[1] = (byte) g;
                pixelBuffer[2] = (byte) r;
                if (hasAlpha) {
                    pixelBuffer[3] = (byte) a;
                }
                os.write(pixelBuffer);
            }
        }
        os.flush();
    }
}