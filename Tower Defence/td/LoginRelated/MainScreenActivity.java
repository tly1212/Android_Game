package com.example.qifeng.td.LoginRelated;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.qifeng.td.MainActivity;
import com.example.qifeng.td.R;
import com.example.qifeng.td.GameRelated.WelcomeView;


public class MainScreenActivity extends AppCompatActivity {
    WelcomeView welcomeView;
    MainActivity activity = MainActivity.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);


        final TextView tvMyInfo = (TextView) findViewById(R.id.tvMyInfo);
        final Button bSingle = (Button) findViewById(R.id.bSingle);
        final TextView tvWelcome = (TextView) findViewById(R.id.tvWelcomeMsgMS);


        Intent intent = getIntent();
        final String name = intent.getStringExtra("name");
        final String username = intent.getStringExtra("username");
        final String email = intent.getStringExtra("email");
        final int coin = intent.getIntExtra("coin", -1);
        final int level = intent.getIntExtra("level", -1);
        final int crystal = intent.getIntExtra("crystal", -1);
        final int experience = intent.getIntExtra("experience", -1);


        String message = "Hi " + name + ", Welcome to Our Game!";
        tvWelcome.setText(message);

        activity.returnFlag = true;

        tvMyInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent myInfoIntent = new Intent(MainScreenActivity.this, UserAreaActivity.class);
                myInfoIntent.putExtra("Uname", name);
                myInfoIntent.putExtra("Uusername", username);
                myInfoIntent.putExtra("Uemail", email);
                myInfoIntent.putExtra("Ucoin", coin);
                myInfoIntent.putExtra("Ulevel", level);
                myInfoIntent.putExtra("Ucrystal", crystal);
                myInfoIntent.putExtra("Uexperience", experience);


                MainScreenActivity.this.startActivity(myInfoIntent);

            }
        });

        bSingle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.toWelcomeView();
            }
        });


//        bMulti.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent multiIntent = new Intent(MainScreenActivity.this, MultiPlayer.class);
//                MainScreenActivity.this.startActivity(multiIntent);
//            }
//        });


    }
}
