package com.example.qifeng.td.GameRelated;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.qifeng.td.MainActivity;
import com.example.qifeng.td.R;

import static android.content.ContentValues.TAG;

/**
 * Created by Qifeng on 10/4/2016.
 */

public class WelcomeView extends SurfaceView implements SurfaceHolder.Callback {
    //SurfaceHolder.Callback
    // A client may implement this interface to receive information about changes to the surface.


    MainActivity activity;
    Paint paint;
    int currentAlpha = 0;

    float screenWidth = MainActivity.screenWidth;
    float screenHeight = MainActivity.screenHeight;
    int sleepSpan = 5;//latency ms

    Bitmap[] logos = new Bitmap[2];//logo image array
    Bitmap currentLogo;//the current logo image
    int xPosition;
    int yPosition;

    public WelcomeView(MainActivity activity) {

        super(activity);
        this.activity = activity;
        //Add a Callback interface for this holder.
        this.getHolder().addCallback(this);
        paint = new Paint();
        paint.setAntiAlias(true);

        //Load images
        logos[0] = BitmapFactory.decodeResource(activity.getResources(), R.drawable.logoa);
        logos[1] = BitmapFactory.decodeResource(activity.getResources(), R.drawable.logob);

        //Compute scale rate
        float wAScaleRate = screenWidth / (float) logos[0].getWidth();
        float wBScaleRate = screenWidth / (float) logos[1].getWidth();
        float hAScaleRate = screenHeight / (float) logos[0].getHeight();
        float hBScaleRate = screenHeight / (float) logos[1].getHeight();

        Matrix matrixA = new Matrix();
        Matrix matrixB = new Matrix();
        matrixA.postScale(wAScaleRate, hAScaleRate);
        matrixB.postScale(wBScaleRate, hBScaleRate);

        //Obtain new image
        logos[0] = Bitmap.createBitmap(logos[0], 0, 0, logos[0].getWidth(), logos[0].getHeight(), matrixA, true);
        logos[1] = Bitmap.createBitmap(logos[1], 0, 0, logos[1].getWidth(), logos[1].getHeight(), matrixB, true);


    }

    public void onDraw(Canvas canvas) {
        //let the whole background be black
        paint.setColor(Color.BLACK);
        //set opaque
        paint.setAlpha(255);
        canvas.drawRect(0, 0, screenWidth, screenHeight, paint);

        if (currentLogo == null)
            return;
        paint.setAlpha(currentAlpha);

        canvas.drawBitmap(currentLogo, xPosition, yPosition, paint);
    }

    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {

    }

    public void surfaceCreated(SurfaceHolder holder) {
        new Thread() {
            public void run() {
                for (Bitmap bm : logos) {
                    currentLogo = bm;
                    //compute the image position
                    xPosition = activity.screenWidth / 2 - bm.getWidth() / 2;
                    yPosition = activity.screenHeight / 2 - bm.getHeight() / 2;

                    for (int i = 255; i >= 0; i -= 10) {
                        if (i < 0) {
                            currentAlpha = 0;
                        } else {
                            currentAlpha = i;
                        }
                        SurfaceHolder sHolder = WelcomeView.this.getHolder();
                        //obtain canvas and start editing the pixels in the surface.
                        Canvas canvas = sHolder.lockCanvas();

                        try {
                            synchronized (sHolder) {
                                onDraw(canvas);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            if (canvas != null) {
                                //Finish editing pixels in the surface.
                                sHolder.unlockCanvasAndPost(canvas);
                            }
                        }
                        try {
                            //if it is a new image, wait a while(1000 ms)
                            if (i == 255) {
                                Thread.sleep(1000);
                            }
                            Thread.sleep(sleepSpan);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                activity.myHandler.sendEmptyMessage(1);
            }
        }.start();
    }

    public void surfaceDestroyed(SurfaceHolder arg0) {
    }


}