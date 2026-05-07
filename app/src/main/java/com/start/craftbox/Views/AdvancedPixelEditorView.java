package com.start.craftbox.Views;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

public class AdvancedPixelEditorView extends View {
    private Bitmap mainBitmap;
    private boolean isModifying = false;
    private Matrix drawMatrix = new Matrix();
    private Matrix inverseMatrix = new Matrix();
    private Paint bitmapPaint = new Paint();
    private Paint checkerPaint = new Paint();
    private int selectedColor = Color.RED;
    public void setPaintColor(int color) {
        selectedColor = color;
    }
    public int getSelectedColor() {
        return selectedColor;
    }

    private Stack<UndoableAction> undoStack = new Stack<>();
    private PixelAction currentPixelAction; // 仅铅笔使用

    private ScaleGestureDetector mScaleDetector;
    private GestureDetector mGestureDetector;
    private int lastX = -1, lastY = -1;
    public EditMode getEditMode() {
        return currentMode;
    }
    public enum EditMode { DRAW, PAN }
    private EditMode currentMode = EditMode.PAN;

    public enum Tool {
        PENCIL,    // 铅笔
        ERASER,    // 橡皮擦
        BUCKET,    // 油漆桶
        BRUSH      // 画笔
    }

    private Tool currentTool = Tool.PENCIL;
    private int brushSize = 5;

    public void setEditMode(EditMode mode) {
        this.currentMode = mode;
        lastX = -1;
        lastY = -1;
    }

    public void setTool(Tool tool) {
        currentTool = tool;
    }

    public boolean isModifying() {
        return isModifying;
    }

    private static class PixelChange {
        int x, y, oldColor;
        PixelChange(int x, int y, int oldColor) {
            this.x = x; this.y = y; this.oldColor = oldColor;
        }
    }

    public AdvancedPixelEditorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AdvancedPixelEditorView(Context context) {
        super(context);
        init(context);
    }

    private interface UndoableAction {
        void undo(Bitmap bitmap);
    }

    private static class PixelAction implements UndoableAction {
        List<PixelChange> changes = new ArrayList<>();
        @Override
        public void undo(Bitmap bitmap) {
            for (int i = changes.size() - 1; i >= 0; i--) {
                PixelChange c = changes.get(i);
                bitmap.setPixel(c.x, c.y, c.oldColor);
            }
        }
    }
    private static class SnapshotAction implements UndoableAction {
        int[] snapshot;
        @Override
        public void undo(Bitmap bitmap) {
            bitmap.setPixels(snapshot, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        }
    }

    private void performBucketFill(int x, int y) {
        if (mainBitmap == null) return;
        isModifying = true;
        int targetColor = mainBitmap.getPixel(x, y);
        if (targetColor == selectedColor) return;
        SnapshotAction action = new SnapshotAction();
        action.snapshot = new int[mainBitmap.getWidth() * mainBitmap.getHeight()];
        mainBitmap.getPixels(action.snapshot, 0, mainBitmap.getWidth(), 0, 0, mainBitmap.getWidth(), mainBitmap.getHeight());
        floodFill(x, y, targetColor, selectedColor);
        undoStack.push(action);
        invalidate();
    }

    private void floodFill(int x, int y, int targetColor, int replacementColor) {
        int bitmapWidth = mainBitmap.getWidth();
        int bitmapHeight = mainBitmap.getHeight();
        if (x < 0 || x >= bitmapWidth || y < 0 || y >= bitmapHeight) return;
        int startColor = mainBitmap.getPixel(x, y);
        if (startColor == replacementColor) return;
        Queue<Point> queue = new LinkedList<>();
        queue.add(new Point(x, y));
        while (!queue.isEmpty()) {
            Point p = queue.poll();
            int currentColor = mainBitmap.getPixel(p.x, p.y);
            if (!isColorMatch(currentColor, startColor)) continue;
            mainBitmap.setPixel(p.x, p.y, replacementColor);
            if (p.x > 0) queue.add(new Point(p.x - 1, p.y));
            if (p.x < bitmapWidth - 1) queue.add(new Point(p.x + 1, p.y));
            if (p.y > 0) queue.add(new Point(p.x, p.y - 1));
            if (p.y < bitmapHeight - 1) queue.add(new Point(p.x, p.y + 1));
        }
    }

    private boolean isColorMatch(int color1, int color2) {
        int tolerance = 30;
        int r1 = Color.red(color1);
        int g1 = Color.green(color1);
        int b1 = Color.blue(color1);
        int a1 = Color.alpha(color1);
        int r2 = Color.red(color2);
        int g2 = Color.green(color2);
        int b2 = Color.blue(color2);
        int a2 = Color.alpha(color2);
        if (Math.abs(a1 - a2) > tolerance) return false;
        int diff = (int) Math.sqrt(
            Math.pow(r1 - r2, 2) + 
            Math.pow(g1 - g2, 2) + 
            Math.pow(b1 - b2, 2)
        );
        return diff <= tolerance * Math.sqrt(3);
    }

