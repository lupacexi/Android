package com.example.admin.btl;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MainActivity extends Activity {

    Button btnCamera;
    Button btnPhoto;
    ImageView imageView;
    TextView tvResult;

    int REQUEST_IMAGE_CAPTURE = 1;
    int REQUEST_IMAGE_GALARY = 2;
    public static final String mBroadcastAction = "STRING_BROADCAST_ACTION";
    private IntentFilter mIntentFilter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        btnCamera = (Button) findViewById(R.id.camera);
        btnPhoto = (Button) findViewById(R.id.photo);
        imageView = (ImageView) findViewById(R.id.image);
        tvResult = (TextView)findViewById(R.id.result);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(mBroadcastAction);

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
            }
        });


        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_IMAGE_GALARY);
            }
        });



    }

//  Nhận dữ liệu ảnh từ camera hoặc bộ sưu tập và gọi Service để kiểm tra
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ( resultCode == RESULT_OK) {
            tvResult.setText("Đang kiểm tra...");

            byte[] img = null;
            if(requestCode == REQUEST_IMAGE_GALARY){
                Uri selectedImage = data.getData();
                try {
                    Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    imageView.setImageBitmap(imageBitmap);
                    img = convertBitmapToByteArray(imageBitmap);
                } catch (IOException e) {
                    Log.i("TAG", "Some exception " + e);
                }
            }else if(requestCode == REQUEST_IMAGE_CAPTURE){
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                imageView.setImageBitmap(imageBitmap);
                img = convertBitmapToByteArray(imageBitmap);
            }


            Bundle temp = new Bundle();
            temp.putByteArray("data", img);
            //Gọi service
            Intent checkIntent = new Intent(MainActivity.this, CheckService.class);
            checkIntent.putExtras(temp);
            this.startService(checkIntent);

        }


    }

//    Convert anh bitmap sang String
    public byte[] convertBitmapToByteArray(Bitmap bitmap){

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    //Kiểm tra và hiển thị kết quả
    protected void check(String result){
        if(result == null )
            return;
        if(result.contains("dog")){
            tvResult.setText("Chó");
        }else if(result.contains("cat")){
            tvResult.setText("Mèo");
        }else{
            tvResult.setText("Nothing!!!");
        }
    }


//    Nhận kết quả từ service
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(mBroadcastAction)) {
               String result = intent.getStringExtra("Data");
               System.out.println(result);
               check(result);
               Intent stopIntent = new Intent(MainActivity.this, CheckService.class);
               stopService(stopIntent);
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    protected void onPause() {
        unregisterReceiver(mReceiver);
        super.onPause();
    }
}