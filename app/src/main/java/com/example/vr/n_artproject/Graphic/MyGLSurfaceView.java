package com.example.vr.n_artproject.Graphic;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
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

    public void setZoom( float zoom){mRenderer.setZoom(zoom);}
    public void setY_shift(float y) {
        mRenderer.setY_shift(y);
    }

    public void setX_shift(float X) {
        mRenderer.setX_shift(X);
    }
    public void resetZoom(){ mRenderer.resetZoom();}

    public void setRotationIdendity(){
        mRenderer.setRotationIdendity();
    }

    public void setAngleX(float angle){
        mRenderer.setAngleX(angle);
    }

    public void setAngleY(float angle){
        mRenderer.setAngleY(angle);
    }

    public void setSnap(boolean in_snap){mRenderer.Snap = in_snap;}

    public void setSnaped(boolean in_snap){mRenderer.Snaped = in_snap;}
    public boolean getSnaped(){ return mRenderer.Snaped;}

}
