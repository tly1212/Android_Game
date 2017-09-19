package com.example.qifeng.td.GameRelated;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.renderscript.Float2;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.qifeng.td.MainActivity;
import com.example.qifeng.td.R;

/**
 * Created by Qifeng on 10/4/2016.
 */

public class MenuView extends SurfaceView implements SurfaceHolder.Callback {
    //SurfaceHolder.Callback
    // A client may implement this interface to receive information about changes to the surface.

    MainActivity activity;
    Paint paint;
    DrawThread drawThread;
    Bitmap background;
    Bitmap menu;
    //The coordinate of menu's up-left corner
    int xPosition = 10;
    int yPosition = 10;
    final int BUTTON_WIDTH = 300;
    final int BUTTON_HEIGHT = 80;
    float screenWidth = MainActivity.screenWidth;
    float screenHeight = MainActivity.screenHeight;

    public MenuView(MainActivity activity) {
        super(activity);
        this.activity = activity;
        //Call this to try to give focus to a specific view or to one of its descendants.
        this.requestFocus();
        this.setFocusableInTouchMode(true);
        //Add a Callback interface for this holder.
        getHolder().addCallback(this);
        initBitmap();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //canvas.drawColor(Color.WHITE);
        try {
            //Draw the specified bitmap, with its top/left corner at (x,y),
            // using the specified paint, transformed by the current matrix.
            canvas.drawBitmap(background, 0, 0, null);
            canvas.drawBitmap(menu, xPosition, yPosition, null);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        //Return the kind of action being performed.
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            //switch (event.getAction()) {
            //case MotionEvent.ACTION_DOWN:

            //Start single game
            if (Float.compare(x, ((float) (xPosition + 50)) * (screenWidth / 1280f)) >= 0
                    && Float.compare(x, ((float) (xPosition + 50 + BUTTON_WIDTH)) * (screenWidth / 1280f)) <= 0
                    && Float.compare(y, ((float) (yPosition + 60)) * (screenHeight / 720f)) >= 0
                    && Float.compare(y, ((float) (yPosition + 60 + BUTTON_HEIGHT)) * (screenHeight / 720f)) <= 0) {
                activity.sendMessage(0);
            }

            //Start multiple game
            if (Float.compare(x, ((float) (xPosition + 50)) * (screenWidth / 1280f)) >= 0
                    && Float.compare(x, ((float) (xPosition + 50 + BUTTON_WIDTH)) * (screenWidth / 1280f)) <= 0
                    && Float.compare(y, ((float) (yPosition + 120 + BUTTON_HEIGHT)) * (screenHeight / 720f)) >= 0
                    && Float.compare(y, ((float) (yPosition + 120 + 2 * BUTTON_HEIGHT)) * (screenHeight / 720f)) <= 0) {
                activity.sendMessage(3);
            }

            //Entry Setting View
            if (Float.compare(x, ((float) (xPosition + 50)) * (screenWidth / 1280f)) >= 0
                    && Float.compare(x, ((float) (xPosition + 50 + BUTTON_WIDTH)) * (screenWidth / 1280f)) <= 0
                    && Float.compare(y, ((float) (yPosition + 180 + 2 * BUTTON_HEIGHT)) * (screenHeight / 720f)) >= 0
                    && Float.compare(y, ((float) (yPosition + 180 + 3 * BUTTON_HEIGHT)) * (screenHeight / 720f)) <= 0) {
                activity.sendMessage(6);
            }

            //Quit
            if (Float.compare(x, ((xPosition + 50) * (screenWidth / 1280f))) >= 0
                    && Float.compare(x, ((float) (xPosition + 50 + BUTTON_WIDTH)) * (screenWidth / 1280f)) <= 0
                    && Float.compare(y, ((float) (yPosition + 570)) * (screenHeight / 720f)) >= 0
                    && Float.compare(y, ((float) (yPosition + 570 + BUTTON_HEIGHT)) * (screenHeight / 720f)) <= 0) {
                System.exit(0);
            }
            //break;
        }
        return true;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        paint = new Paint();
        paint.setAntiAlias(true);
        createAllThreads();
        startAllThreads();

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        stopAllThreads();
    }

    public void initBitmap() {
        //Load images
        background = BitmapFactory.decodeResource(this.getResources(), R.drawable.mainback);
        menu = BitmapFactory.decodeResource(this.getResources(), R.drawable.menu);

        //Compute scale rate
        float wScaleRate = screenWidth / (float) background.getWidth();
        float hScaleRate = screenHeight / (float) background.getHeight();

        Matrix matrix = new Matrix();
        matrix.postScale(wScaleRate, hScaleRate);

        //Obtain new image
        background = Bitmap.createBitmap(background, 0, 0, background.getWidth(), background.getHeight(), matrix, true);
        menu = Bitmap.createBitmap(menu, 0, 0, menu.getWidth(), menu.getHeight(), matrix, true);


    }

    private void createAllThreads() {
        drawThread = new DrawThread(this);
    }

    private void startAllThreads() {
        drawThread.setFlag(true);
        drawThread.start();
    }

    void stopAllThreads() {
        drawThread.setFlag(false);
        //menuThread.setFlag(false);
    }


    private class DrawThread extends Thread {
        private boolean flag = true;
        private int sleepSpan = 100;
        MenuView mView;
        SurfaceHolder surfaceHolder;

        public DrawThread(MenuView mView) {
            this.mView = mView;
            this.surfaceHolder = mView.getHolder();
        }

        public void run() {
            Canvas canvas;
            while (flag) {
                canvas = null;
                try {
                    //obtain canvas and start editing the pixels in the surface.
                    canvas = this.surfaceHolder.lockCanvas();
                    synchronized (this.surfaceHolder) {
                        mView.onDraw(canvas);
                    }
                } finally {
                    if (canvas != null) {
                        //Finish editing pixels in the surface.
                        this.surfaceHolder.unlockCanvasAndPost(canvas);
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
