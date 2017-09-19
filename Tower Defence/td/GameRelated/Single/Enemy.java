package com.example.qifeng.td.GameRelated.Single;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import com.example.qifeng.td.MainActivity;
import com.example.qifeng.td.GameRelated.Maps;

import java.util.List;
import java.util.Vector;

/**
 * Created by Qifeng on 10/9/2016.
 */

public class Enemy {


    SingleGameView singleGameView;
    float screenWidth = MainActivity.screenWidth;
    float screenHeight = MainActivity.screenHeight;
    float widthRate = screenWidth / 1280f;
    float heightRate = screenHeight / 720f;
    Bitmap bitmap;//The enemy's image
    float stepX = 0.625f;
    float stepY = 0.625f;
    double direction;
    float positionX;
    float positionY;
    int turnIndex = 0;//Count turning points
    int frameCount = 0;
    Bitmap bitmapC;
    static List<Enemy> enemiesEntry = new Vector<Enemy>(); //Enemies finally entry the home
    double stepCount = 0; //The number of steps that the enemy walked
    boolean frameFlag = true; //Whether walk out of the path
    float hpWidth = 32;
    float hpHight = 5;
    int currentHP;
    int maximumHP;
    int type;//The type of enemiesEntry
    boolean mapFlag;

    public Enemy(SingleGameView singleGameView, Bitmap bitmap, float positionX, float positionY, int currentHP,
                 int maximumHP, int type, double direction, int turnIndex) {
        this.singleGameView = singleGameView;
        this.bitmap = bitmap;
        this.positionX = positionX;
        this.positionY = positionY;
        this.currentHP = currentHP;
        this.maximumHP = maximumHP;
        this.type = type;
        this.direction = direction;
        this.turnIndex = turnIndex;
        mapFlag = singleGameView.mapFlag;
    }

    public void drawSelf(Canvas canvas, Paint paint) {

        //Obtain images for current type of enemy
        Bitmap[] bitmaps = getBitmapGroup(direction, type);
        //Obtain the current image
        bitmapC = getCurrentBitmap(bitmaps);
        float barX = (positionX + (40 - hpWidth) / 2) * widthRate;
        float barY = (positionY - 20 - hpHight - 5) * heightRate;
        //Draw the enemy
        canvas.drawBitmap(bitmapC, (positionX) * widthRate, (positionY - 20) * heightRate, paint);

        if (currentHP > 0) {
            //Draw the base life bar
            canvas.drawBitmap(singleGameView.enemyHpRed, barX, barY, paint);
            //Saves the current matrix and clip onto a private stack.
            canvas.save();
            //Modify the current clip with the specified path.
            canvas.clipPath(calculatePath(maximumHP, currentHP));
            //Draw the cover life bar
            canvas.drawBitmap(singleGameView.enemyHpGreen, barX, barY, null);
            //This call balances a previous call to save(),
            //and is used to remove all modifications to the matrix/clip state since the last save call.
            canvas.restore();
        }

    }

    public void walk() {
        float x1 = (float) (positionX + stepX * Math.cos(direction));
        float y1 = (float) (positionY + stepY * Math.sin(direction));
        boolean backFlag = false;

        int nextColumn;
        int nextRow;
        if (Double.compare(direction, 0 * Math.PI) == 0) {
            nextColumn = (int) Math.ceil(x1 / 40f);
        } else {
            nextColumn = (int) Math.floor(x1 / 40f);
        }
        if (Double.compare(direction, 0.5 * Math.PI) == 0)
            nextRow = (int) Math.ceil(y1 / 40f);
        else {
            nextRow = (int) Math.floor(y1 / 40f);
        }



        //Whether the enemy entries home locations
        if (singleGameView.mapFlag) {

            if (nextColumn > 31 || nextRow > 17 || (Maps.MAP3[nextRow][nextColumn] != 1)) {

                backFlag = true;
            }

            if (Maps.MAP3[nextRow][nextColumn] == 5) {

                singleGameView.activity.shake();
                enemiesEntry.add(this);
                if (singleGameView.currentHP == 0) {
                    singleGameView.currentHP = 0;
                } else if (singleGameView.currentHP >= 1) {
                    singleGameView.currentHP -= 1;
                }
            }

        } else {

            if (nextColumn > 31 || nextRow > 17 || (Maps.MAP1[nextRow][nextColumn] != 1)) {

                backFlag = true;
            }

            if (Maps.MAP1[nextRow][nextColumn] == 5) {

                singleGameView.activity.shake();
                enemiesEntry.add(this);
                if (singleGameView.currentHP == 0) {
                    singleGameView.currentHP = 0;
                } else if (singleGameView.currentHP >= 1) {
                    singleGameView.currentHP -= 1;
                }
            }

        }


        if (!backFlag) {
            positionX = x1;
            positionY = y1;
            stepCount++;

        } else if (backFlag) {
            turnIndex++;

            if (singleGameView.mapFlag) {

                if (turnIndex < Maps.MAP3_MAX_TURNS) {
                    direction = getDirection(Maps.MAP3_TURNS[turnIndex + 1][0] - Maps.MAP3_TURNS[turnIndex][0],
                            Maps.MAP3_TURNS[turnIndex + 1][1] - Maps.MAP3_TURNS[turnIndex][1]);
                } else {
                    return;
                }

            } else {

                if (turnIndex < Maps.MAP1_MAX_TURNS) {
                    direction = getDirection(Maps.MAP1_TURNS[turnIndex + 1][0] - Maps.MAP1_TURNS[turnIndex][0],
                            Maps.MAP1_TURNS[turnIndex + 1][1] - Maps.MAP1_TURNS[turnIndex][1]);
                } else {
                    return;
                }

            }

        }
    }

