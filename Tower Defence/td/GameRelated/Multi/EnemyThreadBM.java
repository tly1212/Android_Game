package com.example.qifeng.td.GameRelated.Multi;

import com.example.qifeng.td.GameRelated.Maps;

/**
 * Created by Qifeng on 11/21/2016.
 */

public class EnemyThreadBM extends Thread {


    MultiGameView multiGameView;
    private int sleepSpan = 15000;//Latency time ms
    private boolean flag = true;
    private boolean whileFlag = true;
    private int distanceSpan = 1000;

    double directionB = 1 * Math.PI;
    private int maxNumber = 20;

    private int roundB = 1;
    private int maxHP[] = {2, 3, 4};
    private boolean enemyType = false;


    EnemyThreadBM(MultiGameView multiGameView) {
        this.multiGameView = multiGameView;
    }

    @Override
    public void run() {
        while (whileFlag) {
            if (multiGameView.pairedFlag && !multiGameView.drawPairedFlag) {
                try {
                    Thread.sleep(distanceSpan);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (flag) {

                    //For every 3 rounds adding enemies, increase enemies's HP once
                    if (roundB % 3 == 0) {
                        increaseEmenyHP();
                    }
                    roundB++;

                    if (multiGameView.EnemiesB.size() == 0) {
                        for (int i = 0; i < maxNumber; i++) {
                            if (flag) {

                                if (enemyType) {

                                    multiGameView.EnemiesB.add(new EnemyM(multiGameView, null, Maps.MAP2_START_B_X,
                                            Maps.MAP2_START_B_Y, maxHP[1], maxHP[1], 1, directionB, 0));

                                } else {

                                    multiGameView.EnemiesB.add(new EnemyM(multiGameView, null, Maps.MAP2_START_B_X,
                                            Maps.MAP2_START_B_Y, maxHP[0], maxHP[0], 0, directionB, 0));

                                }

                            } else if (!flag) {
                                i--;
                            }
                            try {
                                Thread.sleep(distanceSpan);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        enemyType = !enemyType;
                    }
                }
                try {
                    Thread.sleep(sleepSpan);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //The method used to increase enemies' HP
    void increaseEmenyHP() {
        maxHP[1] += 2;
        maxHP[0] += 2;
        maxHP[2] += 2;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public void setWhileFlag(boolean whileFlag) {
        this.whileFlag = this.whileFlag;
    }
}
