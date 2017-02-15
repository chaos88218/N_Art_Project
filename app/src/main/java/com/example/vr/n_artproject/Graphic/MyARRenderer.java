package com.example.vr.n_artproject.Graphic;

import android.opengl.GLES10;
import android.opengl.GLU;
import android.opengl.Matrix;
import android.util.Log;

import com.example.vr.n_artproject.GlassARActivity;
import com.example.vr.n_artproject.LoaderNCalculater.FileReader;
import com.example.vr.n_artproject.Models.Model;
import com.example.vr.n_artproject.Models.OSP;
import com.example.vr.n_artproject.Registration.ARRegistration;

import org.artoolkit.ar.base.ARToolKit;
import org.artoolkit.ar.base.rendering.Cube;
import org.artoolkit.ar.base.rendering.RenderUtils;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * A very simple Renderer that adds a marker and draws a cube on it.
 */
public class MyARRenderer extends org.artoolkit.ar.base.rendering.ARRenderer {

    //Models
    private Cube cube = new Cube(20, 0, 0, 10);
    private String[] model_files;
    private Model skull;
    private Model maxilla;
    private Model mandible;
    private OSP mOSP1;
    private OSP mOSP2;

    //Marker
    private int markerID = -1;
    private ARRegistration arRegistration;


    //light 0
    private float[] ambientLight = new float[]{0f, 0f, 0f, 1.0f};
    private float[] diffuseLight = new float[]{0.2f, 0.2f, 0.2f, 1.0f};
    private float[] lightPos = new float[]{150.0f, -200.0f, 200, 0.0f};
    //light 1
    private float[] lightPos1 = new float[]{-150.0f, +200.0f, -200, 0.0f};

    //matrix
    private float[] aRMatrix = new float[16];
    private float[] drawing_matrix = new float[16];

    //controls
    private boolean STCorN;
    private boolean sTLLoadingCheck;
    private boolean[] AllSTLLoadingCheck = new boolean[]{false, false, false, false, false, false, false, false};
    private int sTLCheckSum = 0;
    private float rate;
    private float[] ARS;
    private float[] ART;


    /**
     * Markers can be configured here.
     */
    public MyARRenderer(String[] filenames, boolean stc_on) {
        super();
        Matrix.setIdentityM(drawing_matrix, 0);
        model_files = filenames;
        this.STCorN = stc_on;
    }

    @Override
    public boolean configureARScene() {
        markerID = ARToolKit.getInstance().addMarker("single;Data/N_ART.pat;40");
        if (markerID < 0) return false;
        if (!sTLLoadingCheck) {
            loadSTL.start();
        }
        return true;
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int w, int h) {
        super.onSurfaceChanged(gl, w, h);
        rate = (float) w / (float) h;
        Log.d("rate", rate + "");
    }

