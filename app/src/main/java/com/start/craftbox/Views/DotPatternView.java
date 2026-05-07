package com.start.craftbox.Views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;

public class DotPatternView extends View {
    private Paint mPaint;
    private float mDotRadius = 3f; // 点的半径
    private int mGap = 40;         // 点与点之间的间距
    private int mDotColor = Color.WHITE;
    private int mDotAlpha = 30;    // 透明度 (0-255)，对应 HTML 的 0.1 左右

    public DotPatternView(Context context) {
        super(context);
        init();
    }

    public DotPatternView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(mDotColor);
        mPaint.setAlpha(mDotAlpha);
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        float offset = mGap / 2f;

        for (float x = offset; x < width; x += mGap) {
            for (float y = offset; y < height; y += mGap) {
                canvas.drawCircle(x, y, mDotRadius, mPaint);
            }
        }
    }
}