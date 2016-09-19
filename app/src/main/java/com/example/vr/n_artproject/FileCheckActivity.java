package com.example.vr.n_artproject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.vr.n_artproject.Communication.DownloadFileFromURL;
import com.example.vr.n_artproject.LoaderNCalculater.FileReader;

import java.io.File;
import java.net.URISyntaxException;

public class FileCheckActivity extends AppCompatActivity {

    //launch and download
    private DownloadFileFromURL downloadTask;
    private ProgressDialog progressDialog;
    String download_ip;
    Button launchAR;
    ImageButton download_button;
    ToggleButton aR_or_not;

    //file checker views
    TextView skull_check;
    TextView maxiila_check;
    TextView mandible_check;
    TextView osp1_check;
    TextView osp2_check;
    TextView project_check;
    TextView arMarker_check;

    RadioButton STC_check;
    RadioButton ARC_check;

    //addition button for radiobutton to select file
    Button vSCM_button;
    Button aRC_button;

    FileReader fileReader;
    float[] ARC;

    //path names and checker
    boolean check_all = true;

    String root_path = "/sdcard/";
    String[] filenames = new String[]{
            root_path + "skull.stl", root_path + "maxilla.stl",
            root_path + "mandible.stl", root_path + "max_OSP.stl",
            root_path + "man_OSP.stl", root_path + "arproject.txt",
            root_path + "arpoints.txt", root_path + "HECmatrix.txt",
            root_path + "optical_param_left.dat"};
    boolean[] check = new boolean[]{
            false, false,
            false, false,
            false, false,
            false, false, false};
    TextView[] set_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_file_check);

        launchAR = (Button) findViewById(R.id.launch_button);
        download_button = (ImageButton) findViewById(R.id.download_button);
        aR_or_not = (ToggleButton) findViewById(R.id.aR_toggle);

        skull_check = (TextView) findViewById(R.id.skull_check_text);
        maxiila_check = (TextView) findViewById(R.id.maxi_check_text);
        mandible_check = (TextView) findViewById(R.id.mand_check_text);
        osp1_check = (TextView) findViewById(R.id.osp1_check_text);
        osp2_check = (TextView) findViewById(R.id.osp2_check_text);
        project_check = (TextView) findViewById(R.id.project_check_text);
        arMarker_check = (TextView) findViewById(R.id.ar_check_text);

        STC_check = (RadioButton) findViewById(R.id.stc_radButton);
        ARC_check = (RadioButton) findViewById(R.id.arc_radButton);

        set_view = new TextView[]{
                skull_check, maxiila_check,
                mandible_check, osp1_check,
                osp2_check, project_check,
                arMarker_check, STC_check,
                ARC_check
        };

        for (int i = 0; i < 7; i++) {
            final int finalI = i;
            set_view[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    file_path_chooser(finalI);
                }
            });
        }
        vSCM_button = (Button) findViewById(R.id.VSCM_button);
        vSCM_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                file_path_chooser(7);
            }
        });

        aRC_button = (Button) findViewById(R.id.ARC_button);
        aRC_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                file_path_chooser(8);
            }
        });

        setNewProgressDialog();
        downloadTask = new DownloadFileFromURL(FileCheckActivity.this, progressDialog);

        download_button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final View item = LayoutInflater.from(FileCheckActivity.this).inflate(R.layout.downlaod_ip_dlg, null);
                new AlertDialog.Builder(FileCheckActivity.this)
                        .setTitle("Enter Download address")
                        .setView(item)
                        .setPositiveButton("Confirm IP", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText editText = (EditText) item.findViewById(R.id.download_ip_text);
                                download_ip = editText.getText().toString();
                                Toast.makeText(getApplicationContext(), download_ip, Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();
                return true;
            }
        });
        download_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //downloadTask.execute("http://" + download_ip + ":3000/uploads/plan3");
                downloadTask.execute(download_ip);
                setNewProgressDialog();
                downloadTask = new DownloadFileFromURL(FileCheckActivity.this, progressDialog);
            }
        });

        //TODO: launch different activity or fuse in one
        launchAR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check();
                Intent intent;
                Bundle bundle = new Bundle();

                if (check_all) {
                    if (aR_or_not.isChecked()) {
                        intent = new Intent(FileCheckActivity.this, GlassARActivity.class);
                        if (STC_check.isChecked()) {
                            bundle.putBoolean("STCorN", true);
                            Log.d("l-STCorN", true + "");
                        } else {
                            fileReader = new FileReader();
                            ARC = fileReader.ARTReadBinary(filenames[8]);
                            bundle.putBoolean("STCorN", false);
                            bundle.putFloat("ARC", ARC[1]);
                            Log.d("l-STCorN", false + "");
                        }
                    } else {
                        intent = new Intent(FileCheckActivity.this, ModelActivity.class);
                    }
                    bundle.putStringArray("FILE_NAME", filenames.clone());

                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    Toast.makeText(launchAR.getContext(), "Files not found", Toast.LENGTH_SHORT).show();
                }
            }
        });

        check();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String path = null;
        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            try {
                path = getPath(this, uri);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            filenames[requestCode] = path;
        }

        check();

        super.onActivityResult(requestCode, resultCode, data);
    }

    public String getPath(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                Toast.makeText(launchAR.getContext(), "Files not found", Toast.LENGTH_SHORT).show();
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }


    public void file_path_chooser(int i) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"), i);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }

    }

    public void check() {
        int numbers = 7;
        File file;
        for (int i = 0; i < 9; i++) {
            file = new File(filenames[i]);
            if (file.exists()) {
                set_read_color(i);
            }
        }

        if (aR_or_not.isChecked()) {
            numbers = 9;
        }

        for (int i = 0; i < numbers; i++) {
            check_all = check_all & check[i];
        }
    }

    public void set_read_color(int n) {
        check[n] = true;
        set_view[n].setTextColor(Color.GREEN);
    }

    private void setNewProgressDialog() {
        progressDialog = new ProgressDialog(FileCheckActivity.this);
        progressDialog.setMessage("Dowloading File...");
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
    }
}
