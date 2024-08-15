package com.example.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class ScannerOverlayView extends View {

    private Paint borderPaint;
    private int squareSize;

    public ScannerOverlayView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        borderPaint = new Paint();
        borderPaint.setColor(0xFF4CBB17);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(8);

        squareSize = 700;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        int left = (width - squareSize) / 2;
        int top = (height - squareSize) / 2 - 400;
        int right = left + squareSize;
        int bottom = top + squareSize;
        Paint outsidePaint = new Paint();
        outsidePaint.setColor(0x88000000);
        canvas.drawRect(0, 0, width, top, outsidePaint);
        canvas.drawRect(0, top, left, bottom, outsidePaint);
        canvas.drawRect(right, top, width, bottom, outsidePaint);
        canvas.drawRect(0, bottom, width, height, outsidePaint);

        // Draw the square
        canvas.drawRect(left, top, right, bottom, borderPaint);
    }
}
