package com.example.qifeng.td.GameRelated.Multi;

/**
 * Created by Qifeng on 11/21/2016.
 */

public class BulletThreadAM extends Thread {

    MultiGameView multiGameView;
    boolean flag;
    boolean whileFlag = true;


    public BulletThreadAM(MultiGameView multiGameView) {
        this.multiGameView = multiGameView;
        flag = true;
    }

    public void run() {
        while (whileFlag) {
            if (multiGameView.pairedFlag && !multiGameView.drawPairedFlag) {

                if (flag) {
                    synchronized (multiGameView.BulletsA) {
                        for (BulletM bullet : multiGameView.BulletsA) {
                            bullet.shotA();
                        }
                        multiGameView.BulletsA.removeAll(BulletM.bulletsA);
                    }

                }
                try {
                    Thread.sleep(5);
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
