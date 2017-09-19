package com.example.qifeng.td.LoginRelated;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.qifeng.td.MainActivity;
import com.example.qifeng.td.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FriendList extends AppCompatActivity {

    MainActivity activity = MainActivity.getInstance();

    private static final String FRIEND_REQUEST_URL = "http://proj-309-ss-02.cs.iastate.edu/myFriend.php";

    BaseAdapter adapter = null;
    List<String> list = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);


        final Button bAddFriend = (Button) findViewById(R.id.bAddFriend);
        final EditText etFriendUN = (EditText) findViewById(R.id.etFriendUN);
        final TextView userTest = (TextView) findViewById(R.id.tv_userSelf);
        final Button bViewFriends = (Button) findViewById(R.id.bViewFriends);
        final Button bStart = (Button) findViewById(R.id.bstart);


        Intent intent = getIntent();
        final String userSelf = intent.getStringExtra("username");

        userTest.setText("Hi " + userSelf + " Please Input Friend's UserName!");


        bAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = etFriendUN.getText().toString();

                Response.Listener<String> responseListenser = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonResponse = new JSONObject(response);

                            boolean success = jsonResponse.getBoolean("success");

                            if (success) {
                                Toast.makeText(FriendList.this, "Successfully Add Friend", Toast.LENGTH_LONG).show();

                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(FriendList.this);
                                builder.setMessage("Add Friend failed").setNegativeButton("Retry", null).create().show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                };

                FriendListRequest registerRequest = new FriendListRequest(userSelf, username, responseListenser);
                RequestQueue queue = Volley.newRequestQueue(FriendList.this);
                queue.add(registerRequest);

            }

        });


        ListView listView = (ListView) findViewById(R.id.listview);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);


        bViewFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                final String username = etFriendUN.getText().toString();

                final Response.Listener<String> responseListenser = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
//                            JSONObject jsonResponse = new JSONObject(response);

                            Log.e("debug", response);
                            JSONArray array = new JSONArray(response);
                            list.clear();
                            for (int i = 0; i < array.length(); i++) {
                                list.add((String) array.get(i));
                            }

                            adapter.notifyDataSetChanged();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                };

                GetFriendListRequest registerRequest = new GetFriendListRequest(userSelf, responseListenser);
                RequestQueue queue = Volley.newRequestQueue(FriendList.this);
                queue.add(registerRequest);

            }

        });

        bStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent multiIntent = new Intent(FriendList.this, MainScreenActivity.class);

               FriendList.this.startActivity(multiIntent);
            }
        });

    }


}

