package com.example.qifeng.td.GameRelated.Multi;

/**
 * Created by Qifeng on 11/21/2016.
 */

public class GameOverThreadM extends Thread {

    MultiGameView multiGameView;
    private boolean flag = true;
    private int sleepSpan = 1000;

    public GameOverThreadM(MultiGameView multiGameView) {
        this.multiGameView = multiGameView;
    }

    @Override
    public void run() {
        while (flag) {
            try {
                Thread.sleep(sleepSpan);
            } catch (Exception e) {
                e.printStackTrace();
            }
            multiGameView.GameOver();
        }
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
