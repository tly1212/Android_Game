package com.example.qifeng.td.GameRelated.Multi;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.media.MediaPlayer;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.qifeng.td.MainActivity;
import com.example.qifeng.td.GameRelated.Maps;
import com.example.qifeng.td.R;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

/**
 * Created by Qifeng on 11/16/2016.
 */

public class MultiGameView extends SurfaceView implements SurfaceHolder.Callback {

    MainActivity activity;
    MultiGameView multiGameView;
    GameOverThreadM ifGameOverThreadM;
    DrawGameViewThreadM drawGameViewThreadM;
    InGameMenuThread inGameMenuThread;
    EnemyThreadAM enemyThreadAM;
    EnemyThreadBM enemyThreadBM;
    EnemyControlThreadAM enemyControlThreadAM;
    EnemyControlThreadBM enemyControlThreadBM;
    BulletControlThreadAM bulletControlThreadAM;
    BulletControlThreadBM bulletControlThreadBM;
    BulletThreadAM bulletThreadAM;
    BulletThreadBM bulletThreadBM;
    CrystalThreadAM crystalThreadAM;
    CrystalThreadBM crystalThreadBM;
    CrystalTimeThreadAM crystalTimeThreadAM;
    CrystalTimeThreadBM crystalTimeThreadBM;
    MediaPlayer mediaPlayer;

    Paint paint;
    Bitmap background;
    Bitmap menu;
    Bitmap home;
    Bitmap path;
    Bitmap grass;
    Bitmap lowTree;
    Bitmap tallTree;
    Bitmap menuButton;
    Bitmap enemyHpRed;
    Bitmap enemyHpGreen;
    Bitmap crystal;
    Bitmap crystalCover;
    Bitmap crystalEffect;
    Bitmap heart;
    Bitmap coin;
    Bitmap bullet;
    Bitmap bullet2;
    Bitmap bullet3;
    Bitmap killed;
    Bitmap connected;
    Bitmap waiting;
    Bitmap paired;
    Bitmap tagYouR;
    Bitmap tagYouB;
    Bitmap tagOpponentR;
    Bitmap tagOpponentB;
    Bitmap tagRed;
    Bitmap tagBlue;
    Bitmap camp;
    Bitmap[] crystalNumbers = new Bitmap[10];
    Bitmap[] killNumbers = new Bitmap[10];
    Bitmap[] coinNumbers = new Bitmap[10];
    Bitmap[] numbers = new Bitmap[10];

    Bitmap[] tower = new Bitmap[3];
    Bitmap[] towerButton = new Bitmap[3];
    Bitmap[] towerRange = new Bitmap[3];
    Bitmap[][] enemyFront = new Bitmap[2][4];//Enemy type, image count
    Bitmap[][] enemyRight = new Bitmap[2][4];//Enemy type, image count
    Bitmap[][] enemyBack = new Bitmap[2][4];//Enemy type, image count
    Bitmap[][] enemyLeft = new Bitmap[2][4];

    float screenWidth = MainActivity.screenWidth;
    float screenHeight = MainActivity.screenHeight;

    //Initial at the hidden position
    static float menuPositionX = 430;
    static float menuPositionY = -600;

    int identity = -1; //A:0 B:1
    int currentHPA = 10;
    int currentHPB = 10;
    int goldA = 500;
    int goldB = 500;
    int killA = 0; //The number of enemies killed by player A
    int killB = 0;

    List<EnemyM> EnemiesA = new Vector<EnemyM>();//Enemies currently stray in the player A's map
    List<TowerM> TowersA = new Vector<TowerM>();//Towers currently on the player A's map
    List<BulletM> BulletsA = new Vector<BulletM>();//Bullets currently on player A's the map

    List<EnemyM> EnemiesB = new Vector<EnemyM>();//Enemies currently stray in the player B's map
    List<TowerM> TowersB = new Vector<TowerM>();//Towers currently on the player B's map
    List<BulletM> BulletsB = new Vector<BulletM>();//Bullets currently on player B's the map

    SetTowerM setTower0;
    SetTowerM setTower1;
    SetTowerM setTower2;
    CanSetTowerM canSetTower;
    boolean tower0Flag;
    boolean tower1Flag;
    boolean tower2Flag;
    boolean menuFlag;

    //Variables used by crystals
    boolean crystalFlagA = true;
    boolean crystalCoolDownFlagA = false;//Crystal time flag
    boolean crystalEffectFlagA = false;
    boolean crystalFlagB = true;
    boolean crystalCoolDownFlagB = false;//Crystal time flag
    boolean crystalEffectFlagB = false;
    int alphaA = 200;//The current alpha values of the crystal image
    int alphaB = 200;
    int crystalAngleA = 0;
    int crystalAngleB = 0;
    float crystalStartX = 1140f * (screenWidth / 1280f);
    float crystalStartY = 10f * (screenHeight / 720f);
    float crystalX = crystalStartX; //Used by the CD effect
    float crystalY = crystalStartY; //Used by the CD effect
    int crystalNumA = 3;
    int crystalNumB = 3;

    //Valuables used for communication
    Thread speaker = null;
    Thread listener = null;
    int colorFlag = -1; //0:R 1:B
    boolean connectedFlag = false;
    boolean waitingFlag = false;
    boolean pairedFlag = false;
    boolean drawPairedFlag = false;
    boolean sendFlag = false;
    boolean receiveFlag = false;
    boolean winFlag = false;
    boolean loseFlag = false;
    String towerInfo;
    String message;


    public MultiGameView(MainActivity activity) {
        super(activity);
        getHolder().addCallback(this);
        this.activity = activity;
        initBitmap();
        paint = new Paint();
        paint.setAntiAlias(true);
        //Set the width for stroking.
        paint.setStrokeWidth(1);

        setTower0 = new SetTowerM(this, tower[0], 0);
        setTower1 = new SetTowerM(this, tower[1], 1);
        setTower2 = new SetTowerM(this, tower[2], 2);
        canSetTower = new CanSetTowerM(this);

        sendFlag = false;
        waitingFlag = false;
        pairedFlag = false;
        drawPairedFlag = false;
        winFlag = false;
        loseFlag = false;

        multiGameView = this;
    }

