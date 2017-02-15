package com.example.vr.n_artproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.opengl.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vr.n_artproject.Communication.TCPThread;
import com.example.vr.n_artproject.Graphic.MyARRenderer;
import com.sime.speech.SiMESpeechRecognitionListener;
import com.sime.speech.SiMESpeechRecognizer;

import org.artoolkit.ar.base.ARActivity;
import org.artoolkit.ar.base.camera.CameraPreferencesActivity;
import org.artoolkit.ar.base.rendering.ARRenderer;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GlassARActivity extends ARActivity {

    //SiME SDK Speech recognizer
    private SiMESpeechRecognizer mRecognizer;
    private SiMESpeechRecognitionListener mListener;

    //Thread and handler
    TCPThread tcpThread;
    Thread uI_update;

    //Views
    private FrameLayout setting_layout;
    private FrameLayout aRINLayout;
    private RelativeLayout aRLayout;
    private MyARRenderer myARRenderer;

    public EditText iPadd;

    private TextView[] five_num_text = new TextView[5];
    private TextView logMes;

    public Button sockConn;

    //Numbers and data
    private boolean GL_TRANSLUCENT = true;
    public String[] fileNames;
    public static float[] matrix1 = new float[16];
    public static float[] matrix2 = new float[16];

    int width, height, orig_height;
    float ARC;
    protected boolean STCorN;
    public static boolean modelViewable = true;
    private boolean VOICE_SWITCH = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //matrix initialize
        Matrix.setIdentityM(matrix1, 0);
        Matrix.setIdentityM(matrix2, 0);

        //get Bundle
        Bundle bundle = this.getIntent().getExtras();
        fileNames = bundle.getStringArray("FILE_NAME");
        STCorN = bundle.getBoolean("STCorN");
        myARRenderer = new MyARRenderer(fileNames, STCorN);
        ARC = bundle.getFloat("ARC");
        Log.d("get-STCorN", STCorN + " " + ARC);

        //Full screen and drawing views
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_glass_ar);
        Display display = getWindowManager().getDefaultDisplay();
        width = display.getWidth();
        orig_height = display.getHeight();
        if (STCorN) {
            height = (int) (width / ARC);
        } else {
            height = (int) (width / ARC);
        }

        //Five numbers
        five_num_text[0] = (TextView) findViewById(R.id.DAText);
        five_num_text[1] = (TextView) findViewById(R.id.DDText);
        five_num_text[2] = (TextView) findViewById(R.id.FDAText);
        five_num_text[3] = (TextView) findViewById(R.id.HDAText);
        five_num_text[4] = (TextView) findViewById(R.id.PDDText);

        //ip, connect and Option menu
        iPadd = (EditText) findViewById(R.id.IPText);
        sockConn = (Button) findViewById(R.id.SockBut);
        sockConn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thread_start();
            }
        });

        setting_layout = (FrameLayout) findViewById(R.id.Setting_l);
        setting_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openOptionsMenu();
            }
        });

        //AR layout
        aRLayout = (RelativeLayout) findViewById(R.id.ARLayout);
        aRINLayout = (FrameLayout) findViewById(R.id.ARINLayout);
        aRINLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GL_TRANSLUCENT = !GL_TRANSLUCENT;
                if (GL_TRANSLUCENT) {
                    glView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
                } else {
                    glView.getHolder().setFormat(PixelFormat.RGB_565);
                }
            }
        });
        Log.d("height", height + "");
        RelativeLayout.LayoutParams ll = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
        ll.setMargins(0, (int) -((height - orig_height) / 2.0f), 0, (int) -((height - orig_height) / 2.0f));
        aRLayout.setLayoutParams(ll);

        //message
        logMes = (TextView) findViewById(R.id.LogMesg);
        logMes.setMovementMethod(new ScrollingMovementMethod());

        //SiME Speech recognizer
        mRecognizer = new SiMESpeechRecognizer(this);
        mRecognizer.setLanguageModelFiles("ARV.dic", "ARC.lm");
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

        //TODO: rework Speech comments: new comments, screenshot and new needs
        mListener = new SiMESpeechRecognitionListener() {
            @Override
            public void onResult(final String resultword) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(GlassARActivity.this, resultword, Toast.LENGTH_SHORT).show();
                        if (resultword.equals("INFORMATION")) {
                            if (sockConn.isClickable()) {
                                thread_start();
                                Toast.makeText(GlassARActivity.this, "CONNECT", Toast.LENGTH_SHORT).show();
                            }
                            Toast.makeText(GlassARActivity.this, "VOICE " + VOICE_SWITCH, Toast.LENGTH_SHORT).show();
                        }

                        if (resultword.equals("MODEL")) {
                            modelViewable = !modelViewable;
                            Toast.makeText(GlassARActivity.this, "MODEL " + modelViewable, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        };

        mRecognizer.connectService();
        mRecognizer.registerListener(mListener);
    }

    @Override
    protected void onPause() {
        mRecognizer.unregisterListener(mListener);
        mRecognizer.disconnectService();
        super.onPause();
    }

    @Override
    public void onResume() {
        mRecognizer.connectService();
        mRecognizer.registerListener(mListener);
        super.onResume();
    }

    @Override
    public void onStop() {
        mRecognizer.unregisterListener(mListener);
        mRecognizer.disconnectService();
        super.onStop();
    }

    @Override
    protected void onStart() {
        mRecognizer.connectService();
        mRecognizer.registerListener(mListener);
        super.onStart();
    }

    @Override
    protected ARRenderer supplyRenderer() {
        return myARRenderer;
    }

    @Override
    protected FrameLayout supplyFrameLayout() {
        return aRINLayout;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settings) {
            startActivity(new Intent(this, CameraPreferencesActivity.class));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void thread_start() {
        sockConn.setEnabled(false);
        tcpThread = new TCPThread(iPadd.getText().toString());
        uI_update = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (tcpThread.run_or_n) {
                        Thread.sleep(50);
                        matrix1 = tcpThread.get_matrix(0);
                        matrix2 = tcpThread.get_matrix(1);
                        tcpThread.setFive_num_text(five_num_text);
                        five_num_text[0].setText("DA:" + five_num_text[0].getText());
                        five_num_text[1].setText("DD:" + five_num_text[1].getText());
                        five_num_text[2].setText("FDA:" + five_num_text[2].getText());
                        five_num_text[3].setText("HDA:" + five_num_text[3].getText());
                        five_num_text[4].setText("PDD:" + five_num_text[4].getText());
                        tcpThread.setLogMes(logMes);
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
