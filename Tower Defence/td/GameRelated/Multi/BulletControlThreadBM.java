package com.example.qifeng.td.GameRelated.Multi;

import java.util.List;
import java.util.Vector;

/**
 * Created by Qifeng on 11/21/2016.
 */

public class BulletControlThreadBM extends Thread {

    MultiGameView multiGameView;
    private boolean flag = true;
    private boolean whileFlag = true;

    BulletControlThreadBM(MultiGameView multiGameView) {
        this.multiGameView = multiGameView;
    }

    @Override
    public void run() {
        while (whileFlag) {
            if (flag) {
                if (multiGameView.pairedFlag && !multiGameView.drawPairedFlag) {

                    List<EnemyM> EnemiesB = new Vector<EnemyM>(multiGameView.EnemiesB);
                    List<TowerM> TowersB = new Vector<TowerM>(multiGameView.TowersB);

                    //For each player B's tower, searching its target enemy and shooting a bullet to the target enemy.
                    for (int i = 0; i < TowersB.size(); i++) {
                        List<EnemyM> TargetEnemies = new Vector<EnemyM>();

                        //The center coordinates of the tower's location
                        float x1 = (TowersB.get(i).column + 0.5f) * multiGameView.path.getWidth();
                        float y1 = (TowersB.get(i).row + 0.5f) * multiGameView.path.getHeight();
                        //The coordinates that bullets are generated
                        float x2 = x1;
                        float y2 = TowersB.get(i).row * multiGameView.path.getHeight() -
                                (multiGameView.tower[TowersB.get(i).type].getHeight() - multiGameView.grass.getHeight());

                        //For each enemy, checking whether or not it is within the tower's attack range.
                        for (int j = 0; j < EnemiesB.size(); j++) {
                            if ((x1 - EnemiesB.get(j).positionX) * (x1 - EnemiesB.get(j).positionX) +
                                    (y1 - EnemiesB.get(j).positionY) * (y1 - EnemiesB.get(j).positionY)
                                    <= TowersB.get(i).getRange() * TowersB.get(i).getRange()) {
                                TargetEnemies.add(EnemiesB.get(j));
                            } else {
                                continue;
                            }
                        }

                        //If the tower has a target enemy, let it shoot a bullet to the enemy.
                        if (TargetEnemies.size() != 0) {
                            EnemyM targetEnemy = findTheForemostEnemy(TargetEnemies);
                            switch (TowersB.get(i).type) {
                                case 0:
                                    multiGameView.BulletsB.add(new BulletM(multiGameView, multiGameView.bullet, x2, y2, targetEnemy, TowersB.get(i)));
                                    break;
                                case 1:
                                    multiGameView.BulletsB.add(new BulletM(multiGameView, multiGameView.bullet2, x2, y2, targetEnemy, TowersB.get(i)));
                                    break;
                                case 2:
                                    multiGameView.BulletsB.add(new BulletM(multiGameView, multiGameView.bullet3, x2, y2, targetEnemy, TowersB.get(i)));
                                    break;
                            }
                            //multiGameView.BulletsB.add(new BulletM(multiGameView, multiGameView.bullet, x2, y2, targetEnemy, TowersB.get(i)));
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
