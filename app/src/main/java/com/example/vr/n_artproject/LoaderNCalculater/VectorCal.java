package com.example.vr.n_artproject.LoaderNCalculater;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.IntBuffer;

/**
 * Created by miles on 2015/8/11.
 */
public class VectorCal {

    public static float getAngleDeg(float[] p1, float[] p2) {
        float ans = (float) (Math.acos(dot(p1, p2) / (magnitude(p1) * magnitude(p2))) / 3.1415926 * 180.0);
        if (Float.isNaN(ans)) {
            return Float.NaN;
        } else {
            return ans;
        }
    }

    public static float[] getVecByPoint(float[] p1, float[] p2) {
        float[] result = new float[3];
        for (int i = 0; i < 3; i++) {
            result[i] = p2[i] - p1[i];
        }
        normalize(result);
        return result;
    }

    public static float[] getNormByPtArray(float[] ptArray) {
        float[] result = new float[ptArray.length];
        for (int i = 0; i < ptArray.length / 9; i++) {
            float[] temp;
            float[] v1;
            float[] v2;

            int k = 9 * i;

            float[] p1 = new float[]{ptArray[k], ptArray[k + 1], ptArray[k + 2]};
            float[] p2 = new float[]{ptArray[k + 3], ptArray[k + 4], ptArray[k + 5]};
            float[] p3 = new float[]{ptArray[k + 6], ptArray[k + 7], ptArray[k + 8]};

            v1 = getVecByPoint(p1, p2);
            v2 = getVecByPoint(p2, p3);

            temp = cross(v1, v2);
            normalize(temp);

            result[k] = temp[0];
            result[k + 1] = temp[1];
            result[k + 2] = temp[2];

            result[k + 3] = temp[0];
            result[k + 4] = temp[1];
            result[k + 5] = temp[2];

            result[k + 6] = temp[0];
            result[k + 7] = temp[1];
            result[k + 8] = temp[2];
        }
        return result;
    }

    public static float dot(float[] v1, float[] v2) {
        float res = 0;
        for (int i = 0; i < v1.length; i++)
            res += v1[i] * v2[i];
        return res;
    }

    public static float[] cross(float[] p1, float[] p2) {
        float[] result = new float[3];
        result[0] = p1[1] * p2[2] - p2[1] * p1[2];
        result[1] = p1[2] * p2[0] - p2[2] * p1[0];
        result[2] = p1[0] * p2[1] - p2[0] * p1[1];
        normalize(result);
        return result;
    }

    public static void normalize(float[] vector) {
        scalarMultiply(vector, 1 / magnitude(vector));
    }

    public static void scalarMultiply(float[] vector, float scalar) {
        for (int i = 0; i < vector.length; i++)
            vector[i] *= scalar;
    }

    public static float magnitude(float[] vector) {
        return (float) Math.sqrt(vector[0] * vector[0] +
                vector[1] * vector[1] +
                vector[2] * vector[2]);
    }


    public static float[] rotAngMatrixMultiVec(float[] points, float rotAng, float[] rotVn) {

        float[] transMatrix = new float[16];
        Matrix.setIdentityM(transMatrix, 0);

        float[] balls1 = new float[]{points[0], points[1], points[2], 1};
        float[] balls2 = new float[]{points[3], points[4], points[5], 1};
        float[] balls3 = new float[]{points[6], points[7], points[8], 1};

        Matrix.rotateM(transMatrix, 0, rotAng, rotVn[0], rotVn[1], rotVn[2]);

        Matrix.multiplyMV(balls1, 0, transMatrix, 0, balls1, 0);
        Matrix.multiplyMV(balls2, 0, transMatrix, 0, balls2, 0);
        Matrix.multiplyMV(balls3, 0, transMatrix, 0, balls3, 0);

        return new float[]{balls1[0], balls1[1], balls1[2],
                balls2[0], balls2[1], balls2[2],
                balls3[0], balls3[1], balls3[2]};

    }

    public static float[] rotMatrixMultiVec(float[] points, float[] matrix) {


        float[] balls1 = new float[]{points[0], points[1], points[2], 1};
        float[] balls2 = new float[]{points[3], points[4], points[5], 1};
        float[] balls3 = new float[]{points[6], points[7], points[8], 1};

        Matrix.multiplyMV(balls1, 0, matrix, 0, balls1, 0);
        Matrix.multiplyMV(balls2, 0, matrix, 0, balls2, 0);
        Matrix.multiplyMV(balls3, 0, matrix, 0, balls3, 0);

        return new float[]{balls1[0], balls1[1], balls1[2],
                balls2[0], balls2[1], balls2[2],
                balls3[0], balls3[1], balls3[2]};

    }

    public static Bitmap SavePixels(int x, int y, int w, int h){
        int b[]=new int[w*(y+h)];
        int bt[]=new int[w*h];
        IntBuffer ib = IntBuffer.wrap(b);
        ib.position(0);
        GLES20.glReadPixels(0, 0, w, h, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, ib);

        for(int i=0, k=0; i<h; i++, k++)
        {//remember, that OpenGL bitmap is incompatible with Android bitmap
            //and so, some correction need.
            for(int j=0; j<w; j++)
            {
                int pix=b[i*w+j];
                int pb=(pix>>16)&0xff;
                int pr=(pix<<16)&0x00ff0000;
                int pix1=(pix&0xff00ff00) | pr | pb;
                bt[(h-k-1)*w+j]=pix1;
            }
        }

        Bitmap sb=Bitmap.createBitmap(bt, w, h, Bitmap.Config.ARGB_8888);
        return sb;
    }
}
