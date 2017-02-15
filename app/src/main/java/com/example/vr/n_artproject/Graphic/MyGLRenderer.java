package com.example.vr.n_artproject.Graphic;

import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.example.vr.n_artproject.LoaderNCalculater.FileReader;
import com.example.vr.n_artproject.LoaderNCalculater.VectorCal;
import com.example.vr.n_artproject.ModelActivity;
import com.example.vr.n_artproject.Models.Model;
import com.example.vr.n_artproject.Models.OSP;

import org.artoolkit.ar.base.rendering.RenderUtils;

import java.nio.FloatBuffer;
import java.util.Arrays;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by miles on 2015/7/21.
 */
public class MyGLRenderer implements GLSurfaceView.Renderer {

    //models
    //TODO: Model Array
    private String[] model_files;
    private OSP mOSP1;
    private OSP mOSP2;
    private Model mandible;
    private Model maxilla;
    private Model skull;
    private float[] poor;

    //controls
    private boolean sTLLoadingCheck = false;
    private boolean[] AllSTLLoadingCheck = new boolean[]{true, true, true, true, true};
    public boolean Snap = false;
    public boolean Snaped = false;
    private int glViewWide, glViewHeight;
    private float range = 120;

    //matrix
    private float[] osp_ad_matrix = new float[16];
    private float[] last_rotaion_matrix = new float[16];
    private float[] rotation_stack_matrix = new float[16];

    //light 0
    private float[] ambientLight = new float[]{0f, 0f, 0f, 1.0f};
    private float[] diffuseLight = new float[]{0.2f, 0.2f, 0.2f, 1.0f};
    private float[] lightPos = new float[]{150.0f, -200.0f, 200, 0.0f};
    //light 1
    private float[] lightPos1 = new float[]{-150.0f, +200.0f, -200, 0.0f};

    private float angleFH = 0;
    private float mAngleX = 0;
    private float mAngleY = 0;
    private float z_shift = 1, x_shift = 0, y_shift = 0;


    public MyGLRenderer(String[] filenames) {
        super();
        model_files = filenames;
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Matrix.setIdentityM(osp_ad_matrix, 0);
        Matrix.setIdentityM(rotation_stack_matrix, 0);
        Matrix.setIdentityM(last_rotaion_matrix, 0);
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
        if (sTLLoadingCheck) {

            if(ModelActivity.show_model) {
                load_matrix(gl);
                skull.draw(gl);

                gl.glMultMatrixf(ModelActivity.matrix1, 0);
                maxilla.draw(gl);

                load_matrix(gl);
                gl.glMultMatrixf(ModelActivity.matrix2, 0);
                mandible.draw(gl);
            }

            gl.glDisable(GL10.GL_LIGHTING);
            load_matrix(gl);
            mOSP1.draw(gl);
            gl.glMultMatrixf(ModelActivity.matrix2, 0);
            mOSP2.draw(gl);
        }

        if (Snap) {
            ModelActivity.gl_shot_bitmap = VectorCal.SavePixels(0, 0, glViewWide, glViewHeight);
            Snap = false;
            Snaped = true;
        }

    }


    private Thread loadSTL = new Thread(new Runnable() {
        @Override
        public void run() {
            skull = new Model(model_files[0], new float[]{0.2f, 0.709803922f, 0.898039216f}, 1.0f);
            AllSTLLoadingCheck[0] = skull.isLoaded();

            maxilla = new Model(model_files[1], 1.0f);
            AllSTLLoadingCheck[1] = maxilla.isLoaded();

            mandible = new Model(model_files[2], new float[]{1f, 0.54902f, 0f}, 1.0f);
            AllSTLLoadingCheck[2] = mandible.isLoaded();

            mOSP1 = new OSP(model_files[3], new float[]{0.2f, 0.709803922f, 0.898039216f}, 0.25f);
            AllSTLLoadingCheck[3] = mOSP1.isLoaded();
            if (mOSP1.isLoaded()) {
                Matrix.rotateM(osp_ad_matrix, 0, mOSP1.normal_angle, mOSP1.rotation_vector[0], mOSP1.rotation_vector[1], mOSP1.rotation_vector[2]);
                Matrix.translateM(osp_ad_matrix, 0, -mOSP1.mean_position[0], -mOSP1.mean_position[1], -mOSP1.mean_position[2]);
            }

            mOSP2 = new OSP(model_files[4], new float[]{1f, 0.54902f, 0f}, 0.25f);
            AllSTLLoadingCheck[4] = mOSP2.isLoaded();

            FileReader fileReader = new FileReader();
            poor = fileReader.ReadPoorPoints(model_files[7]);
            if (!Float.isNaN(poor[0])) {
                poor = new float[]{poor[0], poor[1], poor[2],
                        (poor[3] + poor[6]) / 2.0f, (poor[4] + poor[7]) / 2.0f, (poor[5] + poor[8]) / 2.0f,
                        poor[9], poor[10], poor[11],
                };

                poor = VectorCal.getNormByPtArray(poor);
                poor = new float[]{0, poor[1], poor[2]};
                angleFH = (float) Math.acos((double) VectorCal.dot(poor, new float[]{0.0f, 0.0f, 1.0f})) * 180f / 3.1415926f;
                Log.d("mAngleX", angleFH + "");
                angleFH += 90;
                angleFH = -angleFH;
            }

            all_stl_check();
        }
    });

    private void load_matrix(GL10 gl) {
        gl.glLoadIdentity();

        gl.glScalef(z_shift, z_shift, z_shift);
        //zooming stack
        gl.glTranslatef(x_shift, y_shift, 0);

        //rotational stack
        float[] temp = new float[16];
        Matrix.setIdentityM(rotation_stack_matrix, 0);
        Matrix.rotateM(rotation_stack_matrix, 0, mAngleX, 1, 0, 0);
        Matrix.rotateM(rotation_stack_matrix, 0, mAngleY, 0, 1, 0);
        mAngleX = 0;
        mAngleY = 0;
        Matrix.multiplyMM(temp, 0, rotation_stack_matrix, 0, last_rotaion_matrix, 0);
        last_rotaion_matrix = temp;

        //drawing Matrix
        gl.glMultMatrixf(temp, 0);
        gl.glRotatef(angleFH, 1, 0, 0);
        gl.glMultMatrixf(osp_ad_matrix, 0);
    }


    private void all_stl_check() {
        boolean temp = true;
        for (int i = 0; i < 5; i++) {
            temp = temp & AllSTLLoadingCheck[i];
        }
        sTLLoadingCheck = temp;
    }

    void setRotationIdendity() {
        Matrix.setIdentityM(last_rotaion_matrix, 0);
    }

    void setAngleX(float angle) {
        mAngleX = angle;
    }

    void setAngleY(float angle) {
        mAngleY = angle;
    }

    void setZoom(float zoom) {
        z_shift += zoom;
    }

    void setY_shift(float y) {
        y_shift += y;
    }

    void setX_shift(float X) {
        x_shift += X;
    }

    void resetZoom() {
        z_shift = 1;
        x_shift = 0;
        y_shift = 0;
    }
}
