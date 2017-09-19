package com.example.qifeng.td.GameRelated.Single;

/**
 * Created by Qifeng on 11/29/2016.
 */

public class CrystalTimeThread extends Thread {
    SingleGameView singleGameView;
    boolean flag = false;
    boolean whileFlag = true;
    int sleepSpan = 100;

    public CrystalTimeThread(SingleGameView singleGameView) {
        this.singleGameView = singleGameView;

    }

    public void run() {
        while (whileFlag) {
            if (flag) {
                singleGameView.crystalAngle += 1;
                if (singleGameView.crystalAngle >= 360) {
                    singleGameView.crystalAngle = 0;
                    this.flag = false;
                    singleGameView.crystalCoolDownFlag = false;
                    singleGameView.crystalFlag = true;
                }

                singleGameView.changeCrystalPosition(singleGameView.crystalAngle);
            }

            try {
                Thread.sleep(sleepSpan);
            } catch (Exception e) {
                e.printStackTrace();
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
