package com.example.qifeng.td.GameRelated.Multi;

import java.util.List;
import java.util.Vector;

/**
 * Created by Qifeng on 11/21/2016.
 */

public class BulletControlThreadAM extends Thread {

    MultiGameView multiGameView;
    private boolean flag = true;
    private boolean whileFlag = true;

    BulletControlThreadAM(MultiGameView multiGameView) {
        this.multiGameView = multiGameView;
    }

    @Override
    public void run() {
        while (whileFlag) {
            if (flag) {
                if (multiGameView.pairedFlag && !multiGameView.drawPairedFlag) {

                    List<EnemyM> EnemiesA = new Vector<EnemyM>(multiGameView.EnemiesA); //Enemies currently on the map that belong to the player A
                    List<TowerM> TowersA = new Vector<TowerM>(multiGameView.TowersA); //Towers currently on the map that belong to the player A


                    //For each player A's tower, searching its target enemy and shooting a bullet to the target enemy.
                    for (int i = 0; i < TowersA.size(); i++) {
                        List<EnemyM> TargetEnemies = new Vector<EnemyM>();

                        //The center coordinates of the tower's location
                        float x1 = (TowersA.get(i).column + 0.5f) * multiGameView.path.getWidth();
                        float y1 = (TowersA.get(i).row + 0.5f) * multiGameView.path.getHeight();
                        //The coordinates that bullets are generated
                        float x2 = x1;
                        float y2 = TowersA.get(i).row * multiGameView.path.getHeight() -
                                (multiGameView.tower[TowersA.get(i).type].getHeight() - multiGameView.grass.getHeight());

                        //For each enemy, checking whether or not it is within the tower's attack range.
                        for (int j = 0; j < EnemiesA.size(); j++) {
                            if ((x1 - EnemiesA.get(j).positionX) * (x1 - EnemiesA.get(j).positionX) +
                                    (y1 - EnemiesA.get(j).positionY) * (y1 - EnemiesA.get(j).positionY)
                                    <= TowersA.get(i).getRange() * TowersA.get(i).getRange()) {
                                TargetEnemies.add(EnemiesA.get(j));
                            } else {
                                continue;
                            }
                        }

                        //If the tower has a target enemy, let it shoot a bullet to the enemy.
                        if (TargetEnemies.size() != 0) {
                            EnemyM targetEnemy = findTheForemostEnemy(TargetEnemies);
                            switch (TowersA.get(i).type) {
                                case 0:
                                    multiGameView.BulletsA.add(new BulletM(multiGameView, multiGameView.bullet, x2, y2, targetEnemy, TowersA.get(i)));
                                    break;
                                case 1:
                                    multiGameView.BulletsA.add(new BulletM(multiGameView, multiGameView.bullet2, x2, y2, targetEnemy, TowersA.get(i)));
                                    break;
                                case 2:
                                    multiGameView.BulletsA.add(new BulletM(multiGameView, multiGameView.bullet3, x2, y2, targetEnemy, TowersA.get(i)));
                                    break;
                            }
                            //multiGameView.BulletsA.add(new BulletM(multiGameView, multiGameView.bullet, x2, y2, targetEnemy, TowersA.get(i)));
                        }
                        TargetEnemies.clear();
                    }

                }

                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //The method used to find the foremost enemy from target enemies.
    public EnemyM findTheForemostEnemy(List<EnemyM> targetEnemies) {
        EnemyM foremost = targetEnemies.get(0);
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
