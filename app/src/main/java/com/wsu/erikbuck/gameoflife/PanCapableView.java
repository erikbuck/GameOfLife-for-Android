package com.wsu.erikbuck.gameoflife;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import static junit.framework.Assert.assertTrue;

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
    private final ScaleGestureDetector mDetector = new ScaleGestureDetector(getContext(),
            new ScaleGestureDetector.SimpleOnScaleGestureListener());

    private float mStartX = 0f;
    private float mStartY = 0f;
    private float mTranslateX = 0f;
    private float mTranslateY = 0f;
    private float mPreviousTranslateX = 0f;
    private float mPreviousTranslateY = 0f;

    /**
     * This constructor is only implemented because it is required by the superclass, View.
     * This implementation does nothing other than call super(context).
     * @param context  See View(context) (cannot be null)
     */
    public PanCapableView(Context context) {
        super(context);
        if(BuildConfig.DEBUG) assertTrue(null != context);
        init(null, 0);
    }

    /**
     * This constructor is only implemented because it is required by the superclass, View.
     * This implementation does nothing other than call super(context, attrs).
     * @param context  See View(context, attrs) (cannot be null)
     * @param attrs See View(context, attrs)
     */
    public PanCapableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(BuildConfig.DEBUG) assertTrue(null != context);
        init(attrs, 0);
    }

    /**
     * This constructor is only implemented because it is required by the superclass, View.
     * This implementation does nothing other than call super(context, attrs, defStyle).
     * @param context  See View(context, attrs, defStyle) (cannot be null)
     * @param attrs See View(context, attrs, defStyle)
     * @param defStyle See View(context, attrs, defStyle)
     */
    public PanCapableView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if(BuildConfig.DEBUG) assertTrue(null != context);
        init(attrs, defStyle);
    }

    /**
     * Subclasses should override this method to perform instance initialization as opposed to
     * performing initialization within a constructor. This is a design pattern required by
     * the Android View class. Any implementation that overrides this method MUST call this
     * implementation via super.init(attrs, defStyle).
     * @param attrs See View init(AttributeSet attrs, int defStyle)
     * @param defStyle See View init(AttributeSet attrs, int defStyle)
     */
    void init(AttributeSet attrs, int defStyle) {
    }

    /**
     *
     * @return The current amount of translation along the X axis applied to implement user gesture
     * controlled pan.
     */
    public float getTranslationX() {
        return mTranslateX;
    }

    /**
     *
     * @return The current amount of translation along the Y axis applied to implement user gesture
     * controlled pan.
     */
    public float getTranslationY() {
        return mTranslateY;
    }

    /**
     * Process user touch events.
     * @param event the motion event to be processed (typically a finger or pointer gesture in some
     *              stage of completion from start through move/drag to end i.e. the finger or
     *              pointer is no longer touching the input device) (cannot be null)
     * @return true if and only if the event has been processed.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(BuildConfig.DEBUG) assertTrue(null != event);

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

    /**
     * This is a Template Method. See https://en.wikipedia.org/wiki/Template_method_pattern
     * This method is called from onDraw() after the canvas has been appropriately scaled and
     * translated. Override this implementation to perform custom drawing in subclass
     * implementations. This implementation does nothing, and it is not necessary for overriding
     * implementations to call this implementation.
     *
     * Note: as with all Template Methods, this method should not be called from any place other
     * than the implementation of this class. This method is called automatically when appropriate.
     *
     * @param canvas The Android Canvas instance upon which drawing should occur
     */
    void onDrawPanned(Canvas canvas) {
    }

    /**
     * This method performs appropriate scaling and translation to implement user controlled pan
     * and then calls onDrawPanned().
     * @param canvas The Android Canvas instance upon which drawing should occur (cannot be null)
     */
    @Override
    public void onDraw(Canvas canvas) {
        if(BuildConfig.DEBUG) assertTrue(null != canvas);
        super.onDraw(canvas);
        canvas.save();
        canvas.translate(mTranslateX, mTranslateY);

        // Call the onDrawPanned after any scale and translation have been performed
        this.onDrawPanned(canvas);
        canvas.restore();
    }

    /**
     * This implementation calls the superclass's implementation and then calls center().
     * @param changed See View.onLayout()
     * @param left See View.onLayout()
     * @param top See View.onLayout()
     * @param right See View.onLayout()
     * @param bottom See View.onLayout()
     */
    @Override
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        center();
    }

    /**
     * This method "centers" the user controlled pan by updating encapsulated translation amounts so
     * that the next time onDrawPanned(canvas) is called, to canvas origin is centered in the
     * rectangle defined by getRight(), getLeft(), getBottom(), and getTop().
     */
    public void center() {
        mTranslateX = (getRight() - getLeft()) * 0.5f;
        mTranslateY = (getBottom() - getTop()) * 0.5f;
        mPreviousTranslateX = mTranslateX;
        mPreviousTranslateY = mTranslateY;
        this.invalidate();
    }
 }