    @Override
    public void onDraw(final Canvas canvas) {
        super.onDraw(canvas);

        if (waitingFlag || (!waitingFlag && !pairedFlag)) {
            //Draw waiting view
            canvas.drawBitmap(waiting, 0, 0, null);
        } else if (pairedFlag && drawPairedFlag) {
            //Draw paired view
            canvas.drawBitmap(paired, 0, 0, null);
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (Exception e) {
                e.printStackTrace();
            }
            drawPairedFlag = false;
        } else if (pairedFlag && !drawPairedFlag) {

            //Draw background
            canvas.drawBitmap(background, 0, 0, null);

            //Draw basic map
            for (int i = 0; i < 18; i++) {
                for (int j = 0; j < 32; j++) {
                    if (Maps.MAP2[i][j] == 1 || Maps.MAP2[i][j] == 4 || Maps.MAP2[i][j] == 50 || Maps.MAP2[i][j] == 51) {
                        //Draw the specified bitmap, with its top/left corner at (x,y),
                        // using the specified paint, transformed by the current matrix.
                        canvas.drawBitmap(path, path.getWidth() * j, path.getHeight() * i, null);
                    } else {
                        canvas.drawBitmap(grass, grass.getWidth() * j, grass.getHeight() * i, null);
                    }
                }
            }

            for (int i = 0; i < 18; i++) {
                for (int j = 0; j < 32; j++) {

                    //Draw additional map elements
                    if (Maps.MAP2[i][j] == 2) {
                        canvas.drawBitmap(lowTree, lowTree.getWidth() * j, lowTree.getHeight() * i, null);
                    } else if (Maps.MAP2[i][j] == 3) {
                        canvas.drawBitmap(tallTree, tallTree.getWidth() * j,
                                path.getHeight() * i - (tallTree.getHeight() - path.getHeight()), null);
                    } else if (Maps.MAP2[i][j] == 4) {
                        canvas.drawBitmap(camp, path.getWidth() * j,
                                path.getHeight() * i - camp.getHeight() + path.getHeight(), null);
                    } else if (Maps.MAP2[i][j] == 50) {
                        canvas.drawBitmap(home, path.getWidth() * j,
                                path.getHeight() * i - (home.getHeight() - path.getHeight()), null);
                        if (identity == 0) {
                            colorFlag = 0;
                            canvas.drawBitmap(tagYouR, path.getWidth() * j,
                                    path.getHeight() * i - (home.getHeight() - path.getHeight()) - tagYouR.getHeight(), null);
                        } else if (identity == 1) {
                            canvas.drawBitmap(tagOpponentR, path.getWidth() * j,
                                    path.getHeight() * i - (home.getHeight() - path.getHeight()) - tagOpponentR.getHeight(), null);
                        }

                    } else if (Maps.MAP2[i][j] == 51) {
                        canvas.drawBitmap(home, path.getWidth() * (j - 1),
                                path.getHeight() * i - (home.getHeight() - path.getHeight()), null);
                        if (identity == 0) {
                            canvas.drawBitmap(tagOpponentB, path.getWidth() * (j - 1),
                                    path.getHeight() * i - (home.getHeight() - path.getHeight()) - tagOpponentB.getHeight(), null);
                        } else if (identity == 1) {
                            colorFlag = 1;
                            canvas.drawBitmap(tagYouB, path.getWidth() * (j - 1),
                                    path.getHeight() * i - (home.getHeight() - path.getHeight()) - tagYouB.getHeight(), null);
                        }
                    }

                    //Draw A's enemiesEntry
                    synchronized (EnemiesA) {
                        for (EnemyM enemy : EnemiesA) {
                            if ((int) (enemy.positionX / 40) == j && (int) (enemy.positionY / 40) == i) {
                                enemy.drawSelf(canvas, paint);
                            }
                        }
                    }

                    //Draw B's enemiesEntry
                    synchronized (EnemiesB) {
                        for (EnemyM enemy : EnemiesB) {
                            if ((int) (enemy.positionX / 40) == j && (int) (enemy.positionY / 40) == i) {
                                enemy.drawSelf(canvas, paint);
                            }
                        }
                    }

                    //Draw A's towers
                    synchronized (TowersA) {
                        for (TowerM towerA : TowersA) {
                            if (towerA.column == j && towerA.row == i) {
                                towerA.draw(canvas, paint);

                                float x = path.getWidth() * j;
                                float y = path.getHeight() * i - (tower[0].getHeight() - path.getHeight());
                                canvas.drawBitmap(tagRed, x, y, paint);

                            }
                        }
                    }

                    //Draw B's towers
                    synchronized (TowersB) {
                        for (TowerM towerB : TowersB) {
                            if (towerB.column == j && towerB.row == i) {
                                towerB.draw(canvas, paint);

                                float x = path.getWidth() * j;
                                float y = path.getHeight() * i - (tower[0].getHeight() - path.getHeight());
                                canvas.drawBitmap(tagBlue, x, y, paint);

                            }
                        }
                    }

                }
            }

            //Draw A's bullets
            synchronized (BulletsA) {
                for (BulletM bullet : BulletsA) {
                    bullet.draw(canvas, paint);
                }
            }

            //Draw B's bullets
            synchronized (BulletsA) {
                for (BulletM bullet : BulletsA) {
                    bullet.draw(canvas, paint);
                }
            }

            if (identity == 0) {

                //Draw coins
                canvas.drawBitmap(coin, (720f) * (screenWidth / 1280f), (10f) * (screenHeight / 720f), null);
                String stringGold = goldA + "";
                for (int i = 0; i < stringGold.length(); i++) {
                    int temp = stringGold.charAt(i) - '0';
                    canvas.drawBitmap(coinNumbers[temp], (720 + 40f + (i * 20f)) * (screenWidth / 1280f), (10f) * (screenHeight / 720f), null);
                }

                //Draw current HP
                canvas.drawBitmap(heart, (860f) * (screenWidth / 1280f), (10f) * (screenHeight / 720f), null);
                String stringHP = currentHPA + "";
                for (int i = 0; i < stringHP.length(); i++) {
                    int temp = stringHP.charAt(i) - '0';
                    canvas.drawBitmap(numbers[temp], (860f + 40f + (i * 22f)) * (screenWidth / 1280f), (10f) * (screenHeight / 720f), null);
                }

                //Draw killed enemy
                canvas.drawBitmap(killed, (1000f) * (screenWidth / 1280f), (10f) * (screenHeight / 720f), null);
                String stringKill = killA + "";
                for (int i = 0; i < stringKill.length(); i++) {
                    int temp = stringKill.charAt(i) - '0';
                    canvas.drawBitmap(numbers[temp], (1000f + 40f + (i * 22f)) * (screenWidth / 1280f), (10f) * (screenHeight / 720f), null);
                }

                //draw crystal
                canvas.drawBitmap(crystal, crystalStartX, crystalStartY, null);
                String crystalString = crystalNumA + "";
                for (int i = 0; i < crystalString.length(); i++) {
                    int temp = crystalString.charAt(i) - '0';
                    canvas.drawBitmap(crystalNumbers[temp], crystalStartX + (40f + (i * 22f)) * (screenWidth / 1280f), crystalStartY, null);
                }

                //draw crystal CD effect
                if (crystalCoolDownFlagA) {
                    canvas.save();
                    canvas.clipPath(makeCrystalPathDash(crystalX, crystalY));
                    canvas.drawBitmap(crystalCover, crystalStartX,
                            crystalStartY, null);
                    canvas.restore();
                }


            } else if (identity == 1) {

                //Draw coins
                canvas.drawBitmap(coin, (720f) * (screenWidth / 1280f), (10f) * (screenHeight / 720f), null);
                String stringGold = goldB + "";
                for (int i = 0; i < stringGold.length(); i++) {
                    int temp = stringGold.charAt(i) - '0';
                    canvas.drawBitmap(coinNumbers[temp], (720 + 40f + (i * 20f)) * (screenWidth / 1280f), (10f) * (screenHeight / 720f), null);
                }

                //Draw current HP
                canvas.drawBitmap(heart, (860f) * (screenWidth / 1280f), (10f) * (screenHeight / 720f), null);
                String stringHP = currentHPB + "";
                for (int i = 0; i < stringHP.length(); i++) {
                    int temp = stringHP.charAt(i) - '0';
                    canvas.drawBitmap(numbers[temp], (860f + 40f + (i * 22f)) * (screenWidth / 1280f), (10f) * (screenHeight / 720f), null);
                }

                //Draw killed enemy
                canvas.drawBitmap(killed, (1000f) * (screenWidth / 1280f), (10f) * (screenHeight / 720f), null);
                String stringKill = killB + "";
                for (int i = 0; i < stringKill.length(); i++) {
                    int temp = stringKill.charAt(i) - '0';
                    canvas.drawBitmap(killNumbers[temp], (1000f + 40f + (i * 22f)) * (screenWidth / 1280f), (10f) * (screenHeight / 720f), null);
                }

                //draw crystal
                canvas.drawBitmap(crystal, crystalStartX, crystalStartY, null);
                String crystalString = crystalNumB + "";
                for (int i = 0; i < crystalString.length(); i++) {
                    int temp = crystalString.charAt(i) - '0';
                    canvas.drawBitmap(crystalNumbers[temp], crystalStartX + (40f + (i * 22f)) * (screenWidth / 1280f), crystalStartY, null);
                }

                //Draw crystal CD effect
                if (crystalCoolDownFlagB) {
                    canvas.save();
                    canvas.clipPath(makeCrystalPathDash(crystalX, crystalY));
                    canvas.drawBitmap(crystalCover, crystalStartX,
                            crystalStartY, null);
                    canvas.restore();
                }

            }


            //Draw tower buttons
            canvas.drawBitmap(towerButton[0], (1280f - 30f - 80f) * (screenWidth / 1280f), (720f - 80f - 30f) * (screenHeight / 720f), null);
            canvas.drawBitmap(towerButton[1], (1280f - 30f - 80f - 30f - 80f) * (screenWidth / 1280f), (720f - 80f - 30f) * (screenHeight / 720f), null);
            canvas.drawBitmap(towerButton[2], (1280f - 30f - 80f - 30f - 80f - 30f - 80f) * (screenWidth / 1280f), (720f - 80f - 30f) * (screenHeight / 720f), null);

            //Draw grids when dragging towers
            if (tower0Flag || tower1Flag || tower2Flag) {

                //Draw vertical grid lines
                for (int i = 0; i < Maps.MAP2[0].length; i++) {

                    float xStart = path.getWidth() * i;
                    float yStart = 0;
                    float xEnd = path.getWidth() * i;
                    float yEnd = path.getHeight() * Maps.MAP2.length;

                    //Helper to setColor(), that takes a,r,g,b and constructs the color int. (a = alpha)
                    paint.setARGB(255, 255, 255, 255);
                    canvas.drawLine(xStart, yStart, xEnd, yEnd, paint);
                }

                //Draw horizontal grid lines
                for (int i = 0; i < Maps.MAP2.length; i++) {

                    float xStart = 0;
                    float yStart = path.getHeight() * i;
                    float xEnd = path.getWidth() * Maps.MAP2[0].length;
                    float yEnd = path.getHeight() * i;

                    //Helper to setColor(), that takes a,r,g,b and constructs the color int. (a = alpha)
                    paint.setARGB(255, 255, 255, 255);
                    canvas.drawLine(xStart, yStart, xEnd, yEnd, paint);
                }

                //Draw the tower's attack range, if it is a possible location to set the tower
                if (canSetTower.toDraw == true) {
                    canSetTower.draw(canvas, paint);
                }

                //Draw the tower during the dragging
                if (tower0Flag) {
                    setTower0.draw(canvas, paint);
                } else if (tower1Flag) {
                    setTower1.draw(canvas, paint);
                } else if (tower2Flag) {
                    setTower2.draw(canvas, paint);
                }

            }

            //Draw menu button
            canvas.drawBitmap(menuButton, 30f * (screenWidth / 1280f), (720f - 80f - 30f) * (screenHeight / 720f), null);

            //Draw menu
            if (menuFlag) {
                inGameMenuThread.setFlag(true);
                canvas.drawBitmap(menu, menuPositionX * (screenWidth / 1280f), menuPositionY * (screenHeight / 720f), null);
            }

            //Draw connected icon
            if (connectedFlag) {
                canvas.drawBitmap(connected, (30f) * (screenWidth / 1280f), (10f) * (screenHeight / 720f), null);
            }

            //Draw crystal effect
            if (crystalEffectFlagA) {
                paint.setAlpha(alphaA);
                canvas.drawBitmap(crystalEffect, 0, 0, paint);
                paint.setAlpha(255);
            }

            //Draw crystal effect
            if (crystalEffectFlagB) {
                paint.setAlpha(alphaB);
                canvas.drawBitmap(crystalEffect, 0, 0, paint);
                paint.setAlpha(255);
            }

        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();

        //Corresponding column and row of the on touch coordinate
        int xColumn = (int) (x / path.getWidth());
        int yRow = (int) (y / path.getHeight());

        //A pressed gesture has started, the motion contains the initial starting location.
        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            //Press the give up button in the waiting view
            if (waitingFlag || (!waitingFlag && !pairedFlag) && Float.compare(x, 540f * (screenWidth / 1280f)) >= 0
                    && Float.compare(x, 740f * (screenWidth / 1280f)) <= 0
                    && Float.compare(y, 480f * (screenHeight / 720f)) >= 0
                    && Float.compare(y, 530f * (screenHeight / 720f)) <= 0) {

                sendFlag = false;
                waitingFlag = false;
                pairedFlag = false;
                drawPairedFlag = false;
                winFlag = false;
                loseFlag = false;
                stopAllThreads();
                activity.sendMessage(1);

                return true;
            }

            //Press the crystal button
            if (identity == 0) {
                if (crystalNumA > 0 && crystalFlagA && Float.compare(x, crystalStartX) >= 0
                        && Float.compare(x, crystalStartX + crystal.getWidth()) <= 0
                        && Float.compare(y, crystalStartY) >= 0
                        && Float.compare(y, crystalStartY + crystal.getHeight()) <= 0) {
                    EnemiesA.clear();
                    crystalTimeThreadAM.setFlag(true);
                    this.crystalCoolDownFlagA = true;
                    this.crystalEffectFlagA = true;
                    crystalThreadAM.setFlag(true);
                    crystalNumA -= 1;
                    crystalFlagA = false;

                    sendFlag = true;
                    towerInfo = new String("0 crystal");
                }
            } else if (identity == 1) {
                if (crystalNumB > 0 && crystalFlagB && Float.compare(x, crystalStartX) >= 0
                        && Float.compare(x, crystalStartX + crystal.getWidth()) <= 0
                        && Float.compare(y, crystalStartY) >= 0
                        && Float.compare(y, crystalStartY + crystal.getHeight()) <= 0) {
                    EnemiesB.clear();
                    crystalTimeThreadBM.setFlag(true);
                    this.crystalCoolDownFlagB = true;
                    this.crystalEffectFlagB = true;
                    crystalThreadBM.setFlag(true);
                    crystalNumB -= 1;
                    crystalFlagB = false;

                    sendFlag = true;
                    towerInfo = new String("1 crystal");
                }
            }


            //Press the menu button
            if (Float.compare(x, 30f * (screenWidth / 1280f)) >= 0
                    && Float.compare(x, 110f * (screenWidth / 1280f)) <= 0
                    && Float.compare(y, (720f - 80f - 30f) * (screenHeight / 720f)) >= 0
                    && Float.compare(y, (720f - 30f) * (screenHeight / 720f)) <= 0) {
                menuFlag = true;
                sendFlag = true;
                message = "menu";
                suspendAllThreads();
                stopFrameChange();
                return true;
            }

            //Press the continue button in the menu
            if (menuFlag == true && Float.compare(x, (menuPositionX + 60) * (screenWidth / 1280f)) >= 0
                    && Float.compare(x, (menuPositionX + 360) * (screenWidth / 1280f)) <= 0
                    && Float.compare(y, (menuPositionY + 90) * (screenHeight / 720f)) >= 0
                    && Float.compare(y, (menuPositionY + 180) * (screenHeight / 720f)) <= 0) {
                menuFlag = false;
                menuPositionY = -600;
                sendFlag = true;
                message = "continue";
                continueAllThreads();
                startFrameChange();
                return true;
            }

            //Press the end game button in the menu
            if (menuFlag == true && Float.compare(x, (menuPositionX + 60) * (screenWidth / 1280f)) >= 0
                    && Float.compare(x, (menuPositionX + 360) * (screenWidth / 1280f)) <= 0
                    && Float.compare(y, (menuPositionY + 460) * (screenHeight / 720f)) >= 0
                    && Float.compare(y, (menuPositionY + 550) * (screenHeight / 720f)) <= 0
                    ) {
                try {
                    menuFlag = false;
                    menuPositionY = -600;
                    sendFlag = true;
                    message = "end";
                    stopAllThreads();
                    activity.sendMessage(1);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }

            if (identity == 0) {
                //Press tower buttons of type 0
                if (goldA >= 100 && Float.compare(x, (1280f - 30f - 80f) * (screenWidth / 1280f)) >= 0
                        && Float.compare(x, (1280f - 30f) * (screenWidth / 1280f)) <= 0
                        && Float.compare(y, (720f - 80f - 30f) * (screenHeight / 720f)) >= 0
                        && Float.compare(y, (720f - 30f) * (screenHeight / 720f)) <= 0 && menuFlag == false) {
                    tower0Flag = true;
                    setTower0.setPosition(x, y);
                    return true;
                }

                //Press tower buttons of type 1
                if (goldA >= 150 && Float.compare(x, (1280f - 30f - 80f - 30f - 80f) * (screenWidth / 1280f)) >= 0
                        && Float.compare(x, (1280f - 30f - 80f - 30f) * (screenWidth / 1280f)) <= 0
                        && Float.compare(y, (720f - 80f - 30f) * (screenHeight / 720f)) >= 0
                        && Float.compare(y, (720f - 30f) * (screenHeight / 720f)) <= 0 && menuFlag == false) {
                    tower1Flag = true;
                    setTower1.setPosition(x, y);
                    return true;
                }

                //Press tower buttons of type 2
                if (goldA >= 200 &&
                        Float.compare(x, (1280f - 30f - 80f - 30f - 80f - 30f - 80f) * (screenWidth / 1280f)) >= 0
                        && Float.compare(x, (1280f - 30f - 80f - 30f - 80f - 30f) * (screenWidth / 1280f)) <= 0
                        && Float.compare(y, (720f - 80f - 30f) * (screenHeight / 720f)) >= 0
                        && Float.compare(y, (720f - 30f) * (screenHeight / 720f)) <= 0 && menuFlag == false) {
                    tower2Flag = true;
                    setTower2.setPosition(x, y);
                    return true;
                }
            } else {

                //Press tower buttons of type 0
                if (goldB >= 100 && Float.compare(x, (1280f - 30f - 80f) * (screenWidth / 1280f)) >= 0
                        && Float.compare(x, (1280f - 30f) * (screenWidth / 1280f)) <= 0
                        && Float.compare(y, (720f - 80f - 30f) * (screenHeight / 720f)) >= 0
                        && Float.compare(y, (720f - 30f) * (screenHeight / 720f)) <= 0 && menuFlag == false) {
                    tower0Flag = true;
                    setTower0.setPosition(x, y);
                    return true;
                }

                //Press tower buttons of type 1
                if (goldB >= 150 && Float.compare(x, (1280f - 30f - 80f - 30f - 80f) * (screenWidth / 1280f)) >= 0
                        && Float.compare(x, (1280f - 30f - 80f - 30f) * (screenWidth / 1280f)) <= 0
                        && Float.compare(y, (720f - 80f - 30f) * (screenHeight / 720f)) >= 0
                        && Float.compare(y, (720f - 30f) * (screenHeight / 720f)) <= 0 && menuFlag == false) {
                    tower1Flag = true;
                    setTower1.setPosition(x, y);
                    return true;
                }

                //Press tower buttons of type 2
                if (goldB >= 200 &&
                        Float.compare(x, (1280f - 30f - 80f - 30f - 80f - 30f - 80f) * (screenWidth / 1280f)) >= 0
                        && Float.compare(x, (1280f - 30f - 80f - 30f - 80f - 30f) * (screenWidth / 1280f)) <= 0
                        && Float.compare(y, (720f - 80f - 30f) * (screenHeight / 720f)) >= 0
                        && Float.compare(y, (720f - 30f) * (screenHeight / 720f)) <= 0 && menuFlag == false) {
                    tower2Flag = true;
                    setTower2.setPosition(x, y);
                    return true;
                }
            }

        }

        //A change has happened during a press gesture (between ACTION_DOWN and ACTION_UP).
        else if (event.getAction() == MotionEvent.ACTION_MOVE) {

            if (tower0Flag == true || tower1Flag == true || tower2Flag == true) {

                if (tower0Flag == true) {
                    setTower0.setPosition(x, y);
                } else if (tower1Flag == true) {
                    setTower1.setPosition(x, y);
                } else {
                    setTower2.setPosition(x, y);
                }

                if (Maps.MAP2[(int) (y / path.getHeight())][(int) (x / path.getWidth())] == 0) {

                    canSetTower.toDraw = true;
                    canSetTower.setPosition(x, y);
                    if (tower0Flag == true) {
                        canSetTower.setBitmap(towerRange[0]);
                    } else if (tower1Flag == true) {
                        canSetTower.setBitmap(towerRange[1]);
                    } else {
                        canSetTower.setBitmap(towerRange[2]);
                    }

                } else {

                    canSetTower.toDraw = false;

                }
            }
            return true;
        }

        // A pressed gesture has finished,the motion contains the final release location as
        // well as any intermediate points since the last down or move event.
        else if (event.getAction() == MotionEvent.ACTION_UP) {

            if (identity == 0) {

                if (Maps.MAP2[yRow][xColumn] == 0 && (tower0Flag == true || tower1Flag == true || tower2Flag == true)) {
                    if (tower0Flag == true) {
                        TowersA.add(new TowerM(this, tower[0], xColumn, yRow, 0));
                        goldA -= 100;
                        tower0Flag = false;
                        sendTowerInfo(identity, xColumn, yRow, 0);
                    } else if (tower1Flag == true) {
                        TowersA.add(new TowerM(this, tower[1], xColumn, yRow, 1));
                        goldA -= 150;
                        tower1Flag = false;
                        sendTowerInfo(identity, xColumn, yRow, 1);
                    } else if (tower2Flag == true) {
                        TowersA.add(new TowerM(this, tower[2], xColumn, yRow, 2));
                        goldA -= 200;
                        tower2Flag = false;
                        sendTowerInfo(identity, xColumn, yRow, 2);
                    }
                    return true;
                } else {
                    tower0Flag = false;
                    tower1Flag = false;
                    tower2Flag = false;
                    CanSetTowerM.toDraw = false;
                    return true;
                }

            } else {

                if (Maps.MAP2[yRow][xColumn] == 0 && (tower0Flag == true || tower1Flag == true || tower2Flag == true)) {
                    if (tower0Flag == true) {
                        TowersB.add(new TowerM(this, tower[0], xColumn, yRow, 0));
                        goldB -= 100;
                        tower0Flag = false;
                        sendTowerInfo(identity, xColumn, yRow, 0);
                    } else if (tower1Flag == true) {
                        TowersB.add(new TowerM(this, tower[1], xColumn, yRow, 1));
                        goldB -= 150;
                        tower1Flag = false;
                        sendTowerInfo(identity, xColumn, yRow, 1);
                    } else if (tower2Flag == true) {
                        TowersB.add(new TowerM(this, tower[2], xColumn, yRow, 2));
                        goldB -= 200;
                        tower2Flag = false;
                        sendTowerInfo(identity, xColumn, yRow, 2);
                    }
                    return true;
                } else {
                    tower0Flag = false;
                    tower1Flag = false;
                    tower2Flag = false;
                    CanSetTowerM.toDraw = false;
                    return true;
                }

            }


        }
        return false;
    }

    public void GameOver() {

        if (winFlag || loseFlag || currentHPA == 0 || currentHPB == 0) {

            stopAllThreads();

            if (identity == 0 && currentHPA == 0) {
                sendFlag = true;
                towerInfo = new String("0 lose");
                activity.sendMessage(5);
            } else if (identity == 0 && currentHPB == 0) {
                sendFlag = true;
                towerInfo = new String("0 win");
                activity.sendMessage(4);
            } else if (identity == 1 && currentHPA == 0) {
                sendFlag = true;
                towerInfo = new String("1 win");
                activity.sendMessage(4);
            } else if (identity == 1 && currentHPB == 0) {
                sendFlag = true;
                towerInfo = new String("1 lose");
                activity.sendMessage(5);
            } else if (winFlag) {
                activity.sendMessage(4);
            } else if (loseFlag) {
                activity.sendMessage(5);
            } else {
                activity.sendMessage(2);
            }

        }
    }

    public void receiveTowerInfo(int identity, int xColumn, int yRow, int type) {

        if (identity == 0) {
            switch (type) {
                case 0:
                    TowersA.add(new TowerM(this, tower[0], xColumn, yRow, 0));
                    break;
                case 1:
                    TowersA.add(new TowerM(this, tower[1], xColumn, yRow, 1));
                    break;
                case 2:
                    TowersA.add(new TowerM(this, tower[2], xColumn, yRow, 2));
                    break;
                default:
                    break;
            }

        } else {
            switch (type) {
                case 0:
                    TowersB.add(new TowerM(this, tower[0], xColumn, yRow, 0));
                    break;
                case 1:
                    TowersB.add(new TowerM(this, tower[1], xColumn, yRow, 1));
                    break;
                case 2:
                    TowersB.add(new TowerM(this, tower[2], xColumn, yRow, 2));
                    break;
                default:
                    break;
            }
        }
    }

    public void sendTowerInfo(int identity, int xColumn, int yRow, int type) {
        sendFlag = true;
        towerInfo = new String(identity + " " + xColumn + " " + yRow + " " + type);
    }

    //This is called immediately after any structural changes (format or size) have been made to the surface.
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    public void surfaceCreated(SurfaceHolder holder) {

        //Start the online communication
        Thread clientThread = new Thread() {
            public void run() {
                Client client = new Client("Qifeng");
            }
        };
        clientThread.start();

        drawGameViewThreadM = new DrawGameViewThreadM(this);
        drawGameViewThreadM.start();
        enemyThreadAM = new EnemyThreadAM(this);
        enemyThreadAM.start();
        enemyThreadBM = new EnemyThreadBM(this);
        enemyThreadBM.start();
        ifGameOverThreadM = new GameOverThreadM(this);
        ifGameOverThreadM.start();
        enemyControlThreadAM = new EnemyControlThreadAM(this);
        enemyControlThreadAM.start();
        enemyControlThreadBM = new EnemyControlThreadBM(this);
        enemyControlThreadBM.start();
        inGameMenuThread = new InGameMenuThread(this);
        inGameMenuThread.start();
        bulletControlThreadAM = new BulletControlThreadAM(this);
        bulletControlThreadAM.start();
        bulletControlThreadBM = new BulletControlThreadBM(this);
        bulletControlThreadBM.start();
        bulletThreadAM = new BulletThreadAM(this);
        bulletThreadAM.start();
        bulletThreadBM = new BulletThreadBM(this);
        bulletThreadBM.start();
        crystalThreadAM = new CrystalThreadAM(this);
        crystalThreadAM.start();
        crystalThreadBM = new CrystalThreadBM(this);
        crystalThreadBM.start();
        crystalTimeThreadAM = new CrystalTimeThreadAM(this);
        crystalTimeThreadAM.start();
        crystalTimeThreadBM = new CrystalTimeThreadBM(this);
        crystalTimeThreadBM.start();


        //Initialize the background music
        mediaPlayer = MediaPlayer.create(activity, R.raw.bgm);
        mediaPlayer.setLooping(true);

        //Start the background music
        if (activity.isBackgroundMusicOn()) {
            mediaPlayer.start();
        }


    }

    public void surfaceDestroyed(SurfaceHolder holder) {

        stopAllThreads();

        //Stop the background music
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }

    }

