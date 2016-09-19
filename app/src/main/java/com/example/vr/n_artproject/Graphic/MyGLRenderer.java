package com.example.vr.n_artproject.Graphic;

import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.example.vr.n_artproject.LoaderNCalculater.VectorCal;
import com.example.vr.n_artproject.ModelActivity;
import com.example.vr.n_artproject.Models.Model;
import com.example.vr.n_artproject.Models.OSP;

import org.artoolkit.ar.base.rendering.RenderUtils;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by miles on 2015/7/21.
 */
public class MyGLRenderer implements GLSurfaceView.Renderer {

    //models
    private String[] model_files;
    private OSP mOSP1;
    private OSP mOSP2;
    private Model mandible;
    private Model maxilla;
    private Model skull;

    //controls
    private boolean sTLLoadingCheck = false;
    public boolean[] AllSTLLoadingCheck = new boolean[]{true, true, true, true, true};
    public static boolean Snap = false;
    public static boolean Snaped = false;
    private int glViewWide, glViewHeight;
    private float range = 120;

    //matrix
    private final float[] mMVMMatrix = new float[16];
    private final float[] mMVPMatrix = new float[16];
    private final float[] mModelMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];

    //light
    float[] ambientLight = new float[]{0.05f, 0.05f, 0.05f, 1.0f};
    float[] diffuseLight = new float[]{0.45f, 0.45f, 0.45f, 1.0f};
    float[] lightPos = new float[]{100.0f, -130.0f, 140.0f, 0.0f};

    //matrix rotation stack
    private final float[] mTempMatrix = new float[16];
    public static final float[] mAccumulatedMatrix = new float[16];
    private final float[] mCurrMatrix = new float[16];

    float[] osp1M = new float[16];
    float[] osp2M = new float[16];

    public MyGLRenderer(String[] filenames) {
        super();
        model_files = filenames;
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Matrix.setIdentityM(mTempMatrix, 0);
        Matrix.setIdentityM(mAccumulatedMatrix, 0);
        Matrix.setIdentityM(osp1M, 0);
        Matrix.setIdentityM(osp2M, 0);

        loadSTL.start();
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        gl.glViewport(0, 0, width, height);

        glViewWide = width;
        glViewHeight = height;
        float ratio = (float) width / height;
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrthof(-range * ratio, range * ratio, -range, range, -range, range);
    }

    public void onDrawFrame(GL10 gl) {
//        float[] scratch1 = new float[16];
//        float[] scratch2 = new float[16];
//        Matrix.setIdentityM(scratch1, 0);
//        Matrix.setIdentityM(scratch2, 0);
//
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        //Render settings
        gl.glEnable(GL10.GL_CULL_FACE);
        gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glFrontFace(GL10.GL_CCW);

        //Light setting and bind buffer
        FloatBuffer ambientBuffer = RenderUtils.buildFloatBuffer(ambientLight);
        FloatBuffer diffuseBuffer = RenderUtils.buildFloatBuffer(diffuseLight);
        FloatBuffer lightPosBuffer = RenderUtils.buildFloatBuffer(lightPos);
        gl.glPushMatrix();
        gl.glEnable(GL10.GL_LIGHTING);
        gl.glEnable(GL10.GL_LIGHT0);
        gl.glLightModelfv(GL10.GL_LIGHT_MODEL_AMBIENT, ambientBuffer);
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, diffuseBuffer);
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, diffuseBuffer);
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lightPosBuffer);
        gl.glEnable(GL10.GL_COLOR_MATERIAL);
        gl.glPopMatrix();

