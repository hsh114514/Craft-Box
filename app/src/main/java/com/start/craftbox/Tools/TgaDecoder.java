package com.start.craftbox.Tools;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TgaDecoder {
    private static int offset = 0;
    private static int read(byte[] buf) {
        return buf[offset++] & 0xFF;
    }

    public static byte[] inputStreamToByteArray(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384];
        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return buffer.toByteArray();
    }

    public static Bitmap decodeTGA(byte[] buf) throws IOException {
        offset = 0;
        int imageType = buf[2];
        int width = (buf[13] & 0xFF) << 8 | (buf[12] & 0xFF);
        int height = (buf[15] & 0xFF) << 8 | (buf[14] & 0xFF);
        int bpp = buf[16] & 0xFF;
        int descriptor = buf[17] & 0xFF;

        offset = 18;

        int n = width * height;
        int[] pixels = new int[n];
        int idx = 0;

        if (imageType == 0x02) {
            while (idx < n) {
                int b = read(buf);
                int g = read(buf);
                int r = read(buf);
                int a = (bpp == 32) ? read(buf) : 255;
                pixels[idx++] = (a << 24) | (r << 16) | (g << 8) | b;
            }
        } else if (imageType == 0x0A) {
            while (idx < n) {
                int header = read(buf);
                int count = (header & 0x7F) + 1;
                if ((header & 0x80) != 0) {
                    int b = read(buf);
                    int g = read(buf);
                    int r = read(buf);
                    int a = (bpp == 32) ? read(buf) : 255;
                    int color = (a << 24) | (r << 16) | (g << 8) | b;
                    for (int i = 0; i < count; i++) pixels[idx++] = color;
                } else {
                    for (int i = 0; i < count; i++) {
                        int b = read(buf);
                        int g = read(buf);
                        int r = read(buf);
                        int a = (bpp == 32) ? read(buf) : 255;
                        pixels[idx++] = (a << 24) | (r << 16) | (g << 8) | b;
                    }
                }
            }
        }

        boolean flip = (descriptor & 0x20) == 0;
        if (flip) {
            int[] flippedPixels = new int[n];
            for (int y = 0; y < height; y++) {
                System.arraycopy(pixels, y * width, flippedPixels, (height - 1 - y) * width, width);
            }
            pixels = flippedPixels;
        }

        Bitmap bimg = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bimg.setPixels(pixels, 0, width, 0, 0, width, height);
        return bimg;
    }

    public static void encodeTGA(Bitmap bitmap, OutputStream os) throws IOException {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        byte[] header = new byte[18];
        header[2] = 2;
        header[12] = (byte) (width & 0xFF);
        header[13] = (byte) ((width >> 8) & 0xFF);
        header[14] = (byte) (height & 0xFF);
        header[15] = (byte) ((height >> 8) & 0xFF);
        header[16] = 32;
        header[17] = 0x28;
        os.write(header);

        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        for (int p : pixels) {
            os.write(p & 0xFF);
            os.write((p >> 8) & 0xFF);
            os.write((p >> 16) & 0xFF);
            os.write((p >> 24) & 0xFF);
        }
        os.flush();
    }
}
