package com.example.qifeng.td.LoginRelated;


import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by luxuan on 11/1/16.
 */


public class LoginRequest extends StringRequest {

//    private static final String REGISTER_REQUEST_URL = "10.25.70.99/registerPractise.php";
private static final String LOGIN_REQUEST_URL = "http://proj-309-ss-02.cs.iastate.edu/registerLoginPractise/loginPractise.php";



    private Map<String, String> params;

    public LoginRequest(String username,String password, Response.Listener<String> listener)
    {


        super(Method.POST, LOGIN_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);


    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}

