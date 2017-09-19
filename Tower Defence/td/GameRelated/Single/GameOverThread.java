package com.example.qifeng.td.GameRelated.Single;

/**
 * Created by Qifeng on 10/6/2016.
 */

//A thread to determine whether the game is over
public class GameOverThread extends Thread {

    SingleGameView singleGameView;
    private boolean flag = true;
    private int sleepSpan = 1000;

    public GameOverThread(SingleGameView singleGameView) {
        this.singleGameView = singleGameView;
    }

    @Override
    public void run() {
        while (flag) {
            try {
                Thread.sleep(sleepSpan);
            } catch (Exception e) {
                e.printStackTrace();
            }
            singleGameView.GameOver();
        }
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}

