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

    TCPStreaming tcpstreaming;
    private byte[] frame; // Maximum 1Frame JPEG SIZE of 1080P
    private Handler handler;

    int idx = 0;
    private Integer images[] = {R.drawable.images, R.drawable.images1};
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        handler = new Handler(){
            public void handleMessage(Message msg){
                switch(msg.what){
                    case TCPStreaming.MSG_TYPE_STAT_CHANGED:
                        break;
                    case TCPStreaming.MSG_TYPE_STAT_READ:
                        setImg((byte[])msg.obj);
                        break;
                }
            }
        };
        setContentView(R.layout.activity_main);
        tcpstreaming = new TCPStreaming("192.168.1.177", 9488, handler);

        //Network Connection Initialization PSE;
        //Network Connection Start PSE;
        handler.postDelayed(changeImage, 100); /* 10FPS */

        tcpstreaming.start();
    }
    private void setImg(byte[] frame)
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
              //  setImg(idx);
                idx=1;
            }
            else {
             //   setImg(idx);
                idx=0;
            }
            handler.postDelayed(changeImage, 100); /* 10FPS */
        }
    };


}