    //TODO: read cali file in and use it
    @Override
    public void draw(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        FloatBuffer ambientBuffer = RenderUtils.buildFloatBuffer(ambientLight);
        FloatBuffer diffuseBuffer = RenderUtils.buildFloatBuffer(diffuseLight);
        FloatBuffer lightPosBuffer = RenderUtils.buildFloatBuffer(lightPos);

        // Apply the ARToolKit projection matrix
        gl.glMatrixMode(GL10.GL_PROJECTION);
        if (STCorN) {
            if (AllSTLLoadingCheck[6]) {
                gl.glLoadIdentity();
                GLU.gluPerspective(gl, ARS[0], rate, 1f, 10000.0f);
            } else {
                gl.glLoadMatrixf(ARToolKit.getInstance().getProjectionMatrix(), 0);
            }
        } else {
            if (AllSTLLoadingCheck[7]) {
                gl.glLoadIdentity();
                GLU.gluPerspective(gl, ART[0], rate, 1f, 10000.0f);
            } else {
                gl.glLoadMatrixf(ARToolKit.getInstance().getProjectionMatrix(), 0);
            }
        }

        gl.glEnable(GL10.GL_CULL_FACE);
        gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glFrontFace(GL10.GL_CCW);

        //light 0, 1
        gl.glPushMatrix();
        gl.glEnable(GL10.GL_LIGHTING);
        gl.glEnable(GL10.GL_LIGHT0);
        gl.glLightModelfv(GL10.GL_LIGHT_MODEL_AMBIENT, ambientBuffer);
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, diffuseBuffer);
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, diffuseBuffer);
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lightPosBuffer);

        lightPosBuffer = RenderUtils.buildFloatBuffer(lightPos1);
        gl.glEnable(GL10.GL_LIGHT1);
        gl.glLightModelfv(GL10.GL_LIGHT_MODEL_AMBIENT, ambientBuffer);
        gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_AMBIENT, diffuseBuffer);
        gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_DIFFUSE, diffuseBuffer);
        gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_POSITION, lightPosBuffer);

        gl.glEnable(GL10.GL_COLOR_MATERIAL);
        gl.glPopMatrix();

        //
        gl.glMatrixMode(GL10.GL_MODELVIEW);


        if (ARToolKit.getInstance().queryMarkerVisible(markerID)) {
            aRMatrix = ARToolKit.getInstance().queryMarkerTransformation(markerID);
            if (sTLLoadingCheck) {
                if (GlassARActivity.modelViewable) {
                    load_matrix(gl);
                    gl.glMultMatrixf(drawing_matrix, 0);
                    skull.draw(gl);

                    gl.glMultMatrixf(GlassARActivity.matrix1, 0);
                    maxilla.draw(gl);

                    load_matrix(gl);
                    gl.glMultMatrixf(drawing_matrix, 0);
                    gl.glMultMatrixf(GlassARActivity.matrix2, 0);
                    mandible.draw(gl);
                }

                GLES10.glEnable(GLES10.GL_BLEND);
                GLES10.glBlendFunc(GLES10.GL_SRC_ALPHA, GLES10.GL_ONE_MINUS_SRC_ALPHA);
                load_matrix(gl);
                gl.glMultMatrixf(drawing_matrix, 0);
                mOSP1.draw(gl);

                gl.glMultMatrixf(GlassARActivity.matrix2, 0);
                mOSP2.draw(gl);
                GLES10.glDisable(GLES10.GL_BLEND);
            } else {
                load_matrix(gl);
                gl.glFrontFace(GLES10.GL_CW);
                gl.glRotatef(180.0f / 8.0f * sTLCheckSum, 0, 0, 1);
                gl.glTranslatef(0, 0, 16.0f - 16.0f / 8.0f * sTLCheckSum);
                cube.draw(gl);
                gl.glFrontFace(GLES10.GL_CCW);
            }
        }
    }


    private void load_matrix(GL10 gl) {
        if (STCorN) {
            if (AllSTLLoadingCheck[6]) {
                gl.glLoadMatrixf(ARS, 2);
            } else {
                gl.glLoadIdentity();
            }
        } else {
            if (AllSTLLoadingCheck[7]) {
                gl.glLoadMatrixf(ART, 2);
            } else {
                gl.glLoadIdentity();
            }
        }
        gl.glMultMatrixf(aRMatrix, 0);
    }

    Thread loadSTL = new Thread(new Runnable() {
        @Override
        public void run() {
            FileReader fileReader = new FileReader();

            skull = new Model(model_files[0], new float[]{0.2f, 0.709803922f, 0.898039216f}, 1.0f);
            AllSTLLoadingCheck[0] = skull.isLoaded();
            if(AllSTLLoadingCheck[0])sTLCheckSum++;

            maxilla = new Model(model_files[1], 1.0f);
            AllSTLLoadingCheck[1] = maxilla.isLoaded();
            if(AllSTLLoadingCheck[1])sTLCheckSum++;

            mandible = new Model(model_files[2], new float[]{1f, 0.54902f, 0f}, 1.0f);
            AllSTLLoadingCheck[2] = mandible.isLoaded();
            if(AllSTLLoadingCheck[2])sTLCheckSum++;

            mOSP1 = new OSP(model_files[3], new float[]{0.2f, 0.709803922f, 0.898039216f}, 0.25f);
            AllSTLLoadingCheck[3] = mOSP1.isLoaded();
            if(AllSTLLoadingCheck[3])sTLCheckSum++;

            mOSP2 = new OSP(model_files[4], new float[]{1f, 0.54902f, 0f}, 0.25f);
            AllSTLLoadingCheck[4] = mOSP2.isLoaded();
            if(AllSTLLoadingCheck[4])sTLCheckSum++;

            arRegistration = new ARRegistration(model_files[5], model_files[6]);
            AllSTLLoadingCheck[5] = arRegistration.isLoaded();
            if (AllSTLLoadingCheck[5]) {
                drawing_matrix = arRegistration.drawing_matrix();
                sTLCheckSum++;
            }


            ARS = fileReader.ARSReadTxt(model_files[8]);
            if (!Float.isNaN(ARS[0])) {
                AllSTLLoadingCheck[6] = true;
                sTLCheckSum++;
            }

            ART = fileReader.ARTReadBinary(model_files[9]);
            if (!Float.isNaN(ART[0])) {
                AllSTLLoadingCheck[7] = true;
                sTLCheckSum++;
            }

            all_stl_check();
        }
    });

    private void all_stl_check() {
        boolean temp = true;
        for (int i = 0; i < 5; i++) {
            temp = temp & AllSTLLoadingCheck[i];
        }
        sTLLoadingCheck = temp;
    }
}