package com.example.qifeng.td.LoginRelated;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by luxuan on 11/25/16.
 */

public class FriendListRequest extends StringRequest {


    private static final String FRIEND_REQUEST_URL = "http://proj-309-ss-02.cs.iastate.edu/myFriend.php";


    private Map<String, String> params;

    public FriendListRequest(String usernameSelf, String usernameOthers, Response.Listener<String> listener)
    {
        super(Method.POST, FRIEND_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("username", usernameSelf);
        params.put("target", usernameOthers);

    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