    private void drawArea(int centerX, int centerY, int color, int size) {
        int radius = size - 1;
        for (int i = -radius; i <= radius; i++) {
            for (int j = -radius; j <= radius; j++) {
                if (i * i + j * j <= radius * radius) {
                    mainBitmap.setPixel(centerX + i, centerY + j, color);
                }
            }
        }
    }

    private void init(Context context) {
        bitmapPaint.setFilterBitmap(false);

        Bitmap checker = Bitmap.createBitmap(40, 40, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(checker);
        Paint p = new Paint();
        p.setColor(Color.LTGRAY);
        c.drawRect(0, 0, 20, 20, p);
        c.drawRect(20, 20, 40, 40, p);
        p.setColor(Color.WHITE);
        c.drawRect(20, 0, 40, 20, p);
        c.drawRect(0, 20, 20, 40, p);
        BitmapShader shader = new BitmapShader(checker, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        checkerPaint.setShader(shader);

        mScaleDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                float scale = detector.getScaleFactor();
                drawMatrix.postScale(scale, scale, detector.getFocusX(), detector.getFocusY());
                invalidate();
                return true;
            }
        });

        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float dX, float dY) {
                drawMatrix.postTranslate(-dX, -dY);
                invalidate();
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                //editPixel(e.getX(), e.getY());
                return true;
            }
        });


    }

    public void setImage(Bitmap bitmap) {
        this.mainBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        undoStack.clear();
        invalidate();
    }

    public Bitmap getImage() {
        return mainBitmap;
    }

    private void editPixel(float screenX, float screenY) {
        if (mainBitmap == null) return;
        isModifying = true;
        drawMatrix.invert(inverseMatrix);
        float[] pts = {screenX, screenY};
        inverseMatrix.mapPoints(pts);

        int x = (int) pts[0];
        int y = (int) pts[1];

        if (x >= 0 && x < mainBitmap.getWidth() && y >= 0 && y < mainBitmap.getHeight()) {
            if (x != lastX || y != lastY) {
                int oldColor = mainBitmap.getPixel(x, y);
                if (oldColor != selectedColor) {
                    currentPixelAction.changes.add(new PixelChange(x, y, oldColor));
                    mainBitmap.setPixel(x, y, selectedColor);
                    invalidate();
                }
                lastX = x;
                lastY = y;
            }
        }
    }

    public void undo() {
        if (undoStack.isEmpty()) return;

        UndoableAction lastAction = undoStack.pop();
        lastAction.undo(mainBitmap);
        invalidate();
    }

//    public void undo() {
//        if (undoStack.isEmpty()) return;
//        EditAction lastAction = undoStack.pop();
//        for (int i = lastAction.changes.size() - 1; i >= 0; i--) {
//            PixelChange change = lastAction.changes.get(i);
//            mainBitmap.setPixel(change.x, change.y, change.oldColor);
//        }
//        invalidate();
//    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mainBitmap == null) return;

        canvas.save();
        canvas.concat(drawMatrix);
        canvas.drawRect(0, 0, mainBitmap.getWidth(), mainBitmap.getHeight(), checkerPaint);
        canvas.drawBitmap(mainBitmap, 0, 0, bitmapPaint);
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (currentMode == EditMode.PAN) {
            mScaleDetector.onTouchEvent(event);
            mGestureDetector.onTouchEvent(event);
        } else {
            if (event.getPointerCount() == 1) {
                handleDraw(event);
            }
        }
        return true;
    }

    private void handleDraw(MotionEvent event) {
        drawMatrix.invert(inverseMatrix);
        float[] pts = {event.getX(), event.getY()};
        inverseMatrix.mapPoints(pts);
        int x = (int) pts[0];
        int y = (int) pts[1];

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (currentTool == Tool.BUCKET) {
                    performBucketFill(x, y);
                } else {
                    currentPixelAction = new PixelAction();
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (currentTool == Tool.PENCIL && currentPixelAction != null) {
                    editPixel(event.getX(), event.getY());
                }
                break;

            case MotionEvent.ACTION_UP:
                if (currentPixelAction != null && !currentPixelAction.changes.isEmpty()) {
                    undoStack.push(currentPixelAction);
                }
                currentPixelAction = null;
                lastX = -1; lastY = -1;
                break;
        }
    }
//    private void handleDraw(MotionEvent event) {
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                currentAction = new EditAction();
//                editPixel(event.getX(), event.getY());
//                break;
//
//            case MotionEvent.ACTION_MOVE:
//                if (currentAction != null) {
//                    editPixel(event.getX(), event.getY());
//                }
//                break;
//
//            case MotionEvent.ACTION_UP:
//                if (currentAction != null && !currentAction.changes.isEmpty()) {
//                    undoStack.push(currentAction);
//                }
//                currentAction = null;
//                lastX = -1;
//                lastY = -1;
//                break;
//        }
//    }
}