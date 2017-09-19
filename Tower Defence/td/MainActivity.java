package com.example.qifeng.td;

import android.app.Service;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.qifeng.td.GameRelated.*;
import com.example.qifeng.td.GameRelated.Multi.MultiGameView;
import com.example.qifeng.td.GameRelated.Single.SingleGameView;
import com.example.qifeng.td.LoginRelated.LoginRequest;
import com.example.qifeng.td.LoginRelated.MainScreenActivity;
import com.example.qifeng.td.LoginRelated.RegisterActivity;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

enum WhichView {WELCOME_VIEW, SINGLE_GAME_VIEW, MULTI_GAME_VIEW, MENU_VIEW, SETTING_VIEW, MSA};

public class MainActivity extends AppCompatActivity {


    SingleGameView singleGameView;
    MultiGameView multiGameView;
    WelcomeView welcomeView;
    MenuView mainMenuView;
    SettingView settingView;
    GameOverView gameOverView;

    //Flags used when the game overs
    private boolean modeFlag; // Single Game Mode: false  Multi Game mode: true
    private boolean winnerFlag; // Current user is the winner: true   Another user is the winner: false

    //Flag used for choosing map
    public static boolean mapFlag = false;

    //Flags used to control background music
    private boolean backgroundMusicOn = true;
    private boolean soundOn = true;

    //Variables used for vibration
    Vibrator vibrator;
    boolean vibrationFlag = true;

    public static int screenWidth;
    public static int screenHeight;
    WhichView currentView;

    public boolean returnFlag = false;

    //handle with messages from SurfaceViews
    public Handler myHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    toSingleGameView();
                    break;
                case 1:
                    toMainMenuView();
                    break;
                case 2:
                    modeFlag = false;
                    toGameOverView();
                    break;
                case 3:
                    toMultiGameView();
                    break;
                case 4:
                    modeFlag = true;
                    winnerFlag = true;
                    toGameOverView();
                    break;
                case 5:
                    modeFlag = true;
                    winnerFlag = false;
                    toGameOverView();
                case 6:
                    toSettingView();
                    break;

            }
        }
    };
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    private static MainActivity instance;

    public static MainActivity getInstance() {
        return instance;
    }

    public static void setInstance(MainActivity instance) {
        MainActivity.instance = instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setInstance(this);

        final EditText etUserName = (EditText) findViewById(R.id.etUsername);
        final EditText etPassword = (EditText) findViewById(R.id.etPassword);

        final Button bLogin = (Button) findViewById(R.id.bLogin);

        final TextView registerLink = (TextView) findViewById(R.id.tvRegisterhere);

        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(MainActivity.this, RegisterActivity.class);
                MainActivity.this.startActivity(registerIntent);
            }
        });

        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String username = etUserName.getText().toString();
                final String password = etPassword.getText().toString();


                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if (success) {

                                String name = jsonResponse.getString("name");
                                String email = jsonResponse.getString("email");
                                int coin = jsonResponse.getInt("coin");
                                int level = jsonResponse.getInt("level");
                                int crystal = jsonResponse.getInt("crystal");
                                int experience = jsonResponse.getInt("experience");


                                Intent intent = new Intent(MainActivity.this, MainScreenActivity.class);
                                intent.putExtra("name", name);
                                intent.putExtra("username", username);
                                intent.putExtra("email", email);
                                intent.putExtra("coin", coin);
                                intent.putExtra("level", level);
                                intent.putExtra("crystal", crystal);
                                intent.putExtra("experience", experience);


                                MainActivity.this.startActivity(intent);


                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setMessage("Login failed").setNegativeButton("Retry", null).create().show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                };


                LoginRequest loginRequest = new LoginRequest(username, password, responseListener);
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                queue.add(loginRequest);


            }
        });

        DisplayMetrics dMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dMetrics);


        //Full screen
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        screenWidth = dMetrics.widthPixels;
        screenHeight = dMetrics.heightPixels;

        //Full screen the window
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Set landscape
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        //Set vibration
        collisionShake();

        //Set the default map
        mapFlag = false;


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void toWelcomeView() {
        if (welcomeView == null) {
            welcomeView = new WelcomeView(this);
        }
        this.setContentView(welcomeView);
        currentView = WhichView.WELCOME_VIEW;
    }

    private void toGameOverView() {
        //if (gameOverView == null) {
        gameOverView = new GameOverView(this, modeFlag, winnerFlag);
        //}
        this.setContentView(gameOverView);
    }

    private void toSingleGameView() {
        singleGameView = new SingleGameView(this);
        this.setContentView(singleGameView);
        currentView = WhichView.SINGLE_GAME_VIEW;

    }

    private void toMultiGameView() {
        multiGameView = new MultiGameView(this);
        this.setContentView(multiGameView);
        currentView = WhichView.MULTI_GAME_VIEW;

    }

    private void toMainMenuView() {
        mainMenuView = new MenuView(this);
        this.setContentView(mainMenuView);
        currentView = WhichView.MENU_VIEW;
    }

    private void toSettingView() {
        settingView = new SettingView(this);
        this.setContentView(settingView);
        currentView = WhichView.SETTING_VIEW;
    }

    //Send message to handler
    public void sendMessage(int what) {
        Message msg = myHandler.obtainMessage(what);
        myHandler.sendMessage(msg);
    }

    //Whether or not the background music is playing
    public boolean isBackgroundMusicOn() {
        return backgroundMusicOn;
    }

    //Start to play the background music
    public void setBackgroundMusicOn(boolean backgroundMusicOn) {
        this.backgroundMusicOn = backgroundMusicOn;
    }

    public boolean isSoundOn() {
        return soundOn;
    }

    public void setSoundOn(boolean soundOn) {
        this.soundOn = soundOn;
    }


    public void collisionShake() {
        vibrator = (Vibrator) getApplication().getSystemService
                (Service.VIBRATOR_SERVICE);
    }

    public void shake() {
        if (vibrationFlag) {
            vibrator.vibrate(new long[]{0, 80}, -1);
        }
    }

    //The function of the return key
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent e) {
        if (keyCode == 4) {
            if (currentView == WhichView.SINGLE_GAME_VIEW || currentView == WhichView.SETTING_VIEW || returnFlag ==true ) {
                toMainMenuView();
                return true;
            }
        }
        return false;
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}



