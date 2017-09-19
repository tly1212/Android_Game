package com.example.qifeng.td.GameRelated.Multi;

/**
 * Created by Qifeng on 12/3/2016.
 */

public class CrystalTimeThreadAM extends Thread {

    MultiGameView multiGameView;
    boolean flag = false;
    boolean whileFlag = true;
    int sleepSpan = 100;

    public CrystalTimeThreadAM(MultiGameView multiGameView) {
        this.multiGameView = multiGameView;
    }

    public void run() {
        while (whileFlag) {
            if (flag) {
                multiGameView.crystalAngleA += 1;
                if (multiGameView.crystalAngleA >= 360) {
                    multiGameView.crystalAngleA = 0;
                    this.flag = false;
                    multiGameView.crystalCoolDownFlagA = false;
                    multiGameView.crystalFlagA = true;
                }

                multiGameView.changeCrystalPosition(multiGameView.crystalAngleA);
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

