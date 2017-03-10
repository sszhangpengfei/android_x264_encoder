package com.test.x264encoderdemo;

import android.annotation.TargetApi;
import android.graphics.ImageFormat;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import example.sszpf.x264.x264sdk;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback,Camera.PreviewCallback {

    private SurfaceView surfaceview;

    private SurfaceHolder surfaceHolder;

    private Camera camera;

    private Parameters parameters;

    private int width = 640;

    private int height = 480;

    private int fps = 20;

    private int bitrate = 90000;

    private x264sdk x264;

    private int timespan = 90000 / fps;

    private long time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        surfaceview = (SurfaceView)findViewById(R.id.surfaceview);
        surfaceHolder = surfaceview.getHolder();
        surfaceHolder.addCallback(this);
        x264 = new x264sdk(l);
        createfile();
    }

    private x264sdk.listener l = new x264sdk.listener(){

        @Override
        public void h264data(byte[] buffer, int length) {
            // TODO Auto-generated method stub
            try {
                outputStream.write(buffer, 0, buffer.length);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    };

    private static String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/zpf_test.h264";
    private BufferedOutputStream outputStream;
    FileOutputStream outStream;
    private void createfile(){
        File file = new File(path);
        if(file.exists()){
            file.delete();
        }
        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(file));
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        // TODO Auto-generated method stub
        time += timespan;
        byte[] yuv420 = new byte[width*height*3/2];
        YUV420SP2YUV420(data,yuv420,width,height);
        x264.PushOriStream(yuv420, yuv420.length, time);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        x264.initX264Encode(width, height, fps, bitrate);
        camera = getBackCamera();
        startcamera(camera);
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // TODO Auto-generated method stub

    }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        if (null != camera) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
        x264.CloseX264Encode();
        try {
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void startcamera(Camera mCamera){
        if(mCamera != null){
            try {
                mCamera.setPreviewCallback(this);
                mCamera.setDisplayOrientation(90);
                if(parameters == null){
                    parameters = mCamera.getParameters();
                }
                parameters = mCamera.getParameters();
                parameters.setPreviewFormat(ImageFormat.NV21);
                parameters.setPreviewSize(width, height);
                mCamera.setParameters(parameters);
                mCamera.setPreviewDisplay(surfaceHolder);
                mCamera.startPreview();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @TargetApi(9)
    private Camera getBackCamera() {
        Camera c = null;
        try {
            c = Camera.open(0); // attempt to get a Camera instance
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c; // returns null if camera is unavailable
    }

    private void YUV420SP2YUV420(byte[] yuv420sp, byte[] yuv420, int width, int height)
    {
        if (yuv420sp == null ||yuv420 == null)return;
        int framesize = width*height;
        int i = 0, j = 0;
        //copy y
        for (i = 0; i < framesize; i++)
        {
            yuv420[i] = yuv420sp[i];
        }
        i = 0;
        for (j = 0; j < framesize/2; j+=2)
        {
            yuv420[i + framesize*5/4] = yuv420sp[j+framesize];
            i++;
        }
        i = 0;
        for(j = 1; j < framesize/2;j+=2)
        {
            yuv420[i+framesize] = yuv420sp[j+framesize];
            i++;
        }
    }
}
