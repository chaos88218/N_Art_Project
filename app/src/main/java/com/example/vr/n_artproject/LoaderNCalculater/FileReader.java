package com.example.vr.n_artproject.LoaderNCalculater;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by miles on 2015/9/23.
 */
public class FileReader {
    //VSCM Calibration file reader
    public float[] ARSReadTxt(String fileName) {
        float[] ospVert = new float[17];
        BufferedReader br;
        try {
            br = new BufferedReader(new java.io.FileReader("/sdcard/" + fileName));

            String str = br.readLine();
            ospVert[0] = Float.valueOf(str);

            str = br.readLine();
            String[] matrix = str.split("\\t");
            for (int i = 0; i < matrix.length; i++) {
                ospVert[i + 1] = Float.valueOf(matrix[i]);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return new float[]{Float.NaN};
        } catch (IOException e) {
            e.printStackTrace();
            return new float[]{Float.NaN};
        }
        return ospVert;
    }

    //ARToolkit see-through Calibration file reader
    public float[] ARTReadBinary(String fileName) {
        double[] ospVert = new double[18];
        File file = new File("/sdcard/" + fileName);
        InputStream inputStream = null;

        try {
            inputStream = new FileInputStream(file);

            byte[] buffer = new byte[144];
            inputStream.read(buffer);

            for (int Line = 0; Line < 18; Line++) {
                ByteBuffer temp = ByteBuffer.wrap(buffer, Line * 8, 8);
                ospVert[Line] = temp.getDouble();
            }
            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return new float[]{Float.NaN};
        } catch (IOException e) {
            e.printStackTrace();
            return new float[]{Float.NaN};
        } finally {
            if (inputStream != null)
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return new float[]{Float.NaN};
                }
        }

        return new float[]{
                (float) ospVert[0], (float) ospVert[1],
                (float) ospVert[2], (float) ospVert[3], (float) ospVert[4], (float) ospVert[5],
                (float) ospVert[6], (float) ospVert[7], (float) ospVert[8], (float) ospVert[9],
                (float) ospVert[10], (float) ospVert[11], (float) ospVert[12], (float) ospVert[13],
                (float) ospVert[14], (float) ospVert[15], (float) ospVert[16], (float) ospVert[17],
        };
    }

    //STL binary reader
    public float[] ReadStlBinary(String fileName) {
        float[] ospVert = new float[0];
        File file = new File("/sdcard/" + fileName);
        InputStream inputStream = null;

        try {
            inputStream = new FileInputStream(file);

            int count;
            byte[] buffer = new byte[84];
            inputStream.read(buffer);
            count = ByteBuffer.wrap(buffer, 80, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();

            ospVert = new float[count * 9];
            buffer = new byte[50 * count];
            inputStream.read(buffer);
            int num1 = 0;
            int num2 = 0;

            for (int Line = 0; Line < count; Line++) {
                ByteBuffer temp = ByteBuffer.wrap(buffer, num2 + 12, 36).order(ByteOrder.LITTLE_ENDIAN);
                for (int jjj = 0; jjj < 9; jjj++) {
                    ospVert[num1 + jjj] = temp.getFloat();
                }
                num1 += 9;
                num2 += 50;
            }
            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return new float[]{Float.NaN};
        } catch (IOException e) {
            e.printStackTrace();
            return new float[]{Float.NaN};
        } finally {
            if (inputStream != null)
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return new float[]{Float.NaN};
                }
        }

        return ospVert;
    }

    //N-art registration project reader
    public float[] ReadProjectSkullMarker(String projectName) {
        float[] s_Markers = new float[27];
        BufferedReader br;
        String str;
        try {
            br = new BufferedReader(new java.io.FileReader("/sdcard/" + projectName));

            for (int i = 0; i < 15; i++) {    //Skip 15 lines
                br.readLine();
            }
            for (int i = 0; i < 9; i++) {
                str = br.readLine();
                String[] marker_value = str.split("\\s+"); //space

                for (int j = 0; j < 3; j++) {
                    s_Markers[3 * i + j] = Float.valueOf(marker_value[j]);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return new float[]{Float.NaN};
        } catch (IOException e) {
            e.printStackTrace();
            return new float[]{Float.NaN};
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            return new float[]{Float.NaN};
        }
        return s_Markers;
    }

    //AR Tag and skul-LED points reader
    public float[] ReadArPoints(String projectName) {
        float[] s_Markers = new float[21];
        String str;
        BufferedReader br;

        try {
            br = new BufferedReader(new java.io.FileReader("/sdcard/" + projectName));

            for (int i = 0; i < 7; i++) {
                str = br.readLine();
                String[] marker_value = str.split("\\t"); //space

                for (int j = 0; j < 3; j++) {
                    s_Markers[3 * i + j] = Float.valueOf(marker_value[j]);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return new float[]{Float.NaN};
        } catch (IOException e) {
            e.printStackTrace();
            return new float[]{Float.NaN};
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            return new float[]{Float.NaN};
        }
        return s_Markers;
    }
}
