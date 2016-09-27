package com.example.vr.n_artproject.Models;

import org.artoolkit.ar.base.rendering.RenderUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by miles on 2016/9/22.
 */

public class BorderLines {
    private ShortBuffer drawLineBuffer;
    private FloatBuffer vertexBuffer;
    private FloatBuffer colorBuffer;

    private short drawOrder[] = {
            0, 1,
            1, 2,
            2, 5,
            5, 0};

    public BorderLines(float[] vertex, float[] in_color){

        //data
        vertexBuffer = RenderUtils.buildFloatBuffer(vertex);
        in_color = new float[]{in_color[0], in_color[1], in_color[2], 1.0f};
        float[] color = new float[vertex.length/3*4];

        for (int i = 0; i < color.length; i++) {
            color[i] = in_color[i % 4];
        }
        colorBuffer = RenderUtils.buildFloatBuffer(color);

        //draw order
        ByteBuffer dlb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
                drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawLineBuffer = dlb.asShortBuffer();
        drawLineBuffer.put(drawOrder);
        drawLineBuffer.position(0);
    }

    public void draw(GL10 gl) {
        //line
        gl.glLineWidth(3);
        gl.glColorPointer(4, GL10.GL_FLOAT, 0, this.colorBuffer);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, this.vertexBuffer);

        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

        gl.glDisable(GL10.GL_CULL_FACE);
        gl.glDrawElements(GL10.GL_LINES, drawOrder.length, GL10.GL_UNSIGNED_SHORT, drawLineBuffer);
        gl.glEnable(GL10.GL_CULL_FACE);

        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }

}
