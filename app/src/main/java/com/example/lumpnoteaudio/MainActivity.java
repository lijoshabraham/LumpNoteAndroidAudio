package com.example.lumpnoteaudio;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Timer;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {
    private MediaRecorder mRecorder;
    private MediaPlayer mPlayer;
    private Button btnRecord, btnStop, btnPlay, btnStopPlay;
    private static final String LOG_TAG = "AudioRecording";
    public static String mFileName = null;
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnRecord = (Button) findViewById(R.id.btnRecord);
        btnStop = (Button) findViewById(R.id.btnStop);
        btnPlay = (Button) findViewById(R.id.btnPlay);
        btnStopPlay = (Button) findViewById(R.id.btnStopPlay);
        btnStop.setEnabled(false);
        btnPlay.setEnabled(false);
        mRecorder = new MediaRecorder();
        btnStopPlay.setEnabled(false);
        //mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName = "/AudioRecording.3gp";

        ActivityCompat.requestPermissions(
                this,
                PERMISSIONS_STORAGE,
                REQUEST_EXTERNAL_STORAGE
        );

        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckPermissions()) {
                    btnStop.setEnabled(true);
                    btnRecord.setEnabled(false);
                    btnPlay.setEnabled(false);
                    btnStopPlay.setEnabled(false);

                    mRecorder = new MediaRecorder();
                    mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    final File outputFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), mFileName);
                    outputFile.setWritable(true);
                    System.out.println(outputFile.canWrite());
                    mRecorder.setOutputFile(outputFile.getAbsolutePath());
                    //mRecorder.setOutputFile(mFileName);

                    try {
                        mRecorder.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(LOG_TAG, "Prepare() failed");
                    }
                    mRecorder.start();
                    Toast.makeText(getApplicationContext(), "Recording Started", Toast.LENGTH_SHORT).show();
                } else {
                    RequestPermissions();
                }
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnStop.setEnabled(false);
                btnRecord.setEnabled(true);
                btnPlay.setEnabled(true);
                btnStopPlay.setEnabled(true);
                mRecorder.stop();
                mRecorder.release();
                mRecorder = null;
                Toast.makeText(getApplicationContext(), "Recording Stopped", Toast.LENGTH_SHORT).show();
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnStop.setEnabled(false);
                btnRecord.setEnabled(true);
                btnPlay.setEnabled(false);
                btnStopPlay.setEnabled(true);
                mPlayer = new MediaPlayer();

                try {
                    final File outputFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), mFileName);
                    mPlayer.setDataSource(outputFile.getAbsolutePath());
                    mPlayer.prepare();
                    mPlayer.start();
                    Toast.makeText(getApplicationContext(), "Recording started playing", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });
        btnStopPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mPlayer.release();
                mPlayer = null;
                btnStop.setEnabled(false);
                btnPlay.setEnabled(true);
                btnPlay.setEnabled(true);
                btnStopPlay.setEnabled(false);

                Toast.makeText(getApplicationContext(), "Recording Stopped", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_AUDIO_PERMISSION_CODE:
                if (grantResults.length > 0) {
                    boolean permissionToRecord = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean permissionToStore = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (permissionToRecord && permissionToStore) {
                        Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    public boolean CheckPermissions() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;

}
private void RequestPermissions() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{RECORD_AUDIO, WRITE_EXTERNAL_STORAGE},REQUEST_AUDIO_PERMISSION_CODE);
}
}






