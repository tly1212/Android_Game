package com.example.qifeng.td.GameRelated.Multi;


/**
 * Created by Qifeng on 12/3/2016.
 */

public class CrystalThreadAM extends Thread {

    MultiGameView multiGameView;
    boolean flag;
    boolean whileFlag = true;

    public CrystalThreadAM(MultiGameView multiGameView) {
        this.multiGameView = multiGameView;
        flag = false;
    }

    public void run() {
        while (whileFlag) {

            if (flag) {
                multiGameView.alphaA = multiGameView.alphaA - 10;
                if (multiGameView.alphaA < 0) {
                    multiGameView.alphaA = 200;
                    multiGameView.crystalEffectFlagA = false;
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