    //Stop all enemies' frame changing animation
    void stopFrameChange() {
        for (int i = 0; i < EnemiesA.size(); i++) {
            EnemiesA.get(i).setFrameFlag(false);
        }

        for (int i = 0; i < EnemiesB.size(); i++) {
            EnemiesB.get(i).setFrameFlag(false);
        }

    }

    //Start all enemies' frame changing animation
    void startFrameChange() {
        for (int i = 0; i < EnemiesA.size(); i++) {
            EnemiesA.get(i).setFrameFlag(true);
        }
        for (int i = 0; i < EnemiesB.size(); i++) {
            EnemiesB.get(i).setFrameFlag(true);
        }
    }

    public void suspendAllThreads() {

        if (enemyThreadAM != null)
            enemyThreadAM.setFlag(false);
        if (enemyThreadBM != null)
            enemyThreadBM.setFlag(false);
        if (enemyControlThreadAM != null)
            enemyControlThreadAM.setFlag(false);
        if (enemyControlThreadBM != null)
            enemyControlThreadBM.setFlag(false);
        if (bulletControlThreadAM != null)
            bulletControlThreadAM.setFlag(false);
        if (bulletControlThreadBM != null)
            bulletControlThreadBM.setFlag(false);
        if (bulletThreadAM != null)
            bulletThreadAM.setFlag(false);
        if (bulletThreadBM != null)
            bulletThreadBM.setFlag(false);
        if (crystalTimeThreadAM != null)
            crystalTimeThreadAM.setFlag(false);
        if (crystalTimeThreadBM != null)
            crystalTimeThreadBM.setFlag(false);
    }