//        Matrix.rotateM(mCurrMatrix, 0, mAngleY, 0.0f, 1.0f, 0.0f);
//        Matrix.rotateM(mCurrMatrix, 0, mAngleX, 1.0f, 0, 0.0f);
//        mAngleX = 0.0f;
//        mAngleY = 0.0f;
//
//        //rotation stack
//        Matrix.multiplyMM(mTempMatrix, 0, mCurrMatrix, 0, mAccumulatedMatrix, 0);
//        System.arraycopy(mTempMatrix, 0, mAccumulatedMatrix, 0, 16);
//
//        Matrix.multiplyMM(mTempMatrix, 0, mModelMatrix, 0, mAccumulatedMatrix, 0);
//        System.arraycopy(mTempMatrix, 0, mModelMatrix, 0, 16);
//
//        //FH plane and reposition
//        Matrix.rotateM(mModelMatrix, 0, mOSP1.ospNrmalAngle, mOSP1.ospRotationVector[0], mOSP1.ospRotationVector[1], mOSP1.ospRotationVector[2]);
//        Matrix.translateM(mModelMatrix, 0, -mOSP1.meanX, -mOSP1.meanY, -mOSP1.meanZ);
//
//        //project
//        Matrix.multiplyMM(mMVMMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
//        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVMMatrix, 0);
//
//        //get matrix from tcp
//        osp1M = GlassUI10.file1Matrix;
//        osp2M = GlassUI10.file2Matrix;
//
//        //multiply matrix from tcp to models
//        Matrix.multiplyMM(scratch1, 0, mModelMatrix, 0, osp1M, 0);
//        Matrix.multiplyMM(scratch1, 0, mViewMatrix, 0, scratch1, 0);
//        Matrix.multiplyMM(scratch1, 0, mProjectionMatrix, 0, scratch1, 0);
//
//        Matrix.multiplyMM(scratch2, 0, mModelMatrix, 0, osp2M, 0);
//        Matrix.multiplyMM(scratch2, 0, mViewMatrix, 0, scratch2, 0);
//        Matrix.multiplyMM(scratch2, 0, mProjectionMatrix, 0, scratch2, 0);
//
//
//        skull.draw(mMVPMatrix, mMVMMatrix);
//        maxilla.draw(scratch1, mMVMMatrix);
//        mandible.draw(scratch2, mMVMMatrix);
//
//        GLES20.glEnable(GLES20.GL_BLEND);
//        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
//        mOSP1.draw(mMVPMatrix);
//        mOSP2.draw(scratch2);
//        GLES20.glDisable(GLES20.GL_BLEND);

        if (Snap) {
            ModelActivity.glShotbitmap = VectorCal.SavePixels(0, 0, glViewWide, glViewHeight);
            Snap = false;
            Snaped = true;
        }

    }


    Thread loadSTL = new Thread(new Runnable() {
        @Override
        public void run() {
            skull = new Model(model_files[0], 0.6f);
            AllSTLLoadingCheck[0] = skull.isLoaded();

            maxilla = new Model(model_files[1], 0.6f);
            AllSTLLoadingCheck[1] = maxilla.isLoaded();

            mandible = new Model(model_files[2], 0.6f);
            AllSTLLoadingCheck[2] = mandible.isLoaded();

            mOSP1 = new OSP(model_files[3], new float[]{0.2f, 0.709803922f, 0.898039216f, 0.4f}, 0.3f);
            AllSTLLoadingCheck[3] = mOSP1.isLoaded();

            mOSP2 = new OSP(model_files[4], new float[]{1f, 0.54902f, 0f, 0.4f}, 0.3f);
            AllSTLLoadingCheck[4] = mOSP2.isLoaded();

            all_stl_check();
        }
    });

    void all_stl_check(){
        boolean temp = true;
        for(int i = 0; i < 5; i++){
            temp = temp & AllSTLLoadingCheck[i];
        }
        sTLLoadingCheck = temp;
    }

    public static volatile float mAngleX = -90;
    public static volatile float mAngleY = 0;

    public static void setAngleX(float angle) {
        mAngleX = angle;
    }

    public float getAngleX() {
        return mAngleX;
    }

    public static void setAngleY(float angle) {
        mAngleY = angle;
    }

    public float getAngleY() {
        return mAngleY;
    }

}
