package com.example.qifeng.td.GameRelated.Single;

/**
 * Created by Qifeng on 11/5/2016.
 */

public class BulletThread extends Thread {

    SingleGameView gameView;
    boolean flag;
    boolean whileFlag = true;


    public BulletThread(SingleGameView gameView) {
        this.gameView = gameView;
        flag = true;
    }

    public void run() {
        while (whileFlag) {


            if (flag) {
                synchronized (gameView.Bullets) {
                    for (Bullet bullet : gameView.Bullets) {

                        bullet.shot();
                    }
                    gameView.Bullets.removeAll(Bullet.bullets);
//				try{
//					Thread.sleep(20);
//				}
//				catch(Exception e){
//					e.printStackTrace();
//				}
                }
            }
            try {
                Thread.sleep(5);
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
