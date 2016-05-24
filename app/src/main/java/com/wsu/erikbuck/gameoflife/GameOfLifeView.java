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
    public static int mediumUpdatePeriodMs = 50;

    private static float cellSize = 45;

    private android.graphics.Paint mPaint;
    private GameOfLifeModel mModel;
    private Handler mHandler;
    private boolean mIsRunning;
    private int mUpdatePeriodMs = mediumUpdatePeriodMs;

    private static class Animator implements Runnable {
        GameOfLifeView mView;

        Animator(GameOfLifeView view) {
            mView = view;
        }

        @Override
        public void run() {
            mView.mModel.update();
            mView.invalidate();
            if(mView.mIsRunning) {
                mView.mHandler.postDelayed(this, mView.mUpdatePeriodMs);
            }
        }
    }

    Animator mRunnable;

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
        mIsRunning = false;
        mHandler = new Handler();
        mPaint = new Paint();
        mModel = null;
        mRunnable = new Animator(this);

        if(mIsRunning) {
            mHandler.postDelayed(mRunnable, mUpdatePeriodMs);
        }
    }

    // This is a Template Method. It is called automatically after the canvas has been
    // appropriately scaled and translated.
    @Override
    protected void onDrawScaled(Canvas canvas) {
        float left = ((float)Math.floor((getLeft() - mTranslateX) / cellSize) - 1);
        float top = ((float)Math.floor((getTop() - mTranslateY) / cellSize) - 1);
        float right =  (float)Math.ceil((getRight() - mTranslateX) / cellSize);
        float bottom =  (float)Math.ceil((getBottom() - mTranslateY) / cellSize);

        mPaint.setStrokeWidth(3);
        mPaint.setColor(Color.GRAY);
        for(float y = top; y <= bottom; y += 1) {
            canvas.drawLine(left * cellSize, y * cellSize, right * cellSize, y * cellSize, mPaint);
        }
        for(float x = left; x <= right; x += 1) {
            canvas.drawLine(x * cellSize, top * cellSize, x * cellSize, bottom * cellSize, mPaint);
        }

        mPaint.setColor(Color.GREEN);
        for(CellCoordinate pos : mModel.getPositions()) {
            canvas.drawCircle((pos.x + 0.5f) * cellSize, (pos.y + 0.5f) * cellSize, cellSize * 0.5f, mPaint);
        }
    }

    public void setUpdatePerionMs(int someMs) {
        mUpdatePeriodMs = someMs;
    }

    public boolean getIsRunning() { return mIsRunning; }

    public void toggleIsRunning() {
        mIsRunning = !mIsRunning;
        if(mIsRunning) {
            mHandler.postDelayed(mRunnable, 0);
        }
    }

    public void setModel(GameOfLifeModel model) {
        mModel = model;
        this.invalidate();
    }
}
