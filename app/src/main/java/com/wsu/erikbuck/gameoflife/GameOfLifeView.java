package com.wsu.erikbuck.gameoflife;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;

/**
 * Created by erik on 5/22/16.
 */
public class GameOfLifeView extends ZoomableView {
    private static float cellSize = 45;
    private static int delayMs = 200;
    private android.graphics.Paint paint;
    private GameOfLifeModel model;

    private Handler mHandler;

    private static class Animator implements Runnable {
        GameOfLifeView mView;

        Animator(GameOfLifeView view) {
            mView = view;
        }

        @Override
        public void run() {
            mView.model.update();
            mView.invalidate();
            mView.mHandler.postDelayed(this, delayMs);
        }
    }
    public GameOfLifeView(Context context) {
        super(context);
    }

    public GameOfLifeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GameOfLifeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init(AttributeSet attrs, int defStyle) {
        super.init(attrs, defStyle);
        mHandler = new Handler();
        paint = new Paint();
        final CellCoordinate initialCellPositions[] = {
                new CellCoordinate(10, 20),
                new CellCoordinate(11, 20),
                new CellCoordinate(12, 20),
                new CellCoordinate(13, 20),
                new CellCoordinate(14, 20),
                new CellCoordinate(15, 20),
                new CellCoordinate(16, 20),
                new CellCoordinate(17, 20),
                new CellCoordinate(18, 20),
                new CellCoordinate(19, 20),
        };
        model = new GameOfLifeModel(initialCellPositions);

        Animator runnable = new Animator(this);
        mHandler.postDelayed(runnable, delayMs);
    }

    // This is a Template Method. It is called automatically after the canvas has been
    // appropriately scaled and translated.
    @Override
    protected void onDrawScaled(Canvas canvas) {
        float left = ((float)Math.floor((getLeft() - translateX) / cellSize) - 1);
        float top = ((float)Math.floor((getTop() - translateY) / cellSize) - 1);
        float right =  (float)Math.ceil((getRight() - translateX) / cellSize);
        float bottom =  (float)Math.ceil((getBottom() - translateY) / cellSize);

        paint.setStrokeWidth(3);
        paint.setColor(Color.GRAY);
        for(float y = top; y <= bottom; y += 1) {
            canvas.drawLine(left * cellSize, y * cellSize, right * cellSize, y * cellSize, paint);
        }
        for(float x = left; x <= right; x += 1) {
            canvas.drawLine(x * cellSize, top * cellSize, x * cellSize, bottom * cellSize, paint);
        }

        paint.setColor(Color.GREEN);
        for(CellCoordinate pos : model.getPositions()) {
            canvas.drawCircle((pos.x + 0.5f) * cellSize, (pos.y + 0.5f) * cellSize, cellSize * 0.5f, paint);
        }
    }

}
