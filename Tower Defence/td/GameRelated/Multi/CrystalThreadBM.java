package com.example.qifeng.td.GameRelated.Multi;

/**
 * Created by Qifeng on 12/3/2016.
 */

public class CrystalThreadBM extends Thread {

    MultiGameView multiGameView;
    boolean flag;
    boolean whileFlag = true;

    public CrystalThreadBM(MultiGameView multiGameView) {
        this.multiGameView = multiGameView;
        flag = false;
    }

    public void run() {
        while (whileFlag) {
            if (flag) {
                multiGameView.alphaB = multiGameView.alphaB - 10;
                if (multiGameView.alphaB < 0) {
                    multiGameView.alphaB = 200;
                    multiGameView.crystalEffectFlagB = false;
                    this.setFlag(false);
                }
            }
            try {

                Thread.sleep(50);
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
