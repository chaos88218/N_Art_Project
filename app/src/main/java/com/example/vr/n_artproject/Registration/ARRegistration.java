package com.example.vr.n_artproject.Registration;

import android.opengl.Matrix;

import com.example.vr.n_artproject.LoaderNCalculater.VectorCal;

/**
 * Created by miles on 2015/10/16.
 */
public class ARRegistration {
    //TODO: rework transformation from angle and axis to matrix

    private boolean loaded = false;

    private float[] resultMatrix = new float[16];


    public ARRegistration(String in_arproject, String in_arpoints) {

        ProMarker proMarker = new ProMarker(in_arproject);
        Arpoints arpoints = new Arpoints(in_arpoints);

        if ((proMarker.isLoaded() && arpoints.isLoaded())) {
            float[] skullMSPoints = proMarker.get_skullMSPoints();
            float[] skullMSARPoints = arpoints.getSkullMSARPoints();

            float[] testpoint = skullMSPoints.clone();
            float[] temp = new float[]{testpoint[0], testpoint[1], testpoint[2]};
            float[] aRRotationMatrix = new float[16];
            Matrix.setIdentityM(aRRotationMatrix, 0);

            float[] aRTransVec = new float[]{-temp[0], -temp[1], -temp[2]};
            for (int ii = 0; ii < 9; ii += 3) {
                skullMSPoints[0 + ii] = skullMSPoints[0 + ii] - temp[0];
                skullMSPoints[1 + ii] = skullMSPoints[1 + ii] - temp[1];
                skullMSPoints[2 + ii] = skullMSPoints[2 + ii] - temp[2];

                testpoint[0 + ii] = testpoint[ii + 0] - temp[0];
                testpoint[1 + ii] = testpoint[ii + 1] - temp[1];
                testpoint[2 + ii] = testpoint[ii + 2] - temp[2];
            }

            float[] vnAR = VectorCal.getNormByPtArray(skullMSARPoints);

            float[] Vec1;
            float[] Vec2;
            Vec1 = VectorCal.getVecByPoint(new float[]{skullMSPoints[0], skullMSPoints[1], skullMSPoints[2]},
                    new float[]{skullMSPoints[3], skullMSPoints[4], skullMSPoints[5]});
            VectorCal.normalize(Vec1);
            Vec2 = VectorCal.getVecByPoint(new float[]{skullMSARPoints[0], skullMSARPoints[1], skullMSARPoints[2]},
                    new float[]{skullMSARPoints[3], skullMSARPoints[4], skullMSARPoints[5]});
            VectorCal.normalize(Vec2);

            float[] reAxis1 = VectorCal.cross(Vec1, Vec2);
            float reAngle1 = VectorCal.getAngleDeg(Vec1, Vec2);

            skullMSPoints = VectorCal.rotAngMatrixMultiVec(skullMSPoints, reAngle1, reAxis1);
            Matrix.rotateM(aRRotationMatrix, 0, reAngle1, reAxis1[0], reAxis1[1], reAxis1[2]);//*****************************

            float[] Vec3;
            float[] Vec4;
            float[] vnCT = VectorCal.getNormByPtArray(skullMSPoints);
            Vec1 = VectorCal.getVecByPoint(new float[]{skullMSPoints[0], skullMSPoints[1], skullMSPoints[2]},
                    new float[]{skullMSPoints[3], skullMSPoints[4], skullMSPoints[5]});
            VectorCal.normalize(Vec1);
            Vec2 = VectorCal.getVecByPoint(new float[]{skullMSARPoints[0], skullMSARPoints[1], skullMSARPoints[2]},
                    new float[]{skullMSARPoints[3], skullMSARPoints[4], skullMSARPoints[5]});
            VectorCal.normalize(Vec2);

            Vec3 = VectorCal.cross(vnCT, Vec1);
            Vec4 = VectorCal.cross(vnAR, Vec2);

            float[] reAxis2 = VectorCal.cross(Vec3, Vec4);
            float reAngle2 = VectorCal.getAngleDeg(Vec3, Vec4);

            skullMSPoints = VectorCal.rotAngMatrixMultiVec(skullMSPoints, reAngle2, reAxis2);
            Matrix.rotateM(aRRotationMatrix, 0, reAngle2, reAxis2[0], reAxis2[1], reAxis2[2]);//*****************************

            testpoint = VectorCal.rotMatrixMultiVec(testpoint, aRRotationMatrix);

            for (int ii = 0; ii < 9; ii += 3) {
                skullMSPoints[0 + ii] = skullMSPoints[0 + ii] + skullMSARPoints[0];
                skullMSPoints[1 + ii] = skullMSPoints[1 + ii] + skullMSARPoints[1];
                skullMSPoints[2 + ii] = skullMSPoints[2 + ii] + skullMSARPoints[2];

                testpoint[0 + ii] = testpoint[0 + ii] + skullMSARPoints[0];
                testpoint[1 + ii] = testpoint[1 + ii] + skullMSARPoints[1];
                testpoint[2 + ii] = testpoint[2 + ii] + skullMSARPoints[2];
            }
            float[] aRTransBackVec = new float[]{skullMSARPoints[0], skullMSARPoints[1], skullMSARPoints[2]};

            loaded = true;

            Matrix.setIdentityM(resultMatrix, 0);
            Matrix.translateM(resultMatrix, 0, aRTransBackVec[0], aRTransBackVec[1], aRTransBackVec[2]);
            Matrix.rotateM(resultMatrix, 0, reAngle2, reAxis2[0], reAxis2[1], reAxis2[2]);
            Matrix.rotateM(resultMatrix, 0, reAngle1, reAxis1[0], reAxis1[1], reAxis1[2]);
            Matrix.translateM(resultMatrix, 0, aRTransVec[0], aRTransVec[1], aRTransVec[2]);
        }
    }

    public boolean isLoaded() {
        return loaded;
    }

    public float[] drawing_matrix() {
        return resultMatrix;
    }
}
