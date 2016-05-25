package com.wsu.erikbuck.gameoflife;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;

import junit.framework.Assert;

/**
 * Created by erik on 5/22/16.
 * Each instance of this class encapsulates and instance of GameOfLifeModel and displays the current
 * state of the encapsulated GameOfLifeModel each time the onDrawPanned() method is called. Note:
 * onDrawPanned() is called automatically by the superclass, PanCapableView, and should not be
 * called at any other time from any other place. The implementation of onDrawPanned() relies upon
 * consistent appropriate external state provided by PanCapableView prior to calling onDrawPanned().
 * <p/>
 * Each instance of this class periodically calls its encapsulated GameOfLifeModel's update()
 * method. The frequency of the calls is determined by the update period in milliseconds specified
 * using the setUpdatePeriodMs() method and whether the GameOfLifeView is "running". If
 * setUpdatePeriodMs() is not called, a reasonable default update period is used.
 */
public class GameOfLifeView extends PanCapableView {
    private static final int defaultUpdatePeriodMs = 50;

    private android.graphics.Paint mPaint;
    private GameOfLifeModel mModel;
    private Handler mHandler;
    private boolean mIsRunning;
    private int mUpdatePeriodMs = defaultUpdatePeriodMs;
    private Animator mRunnable;

    /**
     * This constructor is only implemented because it is required by the superclass, PanCapableView.
     * This implementation does nothing other than call super(context).
     *
     * @param context See PanCapableView(context). (cannot be null)
     */
    GameOfLifeView(Context context) {
        super(context);
        Assert.assertTrue(null != context);
     }

