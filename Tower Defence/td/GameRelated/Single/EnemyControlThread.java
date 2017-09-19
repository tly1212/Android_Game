package com.example.qifeng.td.GameRelated.Single;

/**
 * Created by Qifeng on 10/10/2016.
 */

public class EnemyControlThread extends Thread{

    SingleGameView singleGameView;
    boolean flag = true;
    boolean whileFlag = true;

    public EnemyControlThread(SingleGameView singleGameView) {
        this.singleGameView = singleGameView;
    }

    @Override
    public void run() {
        while (whileFlag) {
            if (flag) {
                synchronized (singleGameView.Enemies) {
                    for (Enemy e : singleGameView.Enemies) {
                        e.walk();
                    }
                    //Removes from this Vector all of its elements that are contained in the specified Collection.
                    singleGameView.Enemies.removeAll(Enemy.enemiesEntry);
                    Enemy.enemiesEntry.clear();
                    singleGameView.Enemies.removeAll(Bullet.enemiesDead);
                    Bullet.enemiesDead.clear();
                }
            }
            try {
                Thread.sleep(10);
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
