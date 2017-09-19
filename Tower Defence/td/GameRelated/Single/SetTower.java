package com.example.qifeng.td.GameRelated.Single;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by Qifeng on 11/3/2016.
 */

public class SetTower {
    SingleGameView singleGameView;
    Bitmap bitmap;
    int column;
    int row;
    int type; //The type of the tower.

    public SetTower(SingleGameView singleGameView, Bitmap bitmap, int type) {
        this.singleGameView = singleGameView;
        this.bitmap = bitmap;
        this.type = type;
    }

    public void draw(Canvas canvas, Paint paint) {
        float x = singleGameView.grass.getWidth() * column;
        float y = singleGameView.grass.getHeight() * row - (singleGameView.tower[type].getHeight() - singleGameView.grass.getHeight());
        canvas.drawBitmap(bitmap, x, y, paint);
    }

    public void setPosition(float x, float y) {
        column = (int) (x / singleGameView.grass.getWidth());
        row = (int) (y / singleGameView.grass.getHeight());
    }

}