    public void continueAllThreads() {

        if (enemyThreadAM != null)
            enemyThreadAM.setFlag(true);
        if (enemyThreadBM != null)
            enemyThreadBM.setFlag(true);
        if (enemyControlThreadAM != null)
            enemyControlThreadAM.setFlag(true);
        if (enemyControlThreadBM != null)
            enemyControlThreadBM.setFlag(true);
        if (bulletControlThreadAM != null)
            bulletControlThreadAM.setFlag(true);
        if (bulletControlThreadBM != null)
            bulletControlThreadBM.setFlag(true);
        if (bulletThreadAM != null)
            bulletThreadAM.setFlag(true);
        if (bulletThreadBM != null)
            bulletThreadBM.setFlag(true);
        if (crystalTimeThreadAM != null)
            crystalTimeThreadAM.setFlag(true);
        if (crystalTimeThreadBM != null)
            crystalTimeThreadBM.setFlag(true);

    }

    void stopAllThreads() {
        if (drawGameViewThreadM != null)
            drawGameViewThreadM.setFlag(false);
        if (ifGameOverThreadM != null)
            ifGameOverThreadM.setFlag(false);
        if (enemyThreadAM != null)
            enemyThreadAM.setWhileFlag(false);
        if (enemyThreadBM != null)
            enemyThreadBM.setWhileFlag(false);
        if (enemyControlThreadAM != null)
            enemyControlThreadAM.setWhileFlag(false);
        if (enemyControlThreadBM != null)
            enemyControlThreadBM.setWhileFlag(false);
        if (inGameMenuThread != null)
            inGameMenuThread.setWhileFlag(false);
        if (bulletThreadAM != null)
            bulletThreadAM.setWhileFlag(false);
        if (bulletThreadBM != null)
            bulletThreadBM.setWhileFlag(false);
        if (bulletControlThreadAM != null)
            bulletControlThreadAM.setWhileFlag(false);
        if (bulletControlThreadBM != null)
            bulletControlThreadBM.setWhileFlag(false);
        if (crystalThreadAM != null)
            crystalThreadAM.setWhileFlag(false);
        if (crystalThreadBM != null)
            crystalThreadBM.setWhileFlag(false);
        if (crystalTimeThreadAM != null)
            crystalTimeThreadAM.setWhileFlag(false);
        if (crystalTimeThreadBM != null)
            crystalTimeThreadBM.setWhileFlag(false);


    }

