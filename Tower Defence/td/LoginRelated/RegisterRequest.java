package com.example.qifeng.td.LoginRelated;


import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by luxuan on 10/31/16.
 */

public class RegisterRequest extends StringRequest {

//    private static final String REGISTER_REQUEST_URL = "10.25.70.99/registerPractise.php";

    private static final String REGISTER_REQUEST_URL = "http://proj-309-ss-02.cs.iastate.edu/registerLoginPractise/registerPractise.php";
//    private static final String REGISTER_REQUEST_URL = "http://proj-309-ss-02.cs.iastate.edu/registerPractise.php";


    private Map<String, String> params;

    public RegisterRequest(String name, String username, String email, String password, Response.Listener<String> listener)
    {
        super(Method.POST, REGISTER_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("name", name);
        params.put("username", username);
        params.put("password", password);
        params.put("email", email);

    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
