package com.example.admin.btl;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;

import javax.net.ssl.HttpsURLConnection;

public class CheckService extends Service {

    private Check check;

    public CheckService() {
    }

    @Override
    public IBinder onBind(Intent intent){
        return null;

    }

    public int onStartCommand(final Intent intent, int flags, int startId) {

        new Thread(new Runnable() {
            public void run() {
                check = new Check(intent);

                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(MainActivity.mBroadcastAction);
                broadcastIntent.putExtra("Data", check.getResult());
                sendBroadcast(broadcastIntent);
            }
        }).start();


        return START_REDELIVER_INTENT;
    }

    public void onCreate(){

    }



}