    public void initBitmap() {
        background = BitmapFactory.decodeResource(this.getResources(), R.drawable.gameback);
        home = BitmapFactory.decodeResource(this.getResources(), R.drawable.home);
        path = BitmapFactory.decodeResource(this.getResources(), R.drawable.path);
        grass = BitmapFactory.decodeResource(this.getResources(), R.drawable.grass);
        lowTree = BitmapFactory.decodeResource(this.getResources(), R.drawable.lowtree);
        tallTree = BitmapFactory.decodeResource(this.getResources(), R.drawable.talltree);
        menuButton = BitmapFactory.decodeResource(this.getResources(), R.drawable.menubuttoningame);
        menu = BitmapFactory.decodeResource(this.getResources(), R.drawable.menuingame);
        enemyHpGreen = BitmapFactory.decodeResource(this.getResources(), R.drawable.enemyhpgreen);
        enemyHpRed = BitmapFactory.decodeResource(this.getResources(), R.drawable.enemyhpred);
        heart = BitmapFactory.decodeResource(this.getResources(), R.drawable.heart);
        bullet = BitmapFactory.decodeResource(this.getResources(), R.drawable.bullet);
        bullet2 = BitmapFactory.decodeResource(this.getResources(), R.drawable.bullet2);
        bullet3 = BitmapFactory.decodeResource(this.getResources(), R.drawable.bullet3);
        coin = BitmapFactory.decodeResource(this.getResources(), R.drawable.coin);
        killed = BitmapFactory.decodeResource(this.getResources(), R.drawable.killed);
        camp = BitmapFactory.decodeResource(this.getResources(), R.drawable.camp);
        tagYouR = BitmapFactory.decodeResource(this.getResources(), R.drawable.tagyour);
        tagYouB = BitmapFactory.decodeResource(this.getResources(), R.drawable.tagyoub);
        tagOpponentR = BitmapFactory.decodeResource(this.getResources(), R.drawable.tagopponentr);
        tagOpponentB = BitmapFactory.decodeResource(this.getResources(), R.drawable.tagopponentb);
        tagRed = BitmapFactory.decodeResource(this.getResources(), R.drawable.tagred);
        tagBlue = BitmapFactory.decodeResource(this.getResources(), R.drawable.tagblue);
        connected = BitmapFactory.decodeResource(this.getResources(), R.drawable.connected);
        waiting = BitmapFactory.decodeResource(this.getResources(), R.drawable.waiting);
        paired = BitmapFactory.decodeResource(this.getResources(), R.drawable.paired);
        crystal = BitmapFactory.decodeResource(this.getResources(), R.drawable.crystal);
        crystalCover = BitmapFactory.decodeResource(this.getResources(), R.drawable.crystalcover);
        crystalEffect = BitmapFactory.decodeResource(this.getResources(), R.drawable.crystaleffect);

        numbers[0] = BitmapFactory.decodeResource(this.getResources(), R.drawable.zero);
        numbers[1] = BitmapFactory.decodeResource(this.getResources(), R.drawable.one);
        numbers[2] = BitmapFactory.decodeResource(this.getResources(), R.drawable.two);
        numbers[3] = BitmapFactory.decodeResource(this.getResources(), R.drawable.three);
        numbers[4] = BitmapFactory.decodeResource(this.getResources(), R.drawable.four);
        numbers[5] = BitmapFactory.decodeResource(this.getResources(), R.drawable.five);
        numbers[6] = BitmapFactory.decodeResource(this.getResources(), R.drawable.six);
        numbers[7] = BitmapFactory.decodeResource(this.getResources(), R.drawable.seven);
        numbers[8] = BitmapFactory.decodeResource(this.getResources(), R.drawable.eight);
        numbers[9] = BitmapFactory.decodeResource(this.getResources(), R.drawable.nine);
        coinNumbers[0] = BitmapFactory.decodeResource(this.getResources(), R.drawable.coin0);
        coinNumbers[1] = BitmapFactory.decodeResource(this.getResources(), R.drawable.coin1);
        coinNumbers[2] = BitmapFactory.decodeResource(this.getResources(), R.drawable.coin2);
        coinNumbers[3] = BitmapFactory.decodeResource(this.getResources(), R.drawable.coin3);
        coinNumbers[4] = BitmapFactory.decodeResource(this.getResources(), R.drawable.coin4);
        coinNumbers[5] = BitmapFactory.decodeResource(this.getResources(), R.drawable.coin5);
        coinNumbers[6] = BitmapFactory.decodeResource(this.getResources(), R.drawable.coin6);
        coinNumbers[7] = BitmapFactory.decodeResource(this.getResources(), R.drawable.coin7);
        coinNumbers[8] = BitmapFactory.decodeResource(this.getResources(), R.drawable.coin8);
        coinNumbers[9] = BitmapFactory.decodeResource(this.getResources(), R.drawable.coin9);
        killNumbers[0] = BitmapFactory.decodeResource(this.getResources(), R.drawable.kill0);
        killNumbers[1] = BitmapFactory.decodeResource(this.getResources(), R.drawable.kill1);
        killNumbers[2] = BitmapFactory.decodeResource(this.getResources(), R.drawable.kill2);
        killNumbers[3] = BitmapFactory.decodeResource(this.getResources(), R.drawable.kill3);
        killNumbers[4] = BitmapFactory.decodeResource(this.getResources(), R.drawable.kill4);
        killNumbers[5] = BitmapFactory.decodeResource(this.getResources(), R.drawable.kill5);
        killNumbers[6] = BitmapFactory.decodeResource(this.getResources(), R.drawable.kill6);
        killNumbers[7] = BitmapFactory.decodeResource(this.getResources(), R.drawable.kill7);
        killNumbers[8] = BitmapFactory.decodeResource(this.getResources(), R.drawable.kill8);
        killNumbers[9] = BitmapFactory.decodeResource(this.getResources(), R.drawable.kill9);
        crystalNumbers[0] = BitmapFactory.decodeResource(this.getResources(), R.drawable.crystal0);
        crystalNumbers[1] = BitmapFactory.decodeResource(this.getResources(), R.drawable.crystal1);
        crystalNumbers[2] = BitmapFactory.decodeResource(this.getResources(), R.drawable.crystal2);
        crystalNumbers[3] = BitmapFactory.decodeResource(this.getResources(), R.drawable.crystal3);
        crystalNumbers[4] = BitmapFactory.decodeResource(this.getResources(), R.drawable.crystal4);
        crystalNumbers[5] = BitmapFactory.decodeResource(this.getResources(), R.drawable.crystal5);
        crystalNumbers[6] = BitmapFactory.decodeResource(this.getResources(), R.drawable.crystal6);
        crystalNumbers[7] = BitmapFactory.decodeResource(this.getResources(), R.drawable.crystal7);
        crystalNumbers[8] = BitmapFactory.decodeResource(this.getResources(), R.drawable.crystal8);
        crystalNumbers[9] = BitmapFactory.decodeResource(this.getResources(), R.drawable.crystal9);
        tower[0] = BitmapFactory.decodeResource(this.getResources(), R.drawable.tower1);
        tower[1] = BitmapFactory.decodeResource(this.getResources(), R.drawable.tower2);
        tower[2] = BitmapFactory.decodeResource(this.getResources(), R.drawable.tower3);
        towerButton[0] = BitmapFactory.decodeResource(this.getResources(), R.drawable.towerbutton1);
        towerButton[1] = BitmapFactory.decodeResource(this.getResources(), R.drawable.towerbutton2);
        towerButton[2] = BitmapFactory.decodeResource(this.getResources(), R.drawable.towerbutton3);
        towerRange[0] = BitmapFactory.decodeResource(this.getResources(), R.drawable.towerrange0);
        towerRange[1] = BitmapFactory.decodeResource(this.getResources(), R.drawable.towerrange1);
        towerRange[2] = BitmapFactory.decodeResource(this.getResources(), R.drawable.towerrange2);
        enemyFront[0][0] = BitmapFactory.decodeResource(this.getResources(), R.drawable.a01);
        enemyFront[0][1] = BitmapFactory.decodeResource(this.getResources(), R.drawable.a02);
        enemyFront[0][2] = BitmapFactory.decodeResource(this.getResources(), R.drawable.a03);
        enemyFront[0][3] = BitmapFactory.decodeResource(this.getResources(), R.drawable.a04);
        enemyRight[0][0] = BitmapFactory.decodeResource(this.getResources(), R.drawable.a05);
        enemyRight[0][1] = BitmapFactory.decodeResource(this.getResources(), R.drawable.a06);
        enemyRight[0][2] = BitmapFactory.decodeResource(this.getResources(), R.drawable.a07);
        enemyRight[0][3] = BitmapFactory.decodeResource(this.getResources(), R.drawable.a08);
        enemyBack[0][0] = BitmapFactory.decodeResource(this.getResources(), R.drawable.a09);
        enemyBack[0][1] = BitmapFactory.decodeResource(this.getResources(), R.drawable.a10);
        enemyBack[0][2] = BitmapFactory.decodeResource(this.getResources(), R.drawable.a11);
        enemyBack[0][3] = BitmapFactory.decodeResource(this.getResources(), R.drawable.a12);
        enemyLeft[0][0] = BitmapFactory.decodeResource(this.getResources(), R.drawable.a13);
        enemyLeft[0][1] = BitmapFactory.decodeResource(this.getResources(), R.drawable.a14);
        enemyLeft[0][2] = BitmapFactory.decodeResource(this.getResources(), R.drawable.a15);
        enemyLeft[0][3] = BitmapFactory.decodeResource(this.getResources(), R.drawable.a16);
        enemyFront[1][0] = BitmapFactory.decodeResource(this.getResources(), R.drawable.b01);
        enemyFront[1][1] = BitmapFactory.decodeResource(this.getResources(), R.drawable.b02);
        enemyFront[1][2] = BitmapFactory.decodeResource(this.getResources(), R.drawable.b03);
        enemyFront[1][3] = BitmapFactory.decodeResource(this.getResources(), R.drawable.b04);
        enemyRight[1][0] = BitmapFactory.decodeResource(this.getResources(), R.drawable.b05);
        enemyRight[1][1] = BitmapFactory.decodeResource(this.getResources(), R.drawable.b06);
        enemyRight[1][2] = BitmapFactory.decodeResource(this.getResources(), R.drawable.b07);
        enemyRight[1][3] = BitmapFactory.decodeResource(this.getResources(), R.drawable.b08);
        enemyBack[1][0] = BitmapFactory.decodeResource(this.getResources(), R.drawable.b09);
        enemyBack[1][1] = BitmapFactory.decodeResource(this.getResources(), R.drawable.b10);
        enemyBack[1][2] = BitmapFactory.decodeResource(this.getResources(), R.drawable.b11);
        enemyBack[1][3] = BitmapFactory.decodeResource(this.getResources(), R.drawable.b12);
        enemyLeft[1][0] = BitmapFactory.decodeResource(this.getResources(), R.drawable.b13);
        enemyLeft[1][1] = BitmapFactory.decodeResource(this.getResources(), R.drawable.b14);
        enemyLeft[1][2] = BitmapFactory.decodeResource(this.getResources(), R.drawable.b15);
        enemyLeft[1][3] = BitmapFactory.decodeResource(this.getResources(), R.drawable.b16);


        //Compute scale rate
        float wScaleRate = screenWidth / (float) background.getWidth();
        float hScaleRate = screenHeight / (float) background.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(wScaleRate, hScaleRate);

        //Obtain new image
        background = Bitmap.createBitmap(background, 0, 0, background.getWidth(), background.getHeight(), matrix, true);
        home = Bitmap.createBitmap(home, 0, 0, home.getWidth(), home.getHeight(), matrix, true);
        path = Bitmap.createBitmap(path, 0, 0, path.getWidth(), path.getHeight(), matrix, true);
        grass = Bitmap.createBitmap(grass, 0, 0, grass.getWidth(), grass.getHeight(), matrix, true);
        lowTree = Bitmap.createBitmap(lowTree, 0, 0, lowTree.getWidth(), lowTree.getHeight(), matrix, true);
        tallTree = Bitmap.createBitmap(tallTree, 0, 0, tallTree.getWidth(), tallTree.getHeight(), matrix, true);
        menuButton = Bitmap.createBitmap(menuButton, 0, 0, menuButton.getWidth(), menuButton.getHeight(), matrix, true);
        menu = Bitmap.createBitmap(menu, 0, 0, menu.getWidth(), menu.getHeight(), matrix, true);
        enemyHpGreen = Bitmap.createBitmap(enemyHpGreen, 0, 0, enemyHpGreen.getWidth(), enemyHpGreen.getHeight(), matrix, true);
        enemyHpRed = Bitmap.createBitmap(enemyHpRed, 0, 0, enemyHpRed.getWidth(), enemyHpRed.getHeight(), matrix, true);
        heart = Bitmap.createBitmap(heart, 0, 0, heart.getWidth(), heart.getHeight(), matrix, true);
        bullet = Bitmap.createBitmap(bullet, 0, 0, bullet.getWidth(), bullet.getHeight(), matrix, true);
        bullet2 = Bitmap.createBitmap(bullet2, 0, 0, bullet2.getWidth(), bullet2.getHeight(), matrix, true);
        bullet3 = Bitmap.createBitmap(bullet3, 0, 0, bullet3.getWidth(), bullet3.getHeight(), matrix, true);
        coin = Bitmap.createBitmap(coin, 0, 0, coin.getWidth(), coin.getHeight(), matrix, true);
        killed = Bitmap.createBitmap(killed, 0, 0, killed.getWidth(), killed.getHeight(), matrix, true);
        camp = Bitmap.createBitmap(camp, 0, 0, camp.getWidth(), camp.getHeight(), matrix, true);
        crystal = Bitmap.createBitmap(crystal, 0, 0, crystal.getWidth(), crystal.getHeight(), matrix, true);
        crystalCover = Bitmap.createBitmap(crystalCover, 0, 0, crystalCover.getWidth(), crystalCover.getHeight(), matrix, true);
        crystalEffect = Bitmap.createBitmap(crystalEffect, 0, 0, crystalEffect.getWidth(), crystalEffect.getHeight(), matrix, true);
        tagYouR = Bitmap.createBitmap(tagYouR, 0, 0, tagYouR.getWidth(), tagYouR.getHeight(), matrix, true);
        tagYouB = Bitmap.createBitmap(tagYouB, 0, 0, tagYouB.getWidth(), tagYouB.getHeight(), matrix, true);
        tagOpponentR = Bitmap.createBitmap(tagOpponentR, 0, 0, tagOpponentR.getWidth(), tagOpponentR.getHeight(), matrix, true);
        tagOpponentB = Bitmap.createBitmap(tagOpponentB, 0, 0, tagOpponentB.getWidth(), tagOpponentB.getHeight(), matrix, true);
        tagRed = Bitmap.createBitmap(tagRed, 0, 0, tagRed.getWidth(), tagRed.getHeight(), matrix, true);
        tagBlue = Bitmap.createBitmap(tagBlue, 0, 0, tagBlue.getWidth(), tagBlue.getHeight(), matrix, true);
        connected = Bitmap.createBitmap(connected, 0, 0, connected.getWidth(), connected.getHeight(), matrix, true);
        waiting = Bitmap.createBitmap(waiting, 0, 0, waiting.getWidth(), waiting.getHeight(), matrix, true);
        paired = Bitmap.createBitmap(paired, 0, 0, paired.getWidth(), paired.getHeight(), matrix, true);

        numbers[0] = Bitmap.createBitmap(numbers[0], 0, 0, numbers[0].getWidth(), numbers[0].getHeight(), matrix, true);
        numbers[1] = Bitmap.createBitmap(numbers[1], 0, 0, numbers[1].getWidth(), numbers[1].getHeight(), matrix, true);
        numbers[2] = Bitmap.createBitmap(numbers[2], 0, 0, numbers[2].getWidth(), numbers[2].getHeight(), matrix, true);
        numbers[3] = Bitmap.createBitmap(numbers[3], 0, 0, numbers[3].getWidth(), numbers[3].getHeight(), matrix, true);
        numbers[4] = Bitmap.createBitmap(numbers[4], 0, 0, numbers[4].getWidth(), numbers[4].getHeight(), matrix, true);
        numbers[5] = Bitmap.createBitmap(numbers[5], 0, 0, numbers[5].getWidth(), numbers[5].getHeight(), matrix, true);
        numbers[6] = Bitmap.createBitmap(numbers[6], 0, 0, numbers[6].getWidth(), numbers[6].getHeight(), matrix, true);
        numbers[7] = Bitmap.createBitmap(numbers[7], 0, 0, numbers[7].getWidth(), numbers[7].getHeight(), matrix, true);
        numbers[8] = Bitmap.createBitmap(numbers[8], 0, 0, numbers[8].getWidth(), numbers[8].getHeight(), matrix, true);
        numbers[9] = Bitmap.createBitmap(numbers[9], 0, 0, numbers[9].getWidth(), numbers[9].getHeight(), matrix, true);
        coinNumbers[0] = Bitmap.createBitmap(coinNumbers[0], 0, 0, coinNumbers[0].getWidth(), coinNumbers[0].getHeight(), matrix, true);
        coinNumbers[1] = Bitmap.createBitmap(coinNumbers[1], 0, 0, coinNumbers[1].getWidth(), coinNumbers[1].getHeight(), matrix, true);
        coinNumbers[2] = Bitmap.createBitmap(coinNumbers[2], 0, 0, coinNumbers[2].getWidth(), coinNumbers[2].getHeight(), matrix, true);
        coinNumbers[3] = Bitmap.createBitmap(coinNumbers[3], 0, 0, coinNumbers[3].getWidth(), coinNumbers[3].getHeight(), matrix, true);
        coinNumbers[4] = Bitmap.createBitmap(coinNumbers[4], 0, 0, coinNumbers[4].getWidth(), coinNumbers[4].getHeight(), matrix, true);
        coinNumbers[5] = Bitmap.createBitmap(coinNumbers[5], 0, 0, coinNumbers[5].getWidth(), coinNumbers[5].getHeight(), matrix, true);
        coinNumbers[6] = Bitmap.createBitmap(coinNumbers[6], 0, 0, coinNumbers[6].getWidth(), coinNumbers[6].getHeight(), matrix, true);
        coinNumbers[7] = Bitmap.createBitmap(coinNumbers[7], 0, 0, coinNumbers[7].getWidth(), coinNumbers[7].getHeight(), matrix, true);
        coinNumbers[8] = Bitmap.createBitmap(coinNumbers[8], 0, 0, coinNumbers[8].getWidth(), coinNumbers[8].getHeight(), matrix, true);
        coinNumbers[9] = Bitmap.createBitmap(coinNumbers[9], 0, 0, coinNumbers[9].getWidth(), coinNumbers[9].getHeight(), matrix, true);
        killNumbers[0] = Bitmap.createBitmap(killNumbers[0], 0, 0, killNumbers[0].getWidth(), killNumbers[0].getHeight(), matrix, true);
        killNumbers[1] = Bitmap.createBitmap(killNumbers[1], 0, 0, killNumbers[1].getWidth(), killNumbers[1].getHeight(), matrix, true);
        killNumbers[2] = Bitmap.createBitmap(killNumbers[2], 0, 0, killNumbers[2].getWidth(), killNumbers[2].getHeight(), matrix, true);
        killNumbers[3] = Bitmap.createBitmap(killNumbers[3], 0, 0, killNumbers[3].getWidth(), killNumbers[3].getHeight(), matrix, true);
        killNumbers[4] = Bitmap.createBitmap(killNumbers[4], 0, 0, killNumbers[4].getWidth(), killNumbers[4].getHeight(), matrix, true);
        killNumbers[5] = Bitmap.createBitmap(killNumbers[5], 0, 0, killNumbers[5].getWidth(), killNumbers[5].getHeight(), matrix, true);
        killNumbers[6] = Bitmap.createBitmap(killNumbers[6], 0, 0, killNumbers[6].getWidth(), killNumbers[6].getHeight(), matrix, true);
        killNumbers[7] = Bitmap.createBitmap(killNumbers[7], 0, 0, killNumbers[7].getWidth(), killNumbers[7].getHeight(), matrix, true);
        killNumbers[8] = Bitmap.createBitmap(killNumbers[8], 0, 0, killNumbers[8].getWidth(), killNumbers[8].getHeight(), matrix, true);
        killNumbers[9] = Bitmap.createBitmap(killNumbers[9], 0, 0, killNumbers[9].getWidth(), killNumbers[9].getHeight(), matrix, true);
        crystalNumbers[0] = Bitmap.createBitmap(crystalNumbers[0], 0, 0, crystalNumbers[0].getWidth(), crystalNumbers[0].getHeight(), matrix, true);
        crystalNumbers[1] = Bitmap.createBitmap(crystalNumbers[1], 0, 0, crystalNumbers[1].getWidth(), crystalNumbers[1].getHeight(), matrix, true);
        crystalNumbers[2] = Bitmap.createBitmap(crystalNumbers[2], 0, 0, crystalNumbers[2].getWidth(), crystalNumbers[2].getHeight(), matrix, true);
        crystalNumbers[3] = Bitmap.createBitmap(crystalNumbers[3], 0, 0, crystalNumbers[3].getWidth(), crystalNumbers[3].getHeight(), matrix, true);
        crystalNumbers[4] = Bitmap.createBitmap(crystalNumbers[4], 0, 0, crystalNumbers[4].getWidth(), crystalNumbers[4].getHeight(), matrix, true);
        crystalNumbers[5] = Bitmap.createBitmap(crystalNumbers[5], 0, 0, crystalNumbers[5].getWidth(), crystalNumbers[5].getHeight(), matrix, true);
        crystalNumbers[6] = Bitmap.createBitmap(crystalNumbers[6], 0, 0, crystalNumbers[6].getWidth(), crystalNumbers[6].getHeight(), matrix, true);
        crystalNumbers[7] = Bitmap.createBitmap(crystalNumbers[7], 0, 0, crystalNumbers[7].getWidth(), crystalNumbers[7].getHeight(), matrix, true);
        crystalNumbers[8] = Bitmap.createBitmap(crystalNumbers[8], 0, 0, crystalNumbers[8].getWidth(), crystalNumbers[8].getHeight(), matrix, true);
        crystalNumbers[9] = Bitmap.createBitmap(crystalNumbers[9], 0, 0, crystalNumbers[9].getWidth(), crystalNumbers[9].getHeight(), matrix, true);
        tower[0] = Bitmap.createBitmap(tower[0], 0, 0, tower[0].getWidth(), tower[0].getHeight(), matrix, true);
        tower[1] = Bitmap.createBitmap(tower[1], 0, 0, tower[1].getWidth(), tower[1].getHeight(), matrix, true);
        tower[2] = Bitmap.createBitmap(tower[2], 0, 0, tower[2].getWidth(), tower[2].getHeight(), matrix, true);
        towerButton[0] = Bitmap.createBitmap(towerButton[0], 0, 0, towerButton[0].getWidth(), towerButton[0].getHeight(), matrix, true);
        towerButton[1] = Bitmap.createBitmap(towerButton[1], 0, 0, towerButton[1].getWidth(), towerButton[1].getHeight(), matrix, true);
        towerButton[2] = Bitmap.createBitmap(towerButton[2], 0, 0, towerButton[2].getWidth(), towerButton[2].getHeight(), matrix, true);
        towerRange[0] = Bitmap.createBitmap(towerRange[0], 0, 0, towerRange[0].getWidth(), towerRange[0].getHeight(), matrix, true);
        towerRange[1] = Bitmap.createBitmap(towerRange[1], 0, 0, towerRange[1].getWidth(), towerRange[1].getHeight(), matrix, true);
        towerRange[2] = Bitmap.createBitmap(towerRange[2], 0, 0, towerRange[2].getWidth(), towerRange[2].getHeight(), matrix, true);
        enemyFront[0][0] = Bitmap.createBitmap(enemyFront[0][0], 0, 0, enemyFront[0][0].getWidth(), enemyFront[0][0].getHeight(), matrix, true);
        enemyFront[0][1] = Bitmap.createBitmap(enemyFront[0][1], 0, 0, enemyFront[0][1].getWidth(), enemyFront[0][1].getHeight(), matrix, true);
        enemyFront[0][2] = Bitmap.createBitmap(enemyFront[0][2], 0, 0, enemyFront[0][2].getWidth(), enemyFront[0][2].getHeight(), matrix, true);
        enemyFront[0][3] = Bitmap.createBitmap(enemyFront[0][3], 0, 0, enemyFront[0][3].getWidth(), enemyFront[0][3].getHeight(), matrix, true);
        enemyRight[0][0] = Bitmap.createBitmap(enemyRight[0][0], 0, 0, enemyRight[0][0].getWidth(), enemyRight[0][0].getHeight(), matrix, true);
        enemyRight[0][1] = Bitmap.createBitmap(enemyRight[0][1], 0, 0, enemyRight[0][1].getWidth(), enemyRight[0][1].getHeight(), matrix, true);
        enemyRight[0][2] = Bitmap.createBitmap(enemyRight[0][2], 0, 0, enemyRight[0][2].getWidth(), enemyRight[0][2].getHeight(), matrix, true);
        enemyRight[0][3] = Bitmap.createBitmap(enemyRight[0][3], 0, 0, enemyRight[0][3].getWidth(), enemyRight[0][3].getHeight(), matrix, true);
        enemyBack[0][0] = Bitmap.createBitmap(enemyBack[0][0], 0, 0, enemyBack[0][0].getWidth(), enemyBack[0][0].getHeight(), matrix, true);
        enemyBack[0][1] = Bitmap.createBitmap(enemyBack[0][1], 0, 0, enemyBack[0][1].getWidth(), enemyBack[0][1].getHeight(), matrix, true);
        enemyBack[0][2] = Bitmap.createBitmap(enemyBack[0][2], 0, 0, enemyBack[0][2].getWidth(), enemyBack[0][2].getHeight(), matrix, true);
        enemyBack[0][3] = Bitmap.createBitmap(enemyBack[0][3], 0, 0, enemyBack[0][3].getWidth(), enemyBack[0][3].getHeight(), matrix, true);
        enemyLeft[0][0] = Bitmap.createBitmap(enemyLeft[0][0], 0, 0, enemyLeft[0][0].getWidth(), enemyLeft[0][0].getHeight(), matrix, true);
        enemyLeft[0][1] = Bitmap.createBitmap(enemyLeft[0][1], 0, 0, enemyLeft[0][1].getWidth(), enemyLeft[0][1].getHeight(), matrix, true);
        enemyLeft[0][2] = Bitmap.createBitmap(enemyLeft[0][2], 0, 0, enemyLeft[0][2].getWidth(), enemyLeft[0][2].getHeight(), matrix, true);
        enemyLeft[0][3] = Bitmap.createBitmap(enemyLeft[0][3], 0, 0, enemyLeft[0][3].getWidth(), enemyLeft[0][3].getHeight(), matrix, true);
        enemyFront[1][0] = Bitmap.createBitmap(enemyFront[1][0], 0, 0, enemyFront[1][0].getWidth(), enemyFront[1][0].getHeight(), matrix, true);
        enemyFront[1][1] = Bitmap.createBitmap(enemyFront[1][1], 0, 0, enemyFront[1][1].getWidth(), enemyFront[1][1].getHeight(), matrix, true);
        enemyFront[1][2] = Bitmap.createBitmap(enemyFront[1][2], 0, 0, enemyFront[1][2].getWidth(), enemyFront[1][2].getHeight(), matrix, true);
        enemyFront[1][3] = Bitmap.createBitmap(enemyFront[1][3], 0, 0, enemyFront[1][3].getWidth(), enemyFront[1][3].getHeight(), matrix, true);
        enemyRight[1][0] = Bitmap.createBitmap(enemyRight[1][0], 0, 0, enemyRight[1][0].getWidth(), enemyRight[1][0].getHeight(), matrix, true);
        enemyRight[1][1] = Bitmap.createBitmap(enemyRight[1][1], 0, 0, enemyRight[1][1].getWidth(), enemyRight[1][1].getHeight(), matrix, true);
        enemyRight[1][2] = Bitmap.createBitmap(enemyRight[1][2], 0, 0, enemyRight[1][2].getWidth(), enemyRight[1][2].getHeight(), matrix, true);
        enemyRight[1][3] = Bitmap.createBitmap(enemyRight[1][3], 0, 0, enemyRight[1][3].getWidth(), enemyRight[1][3].getHeight(), matrix, true);
        enemyBack[1][0] = Bitmap.createBitmap(enemyBack[1][0], 0, 0, enemyBack[1][0].getWidth(), enemyBack[1][0].getHeight(), matrix, true);
        enemyBack[1][1] = Bitmap.createBitmap(enemyBack[1][1], 0, 0, enemyBack[1][1].getWidth(), enemyBack[1][1].getHeight(), matrix, true);
        enemyBack[1][2] = Bitmap.createBitmap(enemyBack[1][2], 0, 0, enemyBack[1][2].getWidth(), enemyBack[1][2].getHeight(), matrix, true);
        enemyBack[1][3] = Bitmap.createBitmap(enemyBack[1][3], 0, 0, enemyBack[1][3].getWidth(), enemyBack[1][3].getHeight(), matrix, true);
        enemyLeft[1][0] = Bitmap.createBitmap(enemyLeft[1][0], 0, 0, enemyLeft[1][0].getWidth(), enemyLeft[1][0].getHeight(), matrix, true);
        enemyLeft[1][1] = Bitmap.createBitmap(enemyLeft[1][1], 0, 0, enemyLeft[1][1].getWidth(), enemyLeft[1][1].getHeight(), matrix, true);
        enemyLeft[1][2] = Bitmap.createBitmap(enemyLeft[1][2], 0, 0, enemyLeft[1][2].getWidth(), enemyLeft[1][2].getHeight(), matrix, true);
        enemyLeft[1][3] = Bitmap.createBitmap(enemyLeft[1][3], 0, 0, enemyLeft[1][3].getWidth(), enemyLeft[1][3].getHeight(), matrix, true);

    }

