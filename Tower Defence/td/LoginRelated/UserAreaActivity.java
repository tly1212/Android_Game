package com.example.qifeng.td.LoginRelated;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.qifeng.td.R;

public class UserAreaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_area);

        final TextView etUsername = (TextView) findViewById(R.id.etUsername);
        final TextView etEmail = (TextView) findViewById(R.id.etEmail);
        final TextView welcomeMessage = (TextView)findViewById(R.id.tvWelcomeMsg);
        final TextView etCoin = (TextView) findViewById(R.id.etCoin);
        final Button bViewFriend = (Button) findViewById(R.id.bViewFriend);

        final TextView etLevel = (TextView) findViewById(R.id.etLevel);
        final TextView etCrystal = (TextView) findViewById(R.id.etCrystal);
        final TextView etExperience = (TextView) findViewById(R.id.etExperience);



        Intent intent = getIntent();
        String name = intent.getStringExtra("Uname");
        final String username = intent.getStringExtra("Uusername");
        String email = intent.getStringExtra("Uemail");
        int coin = intent.getIntExtra("Ucoin", -1);
        int level = intent.getIntExtra("Ulevel", -1);
        int crystal = intent.getIntExtra("Ucrystal", -1);
        int experience = intent.getIntExtra("Uexperience", -1);



        String message = "Hi " + name + " Have Fun with Our Game!";
        welcomeMessage.setText(message);
        etUsername.setText(username);
        etEmail.setText(email);
        etCoin.setText(coin + "");
        etLevel.setText(level + "");
        etCrystal.setText(crystal + "");
        etExperience.setText(experience + "");

        bViewFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent vfIntent = new Intent(UserAreaActivity.this, FriendList.class);
                vfIntent.putExtra("username", username);

                UserAreaActivity.this.startActivity(vfIntent);

            }
        });




    }
}

