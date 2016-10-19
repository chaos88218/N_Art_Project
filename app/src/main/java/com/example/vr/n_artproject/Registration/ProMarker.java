package com.example.vr.n_artproject.Registration;

import android.util.Log;

import com.example.vr.n_artproject.LoaderNCalculater.FileReader;
import com.example.vr.n_artproject.LoaderNCalculater.VectorCal;

import java.util.Arrays;

/**
 * Created by miles on 2015/10/8.
 */
class ProMarker {

    private boolean loaded = false;

    private float[] skullMSPoints = new float[9];//skull marker on

    ProMarker(String string) {
        FileReader fileReader = new FileReader();
        float[] squareCoords = fileReader.ReadProjectSkullMarker(string);

        if (!Float.isNaN(squareCoords[0])) {
            Log.v("ProMarker1Loaded: ", "Loaded");
            loaded = true;

            float[] ballsCTPoints = Arrays.copyOfRange(squareCoords, 0, 9);
            float[] ballsMSPoints = Arrays.copyOfRange(squareCoords, 9, 18);
            skullMSPoints = Arrays.copyOfRange(squareCoords, 18, 27);
            float[] temp = new float[]{ballsMSPoints[0], ballsMSPoints[1], ballsMSPoints[2]};


            for (int ii = 0; ii < 9; ii += 3) {
                ballsMSPoints[0 + ii] = ballsMSPoints[0 + ii] - temp[0];
                ballsMSPoints[1 + ii] = ballsMSPoints[1 + ii] - temp[1];
                ballsMSPoints[2 + ii] = ballsMSPoints[2 + ii] - temp[2];

                skullMSPoints[0 + ii] = skullMSPoints[0 + ii] - temp[0];
                skullMSPoints[1 + ii] = skullMSPoints[1 + ii] - temp[1];
                skullMSPoints[2 + ii] = skullMSPoints[2 + ii] - temp[2];
            }

            float[] vnCT = VectorCal.getNormByPtArray(ballsCTPoints);

            float[] Vec1;
            float[] Vec2;
            Vec1 = VectorCal.getVecByPoint(new float[]{ballsMSPoints[0], ballsMSPoints[1], ballsMSPoints[2]},
                    new float[]{ballsMSPoints[3], ballsMSPoints[4], ballsMSPoints[5]});
            VectorCal.normalize(Vec1);
            Vec2 = VectorCal.getVecByPoint(new float[]{ballsCTPoints[0], ballsCTPoints[1], ballsCTPoints[2]},
                    new float[]{ballsCTPoints[3], ballsCTPoints[4], ballsCTPoints[5]});
            VectorCal.normalize(Vec2);

            float[] rotVn = VectorCal.cross(Vec1, Vec2);
            float rotAng = VectorCal.getAngleDeg(Vec1, Vec2);

            skullMSPoints = VectorCal.rotAngMatrixMultiVec(skullMSPoints, rotAng, rotVn);//*********************
            ballsMSPoints = VectorCal.rotAngMatrixMultiVec(ballsMSPoints, rotAng, rotVn);//*********************

            float[] Vec3;
            float[] Vec4;

            float[] vnMS = VectorCal.getNormByPtArray(ballsMSPoints);
            Vec1 = VectorCal.getVecByPoint(new float[]{ballsMSPoints[0], ballsMSPoints[1], ballsMSPoints[2]},
                    new float[]{ballsMSPoints[3], ballsMSPoints[4], ballsMSPoints[5]});
            VectorCal.normalize(Vec1);
            Vec2 = VectorCal.getVecByPoint(new float[]{ballsCTPoints[0], ballsCTPoints[1], ballsCTPoints[2]},
                    new float[]{ballsCTPoints[3], ballsCTPoints[4], ballsCTPoints[5]});
            VectorCal.normalize(Vec2);

            Vec3 = VectorCal.cross(vnMS, Vec1);
            Vec4 = VectorCal.cross(vnCT, Vec2);

            rotAng = VectorCal.getAngleDeg(Vec3, Vec4);
            rotVn = VectorCal.cross(Vec3, Vec4);

            skullMSPoints = VectorCal.rotAngMatrixMultiVec(skullMSPoints, rotAng, rotVn);//*********************
            ballsMSPoints = VectorCal.rotAngMatrixMultiVec(ballsMSPoints, rotAng, rotVn);//*********************

            for (int ii = 0; ii < 9; ii += 3) {
                ballsMSPoints[0 + ii] = ballsMSPoints[0 + ii] + ballsCTPoints[0];
                ballsMSPoints[1 + ii] = ballsMSPoints[1 + ii] + ballsCTPoints[1];
                ballsMSPoints[2 + ii] = ballsMSPoints[2 + ii] + ballsCTPoints[2];

                skullMSPoints[0 + ii] = skullMSPoints[0 + ii] + ballsCTPoints[0];
                skullMSPoints[1 + ii] = skullMSPoints[1 + ii] + ballsCTPoints[1];
                skullMSPoints[2 + ii] = skullMSPoints[2 + ii] + ballsCTPoints[2];
            }

        } else {
            Log.v(string + " ProMarkerLoaded E*: ", "UnLoaded");
        }

    }

    float[] get_skullMSPoints() {
        return skullMSPoints;
    }

    boolean isLoaded() {
        return loaded;
    }
}
