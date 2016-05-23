package com.wsu.erikbuck.gameoflife;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

/**
 * Created by erik on 5/22/16.
 */
public class GameOfLifeView extends ZoomableView {
    private static float cellSize = 90;
    private android.graphics.Paint paint;
    private GameOfLifeModel model;

    public GameOfLifeView(Context context) {
        super(context);
        model = new GameOfLifeModel();
    }

    public GameOfLifeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        model = new GameOfLifeModel();
    }

    public GameOfLifeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        model = new GameOfLifeModel();
    }

    @Override
    protected void init(AttributeSet attrs, int defStyle) {
        super.init(attrs, defStyle);
        paint = new Paint();
    }

    // This is a Template Method. It is called automatically after the canvas has been
    // appropriately scaled and translated.
    @Override
    protected void onDrawScaled(Canvas canvas) {
        float left = ((float)Math.floor((getLeft() - translateX) / cellSize) - 1) * cellSize;
        float top = ((float)Math.floor((getTop() - translateY) / cellSize) - 1) * cellSize;
        float right =  (float)Math.ceil((getRight() - translateX) / cellSize) * cellSize;
        float bottom =  (float)Math.ceil((getBottom() - translateY) / cellSize) * cellSize;

        paint.setStrokeWidth(3);
        paint.setColor(Color.GRAY);
        for(float y = top; y <= bottom; y += cellSize) {
            canvas.drawLine(left, y, right, y, paint);
        }
        for(float x = left; x <= right; x += cellSize) {
            canvas.drawLine(x, top, x, bottom, paint);
        }
    }

}
