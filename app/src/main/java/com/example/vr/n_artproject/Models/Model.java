package com.example.vr.n_artproject.Models;

import android.opengl.GLES20;
import android.opengl.GLES32;
import android.util.Log;

import com.example.vr.n_artproject.LoaderNCalculater.FileReader;
import com.example.vr.n_artproject.LoaderNCalculater.VectorCal;

import java.nio.FloatBuffer;
import org.artoolkit.ar.base.rendering.RenderUtils;


import javax.microedition.khronos.opengles.GL10;

/**
 * Created by miles on 2015/11/17.
 */
public class Model {
    private FileReader fileReader;

    protected FloatBuffer vertexBuffer;
    protected FloatBuffer colorBuffer;
    private FloatBuffer normalBuffer;

    //STL and render data
    protected float[] vertex;
    private float color[];

    //Loaded flag
    private boolean Loaded = false;

    public Model(String string, float alpha) {
        fileReader = new FileReader();
        vertex = fileReader.ReadStlBinary(string);

        if(!Float.isNaN(vertex[0])) {
            float[] normals = VectorCal.getNormByPtArray(vertex);
            setColor(new float[]{0.5f, 0.5f, 0.5f}, alpha);
            Log.v(string + " loaded: ", "Loaded");
            Loaded = true;

            vertexBuffer = RenderUtils.buildFloatBuffer(vertex);
            normalBuffer = RenderUtils.buildFloatBuffer(normals);
            colorBuffer = RenderUtils.buildFloatBuffer(color);

        }else {
            Log.v(string + "loaded E*: ", "UnLoaded");
        }
    }

    public boolean isLoaded(){
        return Loaded;
    }

    protected void setColor(float[] in_color, float alpha){
        in_color = new float[]{in_color[0], in_color[1], in_color[2], alpha};
        color = new float[vertex.length/3*4];
        for(int i = 0; i < color.length/4 ; i++)
        {
            for(int j = 0; j < 4; j++){
                color[i+j] = in_color[j];
            }
        }
        colorBuffer = RenderUtils.buildFloatBuffer(color);
    }

    public void draw(GL10 gl) {

        gl.glColorPointer(4, GL10.GL_FLOAT, 0, colorBuffer);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
        gl.glNormalPointer(GL10.GL_FLOAT, 0, normalBuffer);

        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);

        gl.glDrawArrays(GL10.GL_TRIANGLES, 0, vertex.length / 3);

        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);

    }
}
