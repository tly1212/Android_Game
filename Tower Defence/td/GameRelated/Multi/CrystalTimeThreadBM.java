package com.example.qifeng.td.GameRelated.Multi;

/**
 * Created by Qifeng on 12/3/2016.
 */

public class CrystalTimeThreadBM extends Thread {

    MultiGameView multiGameView;
    boolean flag = false;
    boolean whileFlag = true;
    int sleepSpan = 100;

    public CrystalTimeThreadBM(MultiGameView multiGameView) {
        this.multiGameView = multiGameView;
    }

    public void run() {
        while (whileFlag) {
            if (flag) {
                multiGameView.crystalAngleB += 1;
                if (multiGameView.crystalAngleB >= 360) {
                    multiGameView.crystalAngleB = 0;
                    this.flag = false;
                    multiGameView.crystalCoolDownFlagB = false;
                    multiGameView.crystalFlagB = true;
                }

                multiGameView.changeCrystalPosition(multiGameView.crystalAngleB);
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
