package com.example.qifeng.td.GameRelated.Single;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

/**
 * Created by Qifeng on 9/28/2016.
 */

public class DrawGameViewThread extends Thread {

    boolean flag = true;
    int sleepSpan = 10;
    SingleGameView singleGameView;
    SurfaceHolder surfaceHolder;

    public DrawGameViewThread(SingleGameView singleGameView) {
        this.singleGameView = singleGameView;
        this.surfaceHolder = singleGameView.getHolder();
    }

    public void run() {
        Canvas canvas;
        while (flag) {
            canvas = null;
            try {
                canvas = this.surfaceHolder.lockCanvas(null);
                synchronized (this.surfaceHolder) {
                    singleGameView.onDraw(canvas);
                }
            } finally {
                if (canvas != null) {
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


