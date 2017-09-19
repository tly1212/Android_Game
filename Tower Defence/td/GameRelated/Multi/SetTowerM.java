package com.example.qifeng.td.GameRelated.Multi;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by Qifeng on 11/16/2016.
 */

public class SetTowerM {

    MultiGameView multiGameView;
    Bitmap bitmap;
    int column;
    int row;
    int type; //The type of the tower.

    public SetTowerM(MultiGameView multiGameView, Bitmap bitmap, int type) {
        this.multiGameView = multiGameView;
        this.bitmap = bitmap;
        this.type = type;
    }

    public void draw(Canvas canvas, Paint paint) {
        float x = multiGameView.path.getWidth() * column;
        float y = multiGameView.path.getHeight() * row - (multiGameView.tower[type].getHeight() - multiGameView.grass.getHeight());
        canvas.drawBitmap(bitmap, x, y, paint);
    }

    public void setPosition(float x, float y) {
        column = (int) (x / multiGameView.path.getWidth());
        row = (int) (y / multiGameView.path.getHeight());
    }
}