    public void setFrameFlag(boolean frameFlag) {
        this.frameFlag = frameFlag;
    }

    public double getDirection(float x, float y) {

        double direction = 0.0;
        if (x == 0 && y > 0) {
            //Down
            direction = (0.5 * Math.PI);
        } else if (x == 0 && y < 0) {
            //Up
            direction = (1.5 * Math.PI);
        } else if (y == 0 && x > 0) {
            //Right
            direction = (0.0 * Math.PI);
        } else if (y == 0 && x < 0) {
            //Left
            direction = (1 * Math.PI);
        }
        return direction;
    }

    public Bitmap[] getBitmapGroup(double direction, int type) {

        Bitmap[] result;
        if (Double.compare(direction, 0.5 * Math.PI) == 0) {
            result = singleGameView.enemyFront[type];
        } else if (Double.compare(direction, 1.5 * Math.PI) == 0) {
            result = singleGameView.enemyBack[type];
        } else if (Double.compare(direction, 0 * Math.PI) == 0) {
            result = singleGameView.enemyRight[type];
        } else if (Double.compare(direction, 1 * Math.PI) == 0) {
            result = singleGameView.enemyLeft[type];
        } else {
            result = null;
        }
        return result;

    }

    //Frame changing animation for enemy walking. frameFlag=false while the game is paused.
    public Bitmap getCurrentBitmap(Bitmap[] bitmaps) {

        Bitmap result = null;
        if (frameFlag) {
            result = bitmaps[(frameCount++) % bitmaps.length];
        } else if (!frameFlag) {
            result = bitmaps[0];
        }
        return result;
    }

    //The method used to draw the HP bar's cover
    private Path calculatePath(float maximumHP, float currentHP) {

        Path p = new Path();
        //The up-left corner
        float x1 = positionX + (40 - hpWidth) / 2;
        float y1 = positionY - 20 - hpHight - 5;
        //The up-right corner
        float x2 = positionX + (40 - hpWidth) / 2 + currentHP / maximumHP * hpWidth;
        float y2 = positionY - 20 - hpHight - 5;
        //The down-right corner
        float x3 = positionX + (40 - hpWidth) / 2 + currentHP / maximumHP * hpWidth;
        float y3 = positionY - 20 - hpHight - 5 + hpHight;
        //The down-left corner
        float x4 = positionX + (40 - hpWidth) / 2;
        float y4 = positionY - 20 - hpHight - 5 + hpHight;
        //Set the beginning of the next contour to the point (x1,y1).
        p.moveTo(x1 * widthRate, y1 * heightRate);
        //Add a line from the point (x1,y1) to the specified point (x2,y2).
        p.lineTo(x2 * widthRate, y2 * heightRate);
        //Add a line from the point (x2,y2) to the specified point (x3,y3).
        p.lineTo(x3 * widthRate, y3 * heightRate);
        //Add a line from the point (x3,y3) to the specified point (x4,y4).
        p.lineTo(x4 * widthRate, y4 * heightRate);
        //Add a line from the point (x4,y4) back to the point (x1,y1).
        p.lineTo(x1 * widthRate, y1 * heightRate);

        return p;
    }
}

