package com.example.qifeng.td.GameRelated.Multi;

/**
 * Created by Qifeng on 11/21/2016.
 */

public class EnemyControlThreadBM extends Thread {

    MultiGameView multiGameView;
    boolean flag = true;
    boolean whileFlag = true;

    public EnemyControlThreadBM(MultiGameView multiGameView) {
        this.multiGameView = multiGameView;
    }

    @Override
    public void run() {
        while (whileFlag) {
            if (flag) {
                if (multiGameView.pairedFlag && !multiGameView.drawPairedFlag) {

                    synchronized (multiGameView.EnemiesB) {
                        // if (multiGameView.pairedFlag && !multiGameView.drawPairedFlag) {
                        for (EnemyM e : multiGameView.EnemiesB) {
                            e.walkB();
                        }
                        // }

                        //Removes from this Vector all of its elements that are contained in the specified Collection.
                        multiGameView.EnemiesB.removeAll(EnemyM.enemiesEntryB);
                        EnemyM.enemiesEntryB.clear();
                        multiGameView.EnemiesB.removeAll(BulletM.enemiesDeadB);
                        BulletM.enemiesDeadB.clear();
                    }
                }
                try {
                    Thread.sleep(10);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public void setWhileFlag(boolean whileFlag) {
        this.whileFlag = whileFlag;
    }
}
