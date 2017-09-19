package com.example.qifeng.td.GameRelated.Multi;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by Qifeng on 11/17/2016.
 */

public class TowerM {

    MultiGameView multiGameView;
    Bitmap bitmap;
    int column;
    int row;
    int attack[]= {1,2,3};//Attack point of towers
    float range[]= new float[3];//Attack range of towers
    int type;//The type of the tower

    public TowerM(MultiGameView multiGameView, Bitmap bitmap, int xColumn, int yRow, int type) {
        this.multiGameView = multiGameView;
        this.bitmap = bitmap;
        this.column = xColumn;
        this.row = yRow;
        this.type = type;
        range[0] = 160f * (multiGameView.screenWidth/1280f);
        range[1] = 130f * (multiGameView.screenWidth/1280f);
        range[2] = 100f * (multiGameView.screenWidth/1280f);
    }

    public void draw(Canvas canvas, Paint paint) {
        float x = multiGameView.path.getWidth() * column;
        float y = multiGameView.path.getHeight() * row - (bitmap.getHeight() - multiGameView.path.getHeight());
        canvas.drawBitmap(bitmap, x, y, paint);
    }

    public int getAttack() {
        return attack[type];
    }

    public float getRange(){
        return range[type];
    }
}
