package com.example.vr.n_artproject.Registration;

import android.util.Log;

import com.example.vr.n_artproject.LoaderNCalculater.FileReader;
import com.example.vr.n_artproject.LoaderNCalculater.VectorCal;


import java.util.Arrays;


/**
 * Created by miles on 2015/10/14.
 */
class Arpoints {

    private boolean loaded = false;

    private float[] skullMSARPoints = new float[9];

    Arpoints(String string) {
        FileReader fileReader = new FileReader();
        float[] squareCoords = fileReader.ReadArPoints(string);

        if (!Float.isNaN(squareCoords[0])) {
            Log.v("ApointsLoaded: ", "Loaded");
            loaded = true;

            float[] aRTagPoints = Arrays.copyOfRange(squareCoords, 0, 12);
            skullMSARPoints = Arrays.copyOfRange(squareCoords, 12, 21);

            float[] mean_position = new float[]{0, 0, 0};
            for (int k = 0; k < aRTagPoints.length; k++) {
                mean_position[k % 3] += aRTagPoints[k];
            }

            for (int i = 0; i < 3; i++) {
                mean_position[i] /= 4;
            }

            for (int ii = 0; ii < 9; ii += 3) {
                aRTagPoints[0 + ii] = aRTagPoints[0 + ii] - mean_position[0];
                aRTagPoints[1 + ii] = aRTagPoints[1 + ii] - mean_position[1];
                aRTagPoints[2 + ii] = aRTagPoints[2 + ii] - mean_position[2];

                skullMSARPoints[0 + ii] = skullMSARPoints[0 + ii] - mean_position[0];
                skullMSARPoints[1 + ii] = skullMSARPoints[1 + ii] - mean_position[1];
                skullMSARPoints[2 + ii] = skullMSARPoints[2 + ii] - mean_position[2];
            }


            float[] Vec1;
            float[] Vec2;
            Vec1 = VectorCal.getVecByPoint(new float[]{aRTagPoints[0], aRTagPoints[1], aRTagPoints[2]},
                    new float[]{aRTagPoints[3], aRTagPoints[4], aRTagPoints[5]});
            VectorCal.normalize(Vec1);

            Vec2 = new float[]{1f, 0f, 0f};

            float[] rotVn = VectorCal.cross(Vec1, Vec2);
            float rotAng = VectorCal.getAngleDeg(Vec1, Vec2);

            aRTagPoints = VectorCal.rotAngMatrixMultiVec(aRTagPoints, rotAng, rotVn);//*********************
            skullMSARPoints = VectorCal.rotAngMatrixMultiVec(skullMSARPoints, rotAng, rotVn);//*********************

            Vec1 = VectorCal.getVecByPoint(new float[]{aRTagPoints[0], aRTagPoints[1], aRTagPoints[2]},
                    new float[]{aRTagPoints[3], aRTagPoints[4], aRTagPoints[5]});
            VectorCal.normalize(Vec1);

            Vec2 = VectorCal.getVecByPoint(new float[]{aRTagPoints[3], aRTagPoints[4], aRTagPoints[5]},
                    new float[]{aRTagPoints[6], aRTagPoints[7], aRTagPoints[8]});
            VectorCal.normalize(Vec2);

            float[] cTVn = VectorCal.cross(Vec1, Vec2);
            Vec2 = VectorCal.cross(cTVn, Vec1);

            rotAng = VectorCal.getAngleDeg(Vec2, new float[]{0, 1, 0});
            rotVn = VectorCal.cross(Vec2, new float[]{0, 1, 0});

            aRTagPoints = VectorCal.rotAngMatrixMultiVec(aRTagPoints, rotAng, rotVn);//*********************
            skullMSARPoints = VectorCal.rotAngMatrixMultiVec(skullMSARPoints, rotAng, rotVn);//*********************

        } else {
            Log.v(string + " ARPointLoaded E*: ", "UnLoaded");
        }

    }

    float[] getSkullMSARPoints() {
        return skullMSARPoints;
    }

    boolean isLoaded() {
        return loaded;
    }
}