    public void changeCrystalPosition(int angle) {
        if (angle >= 0 && angle < 90) {
            crystalX = (float) (crystalStartX + crystal.getWidth() / 2 - crystal.getHeight() / 2 * Math.tan((45 - angle) * Math.PI / 180));
            crystalY = crystalStartY;
        } else if (angle >= 90 && angle < 180) {
            crystalX = crystalStartX + crystal.getWidth();
            crystalY = (float) (crystalStartY + crystal.getHeight() / 2 - crystal.getWidth() / 2 * Math.tan((135 - angle) * Math.PI / 180));
        } else if (angle >= 180 && angle < 270) {
            crystalX = (float) (crystalStartX + crystal.getWidth() / 2 + crystal.getHeight() / 2 * Math.tan((225 - angle) * Math.PI / 180));
            crystalY = crystalStartY + crystal.getHeight();
        } else if (angle >= 270 && angle <= 360) {
            crystalX = crystalStartX;
            crystalY = (float) (crystalStartY + crystal.getHeight() / 2 + crystal.getWidth() / 2 * Math.tan((315 - angle) * Math.PI / 180));
        }

    }

    private Path makeCrystalPathDash(float x6, float y6) {

        Path p = new Path();

        float x1 = crystalStartX + crystal.getWidth() / 2;
        float y1 = crystalStartY + crystal.getHeight() / 2;

        float x2 = crystalStartX + crystal.getWidth();
        float y2 = crystalStartY;

        float x3 = crystalStartX + crystal.getWidth();
        float y3 = crystalStartY + crystal.getHeight();

        float x4 = crystalStartX;
        float y4 = crystalStartY + crystal.getHeight();

        float x5 = crystalStartX;
        float y5 = crystalStartY;
        if (y6 == crystalStartY) {
            p.moveTo(x1, y1);
            p.lineTo(x6, y6);
            p.lineTo(x2, y2);
            p.lineTo(x3, y3);
            p.lineTo(x4, y4);
            p.lineTo(x5, y5);
            p.lineTo(x1, y1);
            return p;
        } else if (x6 == crystalStartX + crystal.getWidth()) {
            p.moveTo(x1, y1);
            p.lineTo(x6, y6);
            p.lineTo(x3, y3);
            p.lineTo(x4, y4);
            p.lineTo(x5, y5);
            p.lineTo(x1, y1);
            return p;
        } else if (y6 == crystalStartY + crystal.getHeight()) {
            p.moveTo(x1, y1);
            p.lineTo(x6, y6);
            p.lineTo(x4, y4);
            p.lineTo(x5, y5);
            p.lineTo(x1, y1);
            return p;
        } else if (x6 == crystalStartX) {
            p.moveTo(x1, y1);
            p.lineTo(x6, y6);
            p.lineTo(x5, y5);
            p.lineTo(x1, y1);
            return p;
        }
        return null;
    }

