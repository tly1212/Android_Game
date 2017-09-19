package com.example.qifeng.td.GameRelated.Single;

import com.example.qifeng.td.GameRelated.Maps;

/**
 * Created by Qifeng on 10/10/2016.
 */

public class EnemyThread extends Thread {

    SingleGameView singleGameView;
    private int sleepSpan = 15000;//Latency time ms
    private boolean flag = true;
    private boolean whileFlag = true;
    private int distanceSpan = 1000;
    double direction = 0.0 * Math.PI;
    private int maxNumber = 20;
    private int round = 1;
    private int maxHP[] = {2, 3, 4};
    private boolean enemyType = false;


    EnemyThread(SingleGameView singleGameView) {
        this.singleGameView = singleGameView;
    }

    @Override
    public void run() {
        while (whileFlag) {
            try {
                Thread.sleep(distanceSpan);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (flag) {

                //For every 3 rounds adding enemies, increase enemies's HP once
                if (round % 3 == 0) {
                    increaseEmenyHP();
                }
                round++;

                if (singleGameView.Enemies.size() == 0) {
                    for (int i = 0; i < maxNumber; i++) {
                        if (flag) {

                            if(enemyType){

                                if (singleGameView.mapFlag) {

                                    singleGameView.Enemies.add(new Enemy(singleGameView, null, Maps.MAP3_START_X,
                                            Maps.MAP3_START_Y, maxHP[1], maxHP[1], 1, direction, 0));

                                } else {

                                    singleGameView.Enemies.add(new Enemy(singleGameView, null, Maps.MAP1_START_X,
                                            Maps.MAP1_START_Y, maxHP[1], maxHP[1], 1, direction, 0));

                                }

                            }else{

                                if (singleGameView.mapFlag) {

                                    singleGameView.Enemies.add(new Enemy(singleGameView, null, Maps.MAP3_START_X,
                                            Maps.MAP3_START_Y, maxHP[0], maxHP[0], 0, direction, 0));

                                } else {

                                    singleGameView.Enemies.add(new Enemy(singleGameView, null, Maps.MAP1_START_X,
                                            Maps.MAP1_START_Y, maxHP[0], maxHP[0], 0, direction, 0));

                                }

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
