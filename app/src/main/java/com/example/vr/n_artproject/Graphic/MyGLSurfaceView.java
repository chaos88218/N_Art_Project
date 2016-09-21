package com.example.vr.n_artproject.Graphic;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

/**
 * Created by miles on 2015/7/21.
 */
public class MyGLSurfaceView extends GLSurfaceView {

    private final MyGLRenderer mRenderer;

    public MyGLSurfaceView(Context context, String[] filenames){
        super(context);
        setEGLContextClientVersion(1);
        mRenderer = new MyGLRenderer(filenames);
        setRenderer(mRenderer);
    }


    private float mPreviousX;
    private float mPreviousY;

    @Override
    public boolean onTouchEvent(MotionEvent e) {

        float x = e.getX();
        float y = e.getY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE: {
                float dx = (x - mPreviousX)* 2 / getWidth();
                float dy = (y - mPreviousY)* 2 / getHeight();

                float TOUCH_SCALE_FACTOR = (float) (180.0f / Math.PI);
                mRenderer.setAngleY(dx * TOUCH_SCALE_FACTOR);
                mRenderer.setAngleX(dy * TOUCH_SCALE_FACTOR);
            }break;

        }
        mPreviousX = x;
        mPreviousY = y;
        return true;
    }


}
