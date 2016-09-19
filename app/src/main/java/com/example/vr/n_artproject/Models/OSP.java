package com.example.vr.n_artproject.Models;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by miles on 2015/11/17.
 */
public class OSP extends Model {

    //mean position for model viewing
    public float[] mean_position = new float[]{0, 0, 0};

    public OSP(String string, float[] in_color, float alpha) {
        super(string, alpha);
        setColor(in_color, alpha);
    }

    public void calculateMean() {
        mean_position = new float[]{0, 0, 0};
        for (int k = 0; k < this.vertex.length; k++) {
            mean_position[k % 3] += this.vertex[k];
        }
        for (int i = 0; i < 3; i++) {
            mean_position[i] /= (this.vertex.length / 3.0);
        }
    }

    @Override
    protected void setColor(float[] in_color, float alpha) {
        super.setColor(in_color, alpha);
    }

    @Override
    public void draw(GL10 gl) {
        gl.glColorPointer(4, GL10.GL_FLOAT, 0, this.colorBuffer);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, this.vertexBuffer);

        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

        gl.glDisable(GL10.GL_CULL_FACE);
        gl.glDrawArrays(GL10.GL_TRIANGLES, 0, this.vertex.length / 3);
        gl.glEnable(GL10.GL_CULL_FACE);

        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }
}
