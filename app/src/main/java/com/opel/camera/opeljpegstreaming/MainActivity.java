package com.opel.camera.opeljpegstreaming;

import android.graphics.Bitmap;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.style.BulletSpan;
import android.widget.ImageView;

import java.io.ByteArrayInputStream;
import java.nio.Buffer;
import java.util.*;
import android.os.*;
import android.graphics.*;

public class MainActivity extends AppCompatActivity {


    private byte[] frame = new byte[4147200]; // Maximum 1Frame JPEG SIZE of 1080P
    Handler handler = new Handler();

    int idx = 0;
    private Integer images[] = {R.drawable.images, R.drawable.images1};
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Network Connection Initialization PSE;
        //Network Connection Start PSE;
        handler.postDelayed(changeImage, 100); /* 10FPS */
    }
    private void setImg(int idx)
    {
        final ImageView imageView = (ImageView) findViewById(R.id.imageView);
        //get Image Stream over Network 'Frame' is Image byte
        Bitmap bm = null;
        ByteArrayInputStream inputStream = new ByteArrayInputStream(frame);
        bm = BitmapFactory.decodeStream(inputStream); //decode Stream
        //imageView.setImageResource(images[idx]); Decode Stream
        imageView.setImageBitmap(bm); //displaying the Bitmap Image
    }
    Runnable changeImage = new Runnable() {
        @Override
        public void run() {
            if(idx == 0) {
                setImg(idx);
                idx=1;
            }
            else {
                setImg(idx);
                idx=0;
            }
            handler.postDelayed(changeImage, 100); /* 10FPS */
        }
    };


}
