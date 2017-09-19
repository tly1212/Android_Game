package com.example.qifeng.td.GameRelated.Single;

import java.util.List;
import java.util.Vector;

/**
 * Created by Qifeng on 11/2/2016.
 */

public class BulletControlThread extends Thread {

    SingleGameView gameView;
    private boolean flag = true;
    private boolean whileFlag = true;

    BulletControlThread(SingleGameView gameView) {
        this.gameView = gameView;
    }

    @Override
    public void run() {
        while (whileFlag) {
            if (flag) {
                List<Enemy> Enemies = new Vector<Enemy>(gameView.Enemies); //Enemies currently on the map
                List<Tower> Towers = new Vector<Tower>(gameView.Towers); //Towers currently on the map

                //For each tower, searching its target enemy and shooting a bullet to the target enemy.
                for (int i = 0; i < Towers.size(); i++) {
                    List<Enemy> TargetEnemies = new Vector<Enemy>();

                    //The center coordinates of the tower's location
                    float x1 = (Towers.get(i).column + 0.5f) * gameView.grass.getWidth();
                    float y1 = (Towers.get(i).row + 0.5f) * gameView.grass.getHeight();
                    //The coordinates that bullets are generated
                    float x2 = x1;
                    float y2 = Towers.get(i).row * gameView.grass.getHeight() -
                            (gameView.tower[Towers.get(i).type].getHeight() - gameView.grass.getHeight());

                    //For each enemy, checking whether or not it is within the tower's attack range.
                    for (int j = 0; j < Enemies.size(); j++) {
                        if ((x1 - Enemies.get(j).positionX) * (x1 - Enemies.get(j).positionX) +
                                (y1 - Enemies.get(j).positionY) * (y1 - Enemies.get(j).positionY)
                                <= Towers.get(i).getRange() * Towers.get(i).getRange()) {
                            TargetEnemies.add(Enemies.get(j));
                        } else {
                            continue;
                        }
                    }

                    //If the tower has a target enemy, let it shoot a bullet to the enemy.
                    if (TargetEnemies.size() != 0) {
                        Enemy targetEnemy = findTheForemostEnemy(TargetEnemies);
                        switch (Towers.get(i).type) {
                            case 0:
                                gameView.Bullets.add(new Bullet(gameView, gameView.bullet, x2, y2, targetEnemy, Towers.get(i)));
                                break;
                            case 1:
                                gameView.Bullets.add(new Bullet(gameView, gameView.bullet2, x2, y2, targetEnemy, Towers.get(i)));
                                break;
                            case 2:
                                gameView.Bullets.add(new Bullet(gameView, gameView.bullet3, x2, y2, targetEnemy, Towers.get(i)));
                                break;
                        }
//                        gameView.Bullets.add(new Bullet(gameView, gameView.bullet, x2, y2, targetEnemy, Towers.get(i)));
                    }
                    TargetEnemies.clear();
                }
            }

            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //The method used to find the foremost enemy from target enemies.
    public Enemy findTheForemostEnemy(List<Enemy> targetEnemies) {
        Enemy foremost = targetEnemies.get(0);
        for (int i = 1; i < targetEnemies.size(); i++) {
            if (foremost.stepCount < targetEnemies.get(i).stepCount) {
                foremost = targetEnemies.get(i);
            }
        }
        return foremost;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public void setWhileFlag(boolean whileFlag) {
        this.whileFlag = whileFlag;
    }

}