package com.example.qifeng.td.GameRelated;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.qifeng.td.MainActivity;
import com.example.qifeng.td.R;


/**
 * Created by Qifeng on 10/4/2016.
 */

public class GameOverView extends SurfaceView implements SurfaceHolder.Callback {
    //SurfaceHolder.Callback
    // A client may implement this interface to receive information about changes to the surface.

    MainActivity activity;
    Paint paint;
    int currentAlpha = 0;
    float screenWidth = MainActivity.screenWidth;
    float screenHeight = MainActivity.screenHeight;
    int sleepSpan = 50;

    Bitmap bitmap;//The game over image
    Bitmap bitmapWin;//The game over image for the player A
    Bitmap bitmapLose;//The game over image for the player B

    int xPosition;
    int yPosition;

    boolean modeFlag;
    boolean winnerFlag;

    public GameOverView(MainActivity activity, boolean modeFlag, boolean winnerFlag) {
        super(activity);
        this.activity = activity;
        //Add a Callback interface for this holder.
        this.getHolder().addCallback(this);
        paint = new Paint();
        paint.setAntiAlias(true);

        //Load the game over image
        bitmap = BitmapFactory.decodeResource(activity.getResources(), R.drawable.gameover);
        bitmapWin = BitmapFactory.decodeResource(activity.getResources(), R.drawable.win);
        bitmapLose = BitmapFactory.decodeResource(activity.getResources(), R.drawable.lose);

        //Compute scale rate
        float wScaleRate = screenWidth / (float) bitmap.getWidth();
        float hScaleRate = screenHeight / (float) bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(wScaleRate, hScaleRate);

        //Generating new game over image
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        bitmapWin = Bitmap.createBitmap(bitmapWin, 0, 0, bitmapWin.getWidth(), bitmapWin.getHeight(), matrix, true);
        bitmapLose = Bitmap.createBitmap(bitmapLose, 0, 0, bitmapLose.getWidth(), bitmapLose.getHeight(), matrix, true);

        this.modeFlag = modeFlag;
        this.winnerFlag = winnerFlag;
    }

    public void onDraw(Canvas canvas) {
        //let the whole background be black
        paint.setColor(Color.BLACK);
        //set opaque
        paint.setAlpha(255);
        canvas.drawRect(0, 0, screenWidth, screenHeight, paint);
        if (bitmap == null)
            return;
        paint.setAlpha(currentAlpha);
        if (modeFlag) {
            if (winnerFlag)
                canvas.drawBitmap(bitmapWin, xPosition, yPosition, paint);
            else
                canvas.drawBitmap(bitmapLose, xPosition, yPosition, paint);
        } else {
            canvas.drawBitmap(bitmap, xPosition, yPosition, paint);
        }

    }

    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {

    }

    public void surfaceCreated(SurfaceHolder holder) {
        new Thread() {
            public void run() {
                //compute the image position
                xPosition = activity.screenWidth / 2 - bitmap.getWidth() / 2;
                yPosition = activity.screenHeight / 2 - bitmap.getHeight() / 2;

                for (int i = 255; i >= 0; i -= 5) {
                    currentAlpha = i;

                    SurfaceHolder sHolder = GameOverView.this.getHolder();
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
                activity.myHandler.sendEmptyMessage(1);
            }
        }.start();
    }

    public void surfaceDestroyed(SurfaceHolder arg0) {

    }
}

