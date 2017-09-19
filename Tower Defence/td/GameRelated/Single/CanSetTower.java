package com.example.qifeng.td.GameRelated.Single;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by Qifeng on 11/5/2016.
 */

public class CanSetTower {
    SingleGameView singleGameView;
    Bitmap bitmap;
    int column;
    int row;
    static Boolean toDraw;

    public CanSetTower(SingleGameView singleGameView) {
        this.singleGameView = singleGameView;
        toDraw = false;
    }

    public void setBitmap(Bitmap bitmap){
        this.bitmap = bitmap;
    }

    public void draw(Canvas canvas, Paint paint) {
        float x = (float) (singleGameView.path.getWidth() * (column + 0.5) - (bitmap.getWidth() / 2.0));
        float y = (float) (singleGameView.path.getHeight() * (row + 0.5) - (bitmap.getWidth() / 2.0));
        canvas.drawBitmap(bitmap, x, y, paint);
    }

    public void setPosition(float x, float y) {
        column = (int) (x / singleGameView.path.getWidth());
        row = (int) (y / singleGameView.path.getHeight());
    }
}
