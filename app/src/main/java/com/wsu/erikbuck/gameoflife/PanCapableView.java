package com.wsu.erikbuck.gameoflife;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

/**
 * This class is inspired by sample code provided at
 * http://vivin.net/2011/12/04/implementing-pinch-zoom-and-pandrag-in-an-android-view-on-the-canvas/8/
 * Author: Vivin Paliath modified by Erik M. Buck
 * <p/>
 * This class implements pan gesture handling. It has been greatly simplified and modernized from
 * Vivin Paliath's version and now supports "inflation" from an XML layout as well as automatic
 * handling of view layout and re-layout by Android.
 */
public class PanCapableView extends View {
    private ScaleGestureDetector mDetector;

    private float mStartX = 0f;
    private float mStartY = 0f;
    private float mTranslateX = 0f;
    private float mTranslateY = 0f;
    private float mPreviousTranslateX = 0f;
    private float mPreviousTranslateY = 0f;

    public PanCapableView(Context context) {
        super(context);
        init(null, 0);
    }

    public PanCapableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public PanCapableView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    void init(AttributeSet attrs, int defStyle) {
        mDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
    }

    public float getTranslationX() { return mTranslateX; }
    public float getTranslationY() { return mTranslateY; }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
                // Assign the current X and Y coordinate of the finger to startX and startY minus
                // the previously translated amount for each coordinates This works even when we are
                // translating the first time.
                mStartX = event.getX() - mPreviousTranslateX;
                mStartY = event.getY() - mPreviousTranslateY;
                break;

            case MotionEvent.ACTION_MOVE:
                mTranslateX = event.getX() - mStartX;
                mTranslateY = event.getY() - mStartY;

                // We cannot use startX and startY directly because we have adjusted their values
                // using the previous translation values. This is why we need to add those values to
                // startX and startY so that we can get the actual coordinates of the finger.
                double distanceSquared = Math.pow(event.getX() - (mStartX + mPreviousTranslateX), 2) +
                        Math.pow(event.getY() - (mStartY + mPreviousTranslateY), 2);

                if (distanceSquared > 1) {
                    invalidate();
                }

                break;

            case MotionEvent.ACTION_UP:
                mPreviousTranslateX = mTranslateX;
                mPreviousTranslateY = mTranslateY;
                invalidate();
                break;
        }

        mDetector.onTouchEvent(event);

        return true;
    }

    // This is a Template Method. It is called from onDraw() after the canvas has been
    //appropriately scaled and translated. Perform custom drawing here.
    void onDrawPanned(Canvas canvas) {}

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.translate(mTranslateX, mTranslateY);

        // Call the onDrawPanned after any scale and translation have been performed
        this.onDrawPanned(canvas);
        canvas.restore();
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            return true;
        }
    }

    @Override
    public void onLayout(boolean changed, int left, int top, int right,  int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        center();
    }

    public void center() {
        mTranslateX = (getRight() - getLeft()) * 0.5f;
        mTranslateY = (getBottom() - getTop()) * 0.5f;
        mPreviousTranslateX = mTranslateX;
        mPreviousTranslateY = mTranslateY;
        this.invalidate();
    }

}
