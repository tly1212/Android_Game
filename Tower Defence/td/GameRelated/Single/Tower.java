package com.example.qifeng.td.GameRelated.Single;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by Qifeng on 10/29/2016.
 */

public class Tower {
    SingleGameView singleGameView;
    Bitmap bitmap;
    int column;
    int row;
    int attack[]= {1,2,3};//Attack point of towers
    float range[]= new float[3];//Attack range of towers
    int type;//The type of the tower

    public Tower(SingleGameView singleGameView, Bitmap bitmap, int xColumn, int yRow, int type) {
        this.singleGameView = singleGameView;
        this.bitmap = bitmap;
        this.column = xColumn;
        this.row = yRow;
        this.type = type;
        range[0] = 160f * (singleGameView.screenWidth/1280f);
        range[1] = 130f * (singleGameView.screenWidth/1280f);
        range[2] = 100f * (singleGameView.screenWidth/1280f);
    }

    public void draw(Canvas canvas, Paint paint) {
        float x = singleGameView.grass.getWidth() * column;
        float y = singleGameView.grass.getHeight() * row - (bitmap.getHeight() - singleGameView.grass.getHeight());
        canvas.drawBitmap(bitmap, x, y, paint);
    }

    public int getAttack() {
        return attack[type];
    }

    public float getRange(){
        return range[type];
    }

}
