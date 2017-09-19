package com.example.qifeng.td.GameRelated.Single;

/**
 * Created by Qifeng on 11/29/2016.
 */

public class CrystalThread extends Thread {

    SingleGameView singleGameView;
    boolean flag;
    boolean whileFlag = true;

    public CrystalThread(SingleGameView singleGameView) {
        this.singleGameView = singleGameView;
        flag = false;
    }

    public void run() {
        while (whileFlag) {

            if (flag) {
                singleGameView.alpha = singleGameView.alpha - 10;
                if (singleGameView.alpha < 0) {
                    singleGameView.alpha = 200;
                    singleGameView.crystalEffectFlag = false;
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

