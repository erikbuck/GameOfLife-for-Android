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
    private android.graphics.Paint paint;

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
        paint = new Paint();
        paint.setColor(Color.GRAY);
    }

    // This is a Template Method. It is called automatically after the canvas has been
    // appropriately scaled and translated.
    @Override
    protected void onDrawScaled(Canvas canvas) {
        canvas.drawColor(Color.BLUE);
        canvas.drawCircle(200, 200, 100, paint);
    }

}
