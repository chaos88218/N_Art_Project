package com.example.vr.n_artproject.Communication;

import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.logging.LogRecord;

/**
 * Created by miles on 2016/9/9.
 */
public class TCPThread {

    //alive or not
    public boolean run_or_n = true;

    //Thread
    Handler handler = new Handler();
    private Thread tCP_thread;
    private Runnable runnable;

    //TCP Communication
    private Socket tCP_sock;
    public String ip = "192.168.0.1";
    private final int port = 59979;

    //Numbers
    public String logMesg = "//****//\n";
    public String[] patient = new String[]{"0", "0", "0"};
    private String[][] file_matrix = new String[2][];
    private String[] five_num = new String[]{"0", "0", "0", "0", "0"};


    public TCPThread(String in_ip) {
        ip = in_ip;
        file_matrix[0] = new String[]{
                "0", "0", "0", "0",
                "0", "0", "0", "0",
                "0", "0", "0", "0",
                "0", "0", "0", "0"};
        file_matrix[1] = new String[]{
                "0", "0", "0", "0",
                "0", "0", "0", "0",
                "0", "0", "0", "0",
                "0", "0", "0", "0"};

        runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    logMesg += "Server:Server Connecting.....\n";
                    tCP_sock = new Socket(ip, port);

                    byte[] rebyte = new byte[256];
                    InputStream inputStream = tCP_sock.getInputStream();
                    OutputStream outputStream = tCP_sock.getOutputStream();

                    String str;
                    logMesg += "Server:Server Connected\n";

                    if (inputStream.read(rebyte) != -1) {
                        str = new String(rebyte, StandardCharsets.UTF_8);
                        if (str.substring(0, 2).equals("S1")) {
                            logMesg += "Server:Server Confirm\n";
                            str = str.substring(2);
                            patient = str.split("\\|");
                            logMesg += ("Patient:" + patient[0] + "|" + patient[1] + "|" + patient[2] + "\n");

                            outputStream.write(("C1" + '\0').getBytes(), 0, 3);
                            rebyte = new byte[256];
                            if (inputStream.read(rebyte) != -1) {
                                str = new String(rebyte, StandardCharsets.UTF_8);
                                if (str.substring(0, 2).equals("S3")) {
                                    logMesg += "Server:Communicating......\n";

                                    //*****loop*****//
                                    while (!str.substring(0, 2).equals("S2")) {
                                        Thread.sleep(50);

                                        //*****file1*****//
                                        outputStream.write(("C5" + String.valueOf(patient[1]) + '\0').getBytes()
                                                , 0, 3 + patient[1].length());
                                        rebyte = new byte[256];
                                        if (inputStream.read(rebyte) != -1) {
                                            str = new String(rebyte, StandardCharsets.UTF_8);
                                            if (str.substring(0, 2).equals("S5")) {
                                                str = str.substring(2);
                                                file_matrix[0] = str.split("\\s+");
                                            } else {
                                                logMesg += "Server:Wrong Communication S5-1";
                                                run_or_n = false;
                                                break;
                                            }
                                        } else {
                                            logMesg += "Server:Wrong Communication C5-1";
                                            run_or_n = false;
                                            break;
                                        }

                                        //*****file2*****//
                                        outputStream.write(("C5" + String.valueOf(patient[2]) + '\0').getBytes()
                                                , 0, 3 + patient[2].length());
                                        rebyte = new byte[256];
                                        if (inputStream.read(rebyte) != -1) {
                                            str = new String(rebyte, StandardCharsets.UTF_8);
                                            if (str.substring(0, 2).equals("S5")) {
                                                str = str.substring(2);
                                                file_matrix[1] = str.split("\\s+");
                                            } else {
                                                logMesg += "Server:Wrong Communication S5-2";
                                                run_or_n = false;
                                                break;
                                            }
                                        } else {
                                            logMesg += "Server:Wrong Communication C5-2";
                                            run_or_n = false;
                                            break;
                                        }

                                        //*****five num*****//
                                        outputStream.write(("C6" + '\0').getBytes(), 0, 3);
                                        rebyte = new byte[256];
                                        if (inputStream.read(rebyte) != -1) {
                                            str = new String(rebyte, StandardCharsets.UTF_8);
                                            if (str.substring(0, 2).equals("S6")) {
                                                if (str.substring(2, 4).equals("NG")) {
                                                    five_num = new String[]{String.valueOf(Float.NaN)};
                                                } else {
                                                    str = str.substring(5);
                                                    five_num = str.split("\\s+");
                                                }
                                            } else {
                                                logMesg += "Server:Wrong Communication S6";
                                                run_or_n = false;
                                                break;
                                            }
                                        } else {
                                            logMesg += "Server:Wrong Communication C6";
                                            run_or_n = false;
                                            break;
                                        }

                                    }
                                    //*****loop*****//

                                } else {
                                    logMesg += "Server:Wrong Communication S3";
                                    run_or_n = false;
                                }
                            } else {
                                logMesg += "Server:Wrong Communication C1";
                                run_or_n = false;
                            }

                        } else {
                            logMesg += "Server:Wrong Communication S1";
                            run_or_n = false;
                        }
                    } else {
                        logMesg += "Server:Can't Receive ID/StlName1/StlName2 ------> null";
                        run_or_n = false;
                    }


                } catch (Exception E) {
                    logMesg += ("Runnable:" + E);
                    run_or_n = false;
                }
            }
        };
        tCP_thread = new Thread(runnable);
    }

    public void thread_start() {
        tCP_thread.start();
    }

    public float[] get_matrix(int file_0_or_1) {
        float[] matrix = new float[16];
        for (int i = 0; i < 16; i++) {
            matrix[i] = Float.valueOf(file_matrix[file_0_or_1][i]);
        }
        return matrix;
    }

    private float[] get_five_num() {
        float[] f_num = new float[5];
        for (int i = 0; i < 5; i++) {
            f_num[i] = Float.valueOf(five_num[i]);
        }
        return f_num;
    }

    public void setFive_num_text(final TextView[] five_num_text){
        final float[] f_num = get_five_num();
        handler.post(new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i < 5; i++){
                    five_num_text[i].setText(String.format("%.1f", f_num[i]));
                    Log.d("thread_five", f_num[i] + "");
                }
            }
        });
    }

    public void setLogMes(final TextView log_text){
        handler.post(new Runnable() {
            @Override
            public void run() {
                log_text.setText(logMesg);
            }
        });
        Log.d("thread_five", logMesg);
    }

    public void SockConHandle(final TextView sockConn, final boolean cc) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                sockConn.setEnabled(cc);
            }
        });
    }
}
