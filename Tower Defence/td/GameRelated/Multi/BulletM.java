package com.example.qifeng.td.GameRelated.Multi;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.List;
import java.util.Vector;

/**
 * Created by Qifeng on 11/21/2016.
 */

public class BulletM {

    MultiGameView multiGameView;
    private Bitmap bitmap;
    private float speed = 1.0f; //The distance that bullet goes forward each time
    float x;
    float y;
    TowerM tower;
    static List<BulletM> bulletsA = new Vector<BulletM>(); //List of bullets to delete
    static List<BulletM> bulletsB = new Vector<BulletM>();
    static List<EnemyM> enemiesDeadA = new Vector<EnemyM>(); //List of dead enemies
    static List<EnemyM> enemiesDeadB = new Vector<EnemyM>();
    EnemyM targetEnemy;
    double direction;

    public BulletM(MultiGameView multiGameView, Bitmap bitmap, float x, float y, EnemyM targetEnemy, TowerM tower) {

        this.multiGameView = multiGameView;
        this.bitmap = bitmap;
        this.x = x;
        this.y = y;
        this.targetEnemy = targetEnemy;
        this.tower = tower;
    }

    public void draw(Canvas canvas, Paint paint) {
//        shotA();
//        shotB();
        canvas.drawBitmap(bitmap, x, y, paint);
    }

    //The method to calculate the next position of a bullet
    public void shotA() {

        direction = getDirection(x, y, targetEnemy.positionX, targetEnemy.positionY);
        ;
        float nextX = (float) (speed * Math.sin(direction * Math.PI / 180) + x);
        float nextY = (float) (speed * Math.cos(direction * Math.PI / 180) + y);

        if (enemiesDeadA.contains(targetEnemy)) {
            bulletsA.add(this);
        }

        if (isRectOverlapped(nextX, nextY, bitmap.getWidth(), bitmap.getHeight(), targetEnemy.positionX,
                targetEnemy.positionY, multiGameView.enemyFront[0][0].getWidth(), multiGameView.enemyFront[0][0].getHeight())) {

            //A enemy is hit
            targetEnemy.currentHP -= tower.getAttack();
            if (targetEnemy.currentHP <= 0) {
                if (!enemiesDeadA.contains(targetEnemy)) {
                    multiGameView.goldA += 10;
                    multiGameView.killA += 1;
                    enemiesDeadA.add(targetEnemy);
                }

            }
            bulletsA.add(this);

        } else if (getDistanceSquare(nextX, nextY, (tower.column + 0.5f) * multiGameView.path.getWidth(),
                (tower.row + 0.5f) * multiGameView.path.getHeight()) > 2.343 * tower.getRange() * tower.getRange()) {

            if (getDistanceSquare(nextX, nextY, targetEnemy.positionX, targetEnemy.positionY) <= 0.25 * tower.getRange() * tower.getRange())

                targetEnemy.currentHP -= tower.getAttack();
            if (targetEnemy.currentHP <= 0) {
                if (!enemiesDeadA.contains(targetEnemy)) {
                    multiGameView.goldA += 10;
                    multiGameView.killA += 1;
                    enemiesDeadA.add(targetEnemy);
                }
            }
            bulletsA.add(this);
        } else {
            x = nextX;
            y = nextY;

        }
    }

