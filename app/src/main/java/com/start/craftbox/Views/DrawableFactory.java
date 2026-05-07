package com.start.craftbox.Views;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class DrawableFactory {
    public static Drawable createCheckerboardDrawable(int tileSize) {
        Bitmap bitmap = Bitmap.createBitmap(tileSize * 2, tileSize * 2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();

        paint.setColor(0xFFFFFFFF);
        canvas.drawRect(0, 0, tileSize, tileSize, paint);
        canvas.drawRect(tileSize, tileSize, tileSize * 2, tileSize * 2, paint);

        paint.setColor(0xFFE0E0E0);
        canvas.drawRect(tileSize, 0, tileSize * 2, tileSize, paint);
        canvas.drawRect(0, tileSize, tileSize, tileSize * 2, paint);

        BitmapDrawable drawable = new BitmapDrawable(Resources.getSystem(), bitmap);
        drawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        return drawable;
    }
}