    private class InGameMenuThread extends Thread {

        MultiGameView multiGameView;
        boolean flag = false;
        boolean whileFlag = true;
        int sleepSpan = 10;//latency time ms


        public InGameMenuThread(MultiGameView multiGameView) {
            this.multiGameView = multiGameView;
            flag = false;
        }

        public void run() {
            while (whileFlag) {
                if (flag) {
                    multiGameView.menuPositionY += 5;
                    if (multiGameView.menuPositionY >= (60f * (screenHeight / 720f))) {
                        multiGameView.menuPositionY = 60f * (screenHeight / 720f);
                    }
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


    public class Client {

        String name;
        PrintWriter out;
        private final String serverHostName = "proj-309-ss-02.cs.iastate.edu";
        private final int serverPortNumber = 4444;
        ServerListener sl;
        Socket serverSocket;

        public Client(String name) {

            this.name = name;

            try {
                serverSocket = new Socket(serverHostName, serverPortNumber);

            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            sl = new ServerListener(serverSocket);
            listener = new Thread(sl);
            listener.start();

            try {

                out = new PrintWriter(new BufferedOutputStream(serverSocket.getOutputStream()));
                out.println("waiting");
                out.flush();
                speaker = new Thread(new Speaker(serverSocket));
                speaker.start();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    class ServerListener implements Runnable {

        Scanner in;
        boolean flag = true;
        boolean whileFlag = true;

        ServerListener(Socket s) {
            try {
                in = new Scanner(new BufferedInputStream(s.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            while (whileFlag) {
                if (flag) {
                    if (in.hasNextLine()) {
                        String temp = in.nextLine();

                        if (!waitingFlag && temp.equals("waiting")) {

                            if (identity == -1)
                                identity = 0;

                            drawPairedFlag = false;
                            pairedFlag = false;
                            waitingFlag = true;

                        } else if (waitingFlag && temp.equals("waiting")) {
                            identity = 1;
                            sendFlag = true;
                            towerInfo = new String("paired");

                        } else if (temp.equals("paired")) {
                            waitingFlag = false;
                            pairedFlag = true;
                            drawPairedFlag = true;

                        } else if (temp.equals("menu")) {
                            menuFlag = true;
                            connectedFlag = false;
                            suspendAllThreads();

                        } else if (menuFlag == true && temp.equals("continue")) {
                            menuFlag = false;
                            menuPositionY = -600;
                            continueAllThreads();
                        } else if (menuFlag == true && temp.equals("end")) {
                            try {
                                menuFlag = false;
                                menuPositionY = -600;
                                stopAllThreads();
                                activity.sendMessage(1);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else if (identity == 0 && temp.equals("1 lose")) {
                            loseFlag = false;
                            winFlag = true;
                        } else if (identity == 1 && temp.equals("0 lose")) {
                            loseFlag = false;
                            winFlag = true;
                        } else if (identity == 0 && temp.equals("1 win")) {
                            winFlag = false;
                            loseFlag = true;
                        } else if (identity == 1 && temp.equals("0 win")) {
                            winFlag = false;
                            loseFlag = true;
                        } else if (identity == 0 && temp.equals("1 crystal")) {

                            EnemiesB.clear();
                            crystalTimeThreadBM.setFlag(true);
                            crystalCoolDownFlagB = true;
                            crystalEffectFlagB = true;
                            crystalThreadBM.setFlag(true);
                            crystalNumB -= 1;
                            crystalFlagB = false;

                        } else if (identity == 1 && temp.equals("0 crystal")) {

                            EnemiesA.clear();
                            crystalTimeThreadAM.setFlag(true);
                            crystalCoolDownFlagA = true;
                            crystalEffectFlagA = true;
                            crystalThreadAM.setFlag(true);
                            crystalNumA -= 1;
                            crystalFlagA = false;

                        } else {

                            receiveFlag = true;
                            Scanner scan = new Scanner(temp);

                            if (scan.hasNextInt()) {
                                int identityTemp = scan.nextInt();
                                if (identityTemp != identity) {
                                    receiveTowerInfo(identityTemp, scan.nextInt(), scan.nextInt(), scan.nextInt());
                                }
                            }
                        }
                    }
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

    class Speaker implements Runnable {

        Scanner in;
        Socket socket;
        PrintWriter out;
        boolean flag = true;
        boolean whileFlag = true;

        Speaker(Socket s) {
            try {
                socket = s;
                out = new PrintWriter(new BufferedOutputStream(socket.getOutputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            while (whileFlag) {
                if (flag) {
                    if (sendFlag) {
                        if (towerInfo != null) {
                            in = new Scanner(towerInfo);
                            if (in.hasNext()) {
                                out.println(in.nextLine());
                                out.flush();
                            }
                            towerInfo = null;
                        } else if (message != null) {

                            in = new Scanner(message);
                            if (in.hasNext()) {
                                out.println(in.nextLine());
                                out.flush();
                            }
                        }
                        sendFlag = false;
                    }
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
}
