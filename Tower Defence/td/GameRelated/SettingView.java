package com.example.qifeng.td.GameRelated;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.qifeng.td.MainActivity;
import com.example.qifeng.td.R;

/**
 * Created by Qifeng on 12/4/2016.
 */

public class SettingView extends SurfaceView implements SurfaceHolder.Callback {

    MainActivity activity;
    DrawThread drawThread;

    Paint paint;
    Bitmap settingBack;
    Bitmap mapAOff;
    Bitmap mapAOn;
    Bitmap mapBOff;
    Bitmap mapBOn;

    float screenWidth = MainActivity.screenWidth;
    float screenHeight = MainActivity.screenHeight;

    boolean mapA = true;
    boolean mapB = false;


    public SettingView(MainActivity activity) {
        super(activity);
        this.activity = activity;
        this.getHolder().addCallback(this);
        paint = new Paint();
        paint.setAntiAlias(true);

        //Load images
        settingBack = BitmapFactory.decodeResource(activity.getResources(), R.drawable.settingback);
        mapAOff = BitmapFactory.decodeResource(activity.getResources(), R.drawable.mapaoff);
        mapAOn = BitmapFactory.decodeResource(activity.getResources(), R.drawable.mapaon);
        mapBOff = BitmapFactory.decodeResource(activity.getResources(), R.drawable.mapboff);
        mapBOn = BitmapFactory.decodeResource(activity.getResources(), R.drawable.mapbon);

        //Compute scale rate
        float wScaleRate = screenWidth / (float) settingBack.getWidth();
        float hScaleRate = screenHeight / (float) settingBack.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(wScaleRate, hScaleRate);

        //Obtain new image
        settingBack = Bitmap.createBitmap(settingBack, 0, 0, settingBack.getWidth(), settingBack.getHeight(), matrix, true);
        mapAOff = Bitmap.createBitmap(mapAOff, 0, 0, mapAOff.getWidth(), mapAOff.getHeight(), matrix, true);
        mapAOn = Bitmap.createBitmap(mapAOn, 0, 0, mapAOn.getWidth(), mapAOn.getHeight(), matrix, true);
        mapBOff = Bitmap.createBitmap(mapBOff, 0, 0, mapBOff.getWidth(), mapBOff.getHeight(), matrix, true);
        mapBOn = Bitmap.createBitmap(mapBOn, 0, 0, mapBOn.getWidth(), mapBOn.getHeight(), matrix, true);

    }

    public void onDraw(Canvas canvas) {

        paint.setColor(Color.BLACK);
        paint.setAlpha(255);

        canvas.drawBitmap(settingBack, 0, 0, paint);
        if (mapA) {
            canvas.drawBitmap(mapAOn, 490f * (screenWidth / 1280f), 290f * (screenHeight / 720f), paint);
        } else if (!mapA) {
            canvas.drawBitmap(mapAOff, 490f * (screenWidth / 1280f), 290f * (screenHeight / 720f), paint);
        }
        if (mapB) {
            canvas.drawBitmap(mapBOn, 490f * (screenWidth / 1280f), 460f * (screenHeight / 720f), paint);
        } else if (!mapB) {
            canvas.drawBitmap(mapBOff, 490f * (screenWidth / 1280f), 460f * (screenHeight / 720f), paint);
        }


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:


                if (Float.compare(x, 490f * (screenWidth / 1280f)) >= 0
                        && Float.compare(x, (490f + 300f) * (screenWidth / 1280f)) <= 0
                        && Float.compare(y, 290f * (screenHeight / 720f)) >= 0
                        && Float.compare(y, (290f + 90f) * (screenHeight / 720f)) <= 0) {

                    if (!mapA) {
                        mapA = true;
                        mapB = false;
                    }
                    activity.mapFlag = false;
                }
                if (Float.compare(x, 490f * (screenWidth / 1280f)) >= 0
                        && Float.compare(x, (490f + 300f) * (screenWidth / 1280f)) <= 0
                        && Float.compare(y, 460f * (screenHeight / 720f)) >= 0
                        && Float.compare(y, (460f + 90f) * (screenHeight / 720f)) <= 0) {
                    if (!mapB) {
                        mapA = false;
                        mapB = true;
                    }
                    activity.mapFlag = true;
                }
                if (Float.compare(x, 1110f * (screenWidth / 1280f)) >= 0
                        && Float.compare(x, (1110f + 125f) * (screenWidth / 1280f)) <= 0
                        && Float.compare(y, 630f * (screenHeight / 720f)) >= 0
                        && Float.compare(y, (630f + 50f) * (screenHeight / 720f)) <= 0) {
                    activity.sendMessage(1);
                }

                break;
        }
        return true;
    }

    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {

    }

    public void surfaceCreated(SurfaceHolder holder) {
        drawThread = new DrawThread(this);
        drawThread.start();

    }

    public void surfaceDestroyed(SurfaceHolder arg0) {
        boolean retry = true;
        drawThread.setFlag(false);
        while (retry) {
            try {
                drawThread.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    private class DrawThread extends Thread {

        private boolean flag = true;
        private int sleepSpan = 100;
        SettingView settingView;
        SurfaceHolder surfaceHolder;

        public DrawThread(SettingView settingView) {
            this.settingView = settingView;
            this.surfaceHolder = settingView.getHolder();
        }

        public void run() {
            Canvas c;
            while (this.flag) {
                c = null;
                try {

                    c = this.surfaceHolder.lockCanvas(null);
                    synchronized (this.surfaceHolder) {
                        settingView.onDraw(c);
                    }
                } finally {
                    if (c != null) {

                        this.surfaceHolder.unlockCanvasAndPost(c);
                    }
                }
                try {
                    Thread.sleep(sleepSpan);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public void setFlag(boolean flag) {
            this.flag = flag;
        }
    }
}