    public void shotB() {

        direction = getDirection(x, y, targetEnemy.positionX, targetEnemy.positionY);
        ;
        float nextX = (float) (speed * Math.sin(direction * Math.PI / 180) + x);
        float nextY = (float) (speed * Math.cos(direction * Math.PI / 180) + y);

        if (enemiesDeadB.contains(targetEnemy)) {
            bulletsB.add(this);
        }

        if (isRectOverlapped(nextX, nextY, bitmap.getWidth(), bitmap.getHeight(), targetEnemy.positionX,
                targetEnemy.positionY, multiGameView.enemyFront[0][0].getWidth(), multiGameView.enemyFront[0][0].getHeight())) {

            //A enemy is hit
            targetEnemy.currentHP -= tower.getAttack();
            if (targetEnemy.currentHP <= 0) {
                if (!enemiesDeadB.contains(targetEnemy)) {
                    multiGameView.goldB += 10;
                    multiGameView.killB += 1;
                    enemiesDeadB.add(targetEnemy);
                }

            }
            bulletsB.add(this);

        } else if (getDistanceSquare(nextX, nextY, (tower.column + 0.5f) * multiGameView.path.getWidth(),
                (tower.row + 0.5f) * multiGameView.path.getHeight()) > 2.343 * tower.getRange() * tower.getRange()) {

            if (getDistanceSquare(nextX, nextY, targetEnemy.positionX, targetEnemy.positionY) <= 0.25 * tower.getRange() * tower.getRange())

                targetEnemy.currentHP -= tower.getAttack();
            if (targetEnemy.currentHP <= 0) {
                if (!enemiesDeadB.contains(targetEnemy)) {
                    multiGameView.goldB += 10;
                    multiGameView.killB += 1;
                    enemiesDeadB.add(targetEnemy);
                }
            }
            bulletsB.add(this);
        }else {
            x = nextX;
            y = nextY;

        }
    }

    public float getDistanceSquare(float x1, float y1, float x2, float y2) {
        float result = (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);
        return result;

    }

    //The method to calculate the bullet's direction
    public double getDirection(float x1, float y1, float x2, float y2) {

        double direction = 0;
        float dx = x1 - x2;
        float dy = y1 - y2;

        if (dx != 0 || dy != 0) {
            if (dx > 0 && dy > 0) {
                direction = 180 + Math.toDegrees(Math.atan(dx / dy));
            } else if (dx < 0 && dy > 0) {
                direction = 180 - Math.toDegrees(Math.atan(-dx / dy));
            } else if (dx < 0 && dy < 0) {
                direction = Math.toDegrees(Math.atan(dx / dy));
            } else if (dx > 0 && dy < 0) {
                direction = 360 - Math.toDegrees(Math.atan(dx / -dy));
            } else if (dx == 0) {
                if (dy > 0) {
                    direction = 180;
                } else {
                    direction = 0;
                }
            } else if (dy == 0) {
                if (dx > 0) {
                    direction = 270;
                } else {
                    direction = 90;
                }
            }
        }
        return direction;
    }

    //The method used to check whether or not two rectangles is overlapped
    public static boolean isRectOverlapped(
            float xLeftTop1, float yLeftTop1, float width1, float height1,
            float xLeftTop2, float yLeftTop2, float width2, float height2
    ) {
        if (
            //Rectangle1 has points in Rectangle2
                isPointInRect(xLeftTop1, yLeftTop1, xLeftTop2, yLeftTop2, width2, height2) ||
                        isPointInRect(xLeftTop1 + width1, yLeftTop1, xLeftTop2, yLeftTop2, width2, height2) ||
                        isPointInRect(xLeftTop1, yLeftTop1 + height1, xLeftTop2, yLeftTop2, width2, height2) ||
                        isPointInRect(xLeftTop1 + width1, yLeftTop1 + height1, xLeftTop2, yLeftTop2, width2, height2) ||
                        //Rectangle2 has points in Rectangle1
                        isPointInRect(xLeftTop2, yLeftTop2, xLeftTop1, yLeftTop1, width1, height1) ||
                        isPointInRect(xLeftTop2 + width2, yLeftTop2, xLeftTop1, yLeftTop1, width1, height1) ||
                        isPointInRect(xLeftTop2, yLeftTop2 + height2, xLeftTop1, yLeftTop1, width1, height1) ||
                        isPointInRect(xLeftTop2 + width2, yLeftTop2 + height2, xLeftTop1, yLeftTop1, width1, height1)) {
            return true;
        }
        return false;
    }

    //The method used to check whether or not a point is in a rectangle.
    public static boolean isPointInRect(float pointX, float pointY, float rectX, float rectY, float width, float height) {
        if (pointX >= rectX && pointX <= (rectX + width) && pointY >= rectY && pointY <= (rectY + height)) {
            return true;
        }
        return false;
    }
}
