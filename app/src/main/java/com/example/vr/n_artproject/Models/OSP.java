package com.example.vr.n_artproject.Models;

import android.opengl.GLES10;
import android.util.Log;

import com.example.vr.n_artproject.LoaderNCalculater.VectorCal;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by miles on 2015/11/17.
 */
public class OSP extends Model {

    BorderLines borderLines;

    //mean position for model viewing
    public float[] mean_position = new float[]{0, 0, 0};
    public float[] rotation_vector = new float[]{1, 0, 0};
    public float normal_angle = 0;

    public OSP(String string, float[] in_color, float alpha) {
        super(string, alpha);
        setColor(in_color, alpha);
        if (isLoaded()) {
            borderLines = new BorderLines(this.vertex, in_color);
            calculateMean();
            calculateAngle();
        }
    }

    private void calculateMean() {
        mean_position = new float[]{0, 0, 0};
        for (int k = 0; k < this.vertex.length; k++) {
            mean_position[k % 3] += this.vertex[k];
        }
        for (int i = 0; i < 3; i++) {
            mean_position[i] /= (this.vertex.length / 3.0);
        }
    }

    private void calculateAngle() {
        float[] normal_vector = VectorCal.getNormByPtArray(this.vertex);
        normal_vector = new float[]{normal_vector[0], normal_vector[1], normal_vector[2]};
        if (normal_vector[0] < 0) {
            for (int i = 0; i < 3; i++) {
                normal_vector[i] = -normal_vector[i];
            }
        }

        normal_angle = (float) Math.acos((double) VectorCal.dot(normal_vector, new float[]{1.0f, 0.0f, 0.0f})) * 180f / 3.1415926f;
        rotation_vector = VectorCal.cross(normal_vector, new float[]{1.0f, 0.0f, 0.0f});
    }

    @Override
    protected void setColor(float[] in_color, float alpha) {
        super.setColor(in_color, alpha);
    }

    @Override
    public void draw(GL10 gl) {
        GLES10.glEnable(GLES10.GL_BLEND);
        GLES10.glBlendFunc(GLES10.GL_SRC_ALPHA, GLES10.GL_ONE_MINUS_SRC_ALPHA);

        gl.glColorPointer(4, GL10.GL_FLOAT, 0, this.colorBuffer);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, this.vertexBuffer);

        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

        gl.glDisable(GL10.GL_CULL_FACE);
        gl.glDrawArrays(GL10.GL_TRIANGLES, 0, this.vertex.length / 3);
        gl.glEnable(GL10.GL_CULL_FACE);

        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);

        GLES10.glDisable(GLES10.GL_BLEND);


        borderLines.draw(gl);
    }
}