    /**
     * This constructor is only implemented because it is required by the superclass, PanCapableView.
     * This implementation does nothing other than call super(context, attrs).
     *
     * @param context See PanCapableView(context, attrs)  (cannot be null)
     * @param attrs   See PanCapableView(context, attrs)
     */
    public GameOfLifeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Assert.assertTrue(null != context);
    }

    /**
     * This constructor is only implemented because it is required by the superclass, PanCapableView.
     * This implementation does nothing other than call super(context, attrs, defStyle).
     *
     * @param context  See PanCapableView(context, attrs, defStyle)  (cannot be null)
     * @param attrs    See PanCapableView(context, attrs, defStyle)
     * @param defStyle See PanCapableView(context, attrs, defStyle)
     */
    public GameOfLifeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Assert.assertTrue(null != context);
    }

    /**
     * Subclasses should override this method to perform instance initialization as opposed to
     * performing initialization within a constructor. This is a design pattern required by
     * the Android View class. Any implementation that overrides this method MUST call this
     * implementation via super.init(attrs, defStyle).
     *
     * @param attrs    See View init(AttributeSet attrs, int defStyle)
     * @param defStyle See View init(AttributeSet attrs, int defStyle)
     */
    @Override
    protected void init(AttributeSet attrs, int defStyle) {
        super.init(attrs, defStyle);
        mModel = null;
        mIsRunning = false;
        mHandler = new Handler();
        mPaint = new Paint();
        mRunnable = new Animator(this);

        Assert.assertTrue(null != mHandler);
        Assert.assertTrue(null != mPaint);
        Assert.assertTrue(null != mRunnable);
    }

    /**
     * This is a Template Method a.k.a "Hollywwod Method". See https://en.wikipedia.org/wiki/Template_method_pattern
     * This method is called automatically after the canvas has been appropriately scaled and
     * translated.
     * <p/>
     * Note: as with all Template Methods, this method should not be called from any place other
     * than the implementation of this class. Any override of this method MUST call this
     * implementation.
     *
     * @param canvas The already scaled and translated Android Canvas instance upon which drawing
     *               should occur
     */
    @Override
    protected void onDrawPanned(Canvas canvas) {
        Assert.assertTrue(null != canvas);
        Assert.assertTrue(null != mPaint);
        Assert.assertTrue(null != mModel);
        float cellSize = 45;
        float left = ((float) Math.floor((getLeft() - getTranslationX()) / cellSize) - 1);
        float top = ((float) Math.floor((getTop() - getTranslationY()) / cellSize) - 1);
        float right = (float) Math.ceil((getRight() - getTranslationX()) / cellSize);
        float bottom = (float) Math.ceil((getBottom() - getTranslationY()) / cellSize);

        mPaint.setStrokeWidth(3);
        mPaint.setColor(Color.GRAY);
        for (float y = top; y <= bottom; y += 1) {
            canvas.drawLine(left * cellSize, y * cellSize, right * cellSize, y * cellSize, mPaint);
        }
        for (float x = left; x <= right; x += 1) {
            canvas.drawLine(x * cellSize, top * cellSize, x * cellSize, bottom * cellSize, mPaint);
        }

        mPaint.setColor(Color.GREEN);
        for (GameOfLifeModel.CellCoordinate pos : mModel.getPositions()) {
            canvas.drawCircle((pos.getX() + 0.5f) * cellSize, (pos.getY() + 0.5f) * cellSize,
                    cellSize * 0.5f, mPaint);
        }
    }

    /**
     * Set the amount of time in milliseconds that should elapse between automatic updates of the
     * encapsulated GameOfLifeModel instance. Note: This update period is only used when the game
     * is "running".
     *
     * @param someMs The number of milliseconds between encapsulated GameOfLifeModel instance updates.
     */
    public void setUpdatePeriodMs(int someMs) {
        mUpdatePeriodMs = someMs;
    }

    /**
     * @return true if and only if the game is "running" meaning that the game is automatically
     * updating its encapsulated GameOfLifeModel instance at some frequency > 0Hz.
     */
    public boolean getIsRunning() {
        return mIsRunning;
    }

    /**
     * If the game is "ruuning", this method stops it from running which means automatic updating
     * the encapsulated GameOfLifeModel stops. If the game is not running, this method resumes
     * running the game by automatic updating the encapsulated GameOfLifeModel.
     */
    public void toggleIsRunning() {
        boolean isRunning_pre = mIsRunning;
        Assert.assertTrue(null != mHandler);
        Assert.assertTrue(null != mRunnable);
        mIsRunning = !mIsRunning;
        if (mIsRunning) {
            mHandler.postDelayed(mRunnable, 0);
        }
        Assert.assertTrue(null != mHandler);
        Assert.assertTrue(null != mRunnable);
        Assert.assertTrue(isRunning_pre != mIsRunning);
    }

    /**
     *
     * @return the model to display
     */
    GameOfLifeModel getModel() {
        return mModel;
    }
    /**
     * Set the model to be encapsulated. The model can be set at any time (even while the game
     * is "running").
     *
     * @param model The model to be displayed and updated.
     */
    public void setModel(GameOfLifeModel model) {
        Assert.assertTrue(null != model);
        mModel = model;
        this.invalidate();
        Assert.assertTrue(null != mModel);
    }

    /**
     * This class implements the Command Pattern. See https://en.wikipedia.org/wiki/Command_pattern
     * Call the run() method to invoke a single update of the encapsulated GameOfLifeView instance
     * and schedule another invocation to occur after the GameOfLifeView instances mUpdatePeriodMs.
     */
    private static class Animator implements Runnable {
        final GameOfLifeView mView;

        Animator(GameOfLifeView view) {
            Assert.assertTrue(null != view);
            mView = view;
            Assert.assertTrue(null != mView);
        }

        @Override
        public void run() {
            Assert.assertTrue(null != mView);
            Assert.assertTrue(null != mView.mModel);
            Assert.assertTrue(null != mView.mHandler);
            mView.mModel.update();
            mView.invalidate();
            if (mView.mIsRunning) {
                mView.mHandler.postDelayed(this, mView.mUpdatePeriodMs);
            }
            Assert.assertTrue(null != mView);
            Assert.assertTrue(null != mView.mModel);
            Assert.assertTrue(null != mView.mHandler);
        }
    }
}
