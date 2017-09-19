package com.example.qifeng.td.GameRelated.Multi;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by Qifeng on 11/16/2016.
 */

public class CanSetTowerM {
    MultiGameView multiGameView;
    Bitmap bitmap;
    int column;
    int row;
    static Boolean toDraw;

    public CanSetTowerM(MultiGameView multiGameView) {
        this.multiGameView = multiGameView;
        toDraw = false;
    }

    public void setBitmap(Bitmap bitmap){
        this.bitmap = bitmap;
    }

    public void draw(Canvas canvas, Paint paint) {
        float x = (float) (multiGameView.path.getWidth() * (column + 0.5) - (bitmap.getWidth() / 2.0));
        float y = (float) (multiGameView.path.getHeight() * (row + 0.5) - (bitmap.getWidth() / 2.0));
        canvas.drawBitmap(bitmap, x, y, paint);
    }

    public void setPosition(float x, float y) {
        column = (int) (x / multiGameView.path.getWidth());
        row = (int) (y / multiGameView.path.getHeight());
    }
}
