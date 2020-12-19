package com.example.servicesassignment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private BoundService mBoundService;
    private boolean isBound;
    private ProgressBar progressBar;
    private ScheduledExecutorService mScheduledExecutorService;
    private int progress;
    private Button minus50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBoundService = new BoundService();
        progressBar = findViewById(R.id.progressBar);
        mScheduledExecutorService = Executors.newScheduledThreadPool(1);
        progress = 0;
        minus50 = findViewById(R.id.minusFifty);

        minus50.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int val = progressBar.getProgress();
                if (val > 50) {
                    val -= 50;
                    progress -= 50;
                    progressBar.setProgress(val);
                } else {
                    progressBar.setProgress(0);
                    progress = 0;
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this , BoundService.class);
        startService(intent);
        bindService(intent , boundServiceConnection, BIND_AUTO_CREATE);
        mScheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (progress < 100) {
                    progress += 5;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        progressBar.setProgress(progress, true);
                    }
                }
            }
        }, 1000, 200, TimeUnit.MILLISECONDS);
    }


    private ServiceConnection boundServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BoundService.MyBinder binderBridge = (BoundService.MyBinder) service ;
            mBoundService = binderBridge.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
            mBoundService= null;
        }
    };
}