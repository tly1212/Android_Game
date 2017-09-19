package com.example.qifeng.td.GameRelated.Multi;

/**
 * Created by Qifeng on 11/21/2016.
 */

public class EnemyControlThreadAM extends Thread {

    MultiGameView multiGameView;
    boolean flag = true;
    boolean whileFlag = true;

    public EnemyControlThreadAM(MultiGameView multiGameView) {
        this.multiGameView = multiGameView;
    }

    @Override
    public void run() {
        while (whileFlag) {
            if (flag) {

                if (multiGameView.pairedFlag && !multiGameView.drawPairedFlag) {
                    synchronized (multiGameView.EnemiesA) {
                        //  if (multiGameView.pairedFlag && !multiGameView.drawPairedFlag) {
                        for (EnemyM e : multiGameView.EnemiesA) {
                            e.walkA();
                        }
                        //     }
                        //Removes from this Vector all of its elements that are contained in the specified Collection.
                        multiGameView.EnemiesA.removeAll(EnemyM.enemiesEntryA);
                        EnemyM.enemiesEntryA.clear();
                        multiGameView.EnemiesA.removeAll(BulletM.enemiesDeadA);
                        BulletM.enemiesDeadA.clear();
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
