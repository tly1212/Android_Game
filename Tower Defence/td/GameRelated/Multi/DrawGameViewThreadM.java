package com.example.qifeng.td.GameRelated.Multi;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

/**
 * Created by Qifeng on 11/21/2016.
 */

public class DrawGameViewThreadM extends Thread {

    boolean flag = true;
    int sleepSpan = 10;
    MultiGameView multiGameView;
    SurfaceHolder surfaceHolder;

    public DrawGameViewThreadM (MultiGameView multiGameView) {
        this.multiGameView = multiGameView;
        this.surfaceHolder = multiGameView.getHolder();
    }

    public void run() {
        Canvas canvas;
        while (flag) {
            canvas = null;
            try {
                canvas = this.surfaceHolder.lockCanvas(null);
                synchronized (this.surfaceHolder) {
                    multiGameView.onDraw(canvas);
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
