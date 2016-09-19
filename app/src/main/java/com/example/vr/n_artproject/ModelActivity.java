package com.example.vr.n_artproject;

import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vr.n_artproject.Communication.TCPThread;
import com.example.vr.n_artproject.Graphic.MyGLSurfaceView;
import com.sime.speech.SiMESpeechRecognitionListener;
import com.sime.speech.SiMESpeechRecognizer;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ModelActivity extends AppCompatActivity {

    //SiME SDK Speech recognizer
    private SiMESpeechRecognizer mRecognizer;
    private SiMESpeechRecognitionListener mListener;

    //Thread and handler
    TCPThread tcpThread;
    Thread uI_update;

    //Views
    private GLSurfaceView mGLView;
    private RelativeLayout glViewLayout;
    private RelativeLayout glViewParentLayout;
    private LinearLayout numbersLayout;

    private ImageView glShotImageView;
    private ImageView fiveNumbersImageView;

    public Button sockConn;

    public EditText IP_text;
    private TextView recvout;
    private TextView[] five_num_text = new TextView[5];

    //Numbers and data
    public String[] fileNames;
    public static Bitmap glShotbitmap;
    public float[] matrix1;
    public float[] matrix2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getIntent().getExtras();
        fileNames = bundle.getStringArray("FILE_NAME");

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mGLView = new MyGLSurfaceView(this, fileNames);
        setContentView(R.layout.activity_model);

        //five numbers
        numbersLayout = (LinearLayout) findViewById(R.id.numbersLayout);
        five_num_text[0] = (TextView) findViewById(R.id.DAText);
        five_num_text[1] = (TextView) findViewById(R.id.DDText);
        five_num_text[2] = (TextView) findViewById(R.id.FDAText);
        five_num_text[3] = (TextView) findViewById(R.id.HDAText);
        five_num_text[4] = (TextView) findViewById(R.id.PDDText);

        //Screenshot control
        glViewParentLayout = (RelativeLayout) findViewById(R.id.glParentRelLayout);
        glShotImageView = (ImageView) findViewById(R.id.glShotImage);
        fiveNumbersImageView = (ImageView) findViewById(R.id.fiveNumbersImage);

        //ip, connect and download
        IP_text = (EditText) findViewById(R.id.ip_text);
        sockConn = (Button) findViewById(R.id.SockBut);
        sockConn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thread_start();
            }
        });

        //message and graphic
        recvout = (TextView) findViewById(R.id.RecText);
        recvout.setMovementMethod(new ScrollingMovementMethod());
        glViewLayout = (RelativeLayout) findViewById(R.id.GLview);
        glViewLayout.addView(mGLView);

        //SiME Speech recognizer
        mRecognizer = new SiMESpeechRecognizer(this);
        mRecognizer.setLanguageModelFiles("0901.dic", "0901.lm");
        mRecognizer.setKeyPhrase("GLASS");
        mRecognizer.setKeywordThreshold("1e-20");
        mRecognizer.setVadThreshold("3.0f");
        mRecognizer.setBeam("1e-45f");
        mRecognizer.setListeningTimeout(6000);
        mRecognizer.setOSDBitmap(new String[]{
                "SiME Speech1.png",
                "SiME Speech2.png",
                "SiME Speech3.png",
                "SiME Speech4.png",
                "SiME Speech5.png",
                "SiME Speech6.png",
                "SiME Speech7.png",
                "SiME Speech8.png",
                "SiME Speech9.png"}, 50);
        mRecognizer.setEnabledOSDView(true);
        mListener = new SiMESpeechRecognitionListener() {
            @Override
            public void onResult(final String resultword) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("resultword: ", resultword);
                        Toast.makeText(ModelActivity.this, resultword, Toast.LENGTH_SHORT).show();
                        if (resultword.equals("CONNECT")) {
                            sockConn.setEnabled(false);
                            thread_start();
                        }

                        if (resultword.equals("ANTERIOR")) {
//                            Matrix.setIdentityM(MyGLRenderer.mAccumulatedMatrix, 0);
//                            MyGLRenderer.setAngleX(-90.0f);
//                            MyGLRenderer.setAngleY(0.0f);
                        }
                        if (resultword.equals("POSTERIOR")) {
//                            Matrix.setIdentityM(MyGLRenderer.mAccumulatedMatrix, 0);
//                            MyGLRenderer.setAngleX(-90.0f);
//                            MyGLRenderer.setAngleY(180.0f);
                        }
                        if (resultword.equals("SUPERIOR")) {
//                            Matrix.setIdentityM(MyGLRenderer.mAccumulatedMatrix, 0);
//                            MyGLRenderer.setAngleX(0.0f);
//                            MyGLRenderer.setAngleY(0.0f);
                        }
                        if (resultword.equals("INFERIOR")) {
//                            Matrix.setIdentityM(MyGLRenderer.mAccumulatedMatrix, 0);
//                            MyGLRenderer.setAngleX(180.0f);
//                            MyGLRenderer.setAngleY(0.0f);
                        }
                        if (resultword.equals("RIGHT")) {
//                            Matrix.setIdentityM(MyGLRenderer.mAccumulatedMatrix, 0);
//                            MyGLRenderer.setAngleX(-90.0f);
//                            MyGLRenderer.setAngleY(90.0f);
                        }
                        if (resultword.equals("LEFT")) {
//                            Matrix.setIdentityM(MyGLRenderer.mAccumulatedMatrix, 0);
//                            MyGLRenderer.setAngleX(-90.0f);
//                            MyGLRenderer.setAngleY(-90.0f);
                        }
                        if (resultword.equals("SHOT")) {
//                            MyGLRenderer.Snap = true;
//                            glShotImageView.setVisibility(View.VISIBLE);
//
//                            numbersLayout.setDrawingCacheEnabled(true);
//                            Bitmap bitmap = Bitmap.createBitmap(numbersLayout.getDrawingCache());
//                            numbersLayout.setDrawingCacheEnabled(false);
//                            fiveNumbersImageView.setVisibility(View.VISIBLE);

//                            while (!MyGLRenderer.Snaped) {
//                            }
//                            fiveNumbersImageView.setImageBitmap(bitmap);
//                            numbersLayout.setVisibility(View.GONE);
//                            glShotImageView.setImageBitmap(glShotbitmap);
//                            MyGLRenderer.Snaped = false;
                        }
                        if (resultword.equals("REFRESH")) {
//                            MyGLRenderer.Snap = false;
//                            glShotImageView.setVisibility(View.GONE);
//                            fiveNumbersImageView.setVisibility(View.GONE);
//                            numbersLayout.setVisibility(View.VISIBLE);
//                            MyGLRenderer.Snaped = false;
                        }
                        if (resultword.equals("SAVE")) {
                            Date date = new Date();
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
                            String dateString = sdf.format(date);

                            try {
                                String mPath = "/sdcard/" + dateString + ".jpg";
                                glViewParentLayout.setDrawingCacheEnabled(true);
                                Bitmap bitmap = Bitmap.createBitmap(glViewParentLayout.getDrawingCache());
                                glViewParentLayout.setDrawingCacheEnabled(false);

                                File imageFile = new File(mPath);
                                Log.e("File path", imageFile.getAbsolutePath());
                                FileOutputStream outputStream = new FileOutputStream(imageFile);
                                int quality = 100;
                                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
                                outputStream.flush();
                                outputStream.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            glShotbitmap = null;
                            glShotImageView.setVisibility(View.GONE);
                            fiveNumbersImageView.setVisibility(View.GONE);
                            numbersLayout.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        };
        mRecognizer.connectService();
        mRecognizer.registerListener(mListener);

    }


    @Override
    protected void onResume() {
        super.onResume();
        mRecognizer.registerListener(mListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mRecognizer.unregisterListener(mListener);
    }

    @Override
    protected void onDestroy() {
        mRecognizer.disconnectService();
        super.onDestroy();
    }

    private void thread_start() {
        sockConn.setEnabled(false);
        tcpThread = new TCPThread(IP_text.getText().toString());
        uI_update = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (tcpThread.run_or_n) {
                        Thread.sleep(50);
                        matrix1 = tcpThread.get_matrix(0);
                        matrix2 = tcpThread.get_matrix(1);
                        tcpThread.setFive_num_text(five_num_text);
                        tcpThread.setLogMes(recvout);
                    }
                } catch (Exception e) {
                    Log.e("ServerUP", e + "");
                }
                tcpThread.SockConHandle(sockConn, true);
            }
        });
        tcpThread.thread_start();
        Log.d("ServerUP", tcpThread.run_or_n + "");
        uI_update.start();
    }


}