package com.example.qifeng.td.LoginRelated;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by luxuan on 11/26/16.
 */

public class GetFriendListRequest extends StringRequest {

    private static final String GET_FRIEND_REQUEST_URL = "http://proj-309-ss-02.cs.iastate.edu/getFriendList.php";


    private Map<String, String> params;

    public GetFriendListRequest (String friendUserName, Response.Listener<String> listener)
    {
        super(Method.POST, GET_FRIEND_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("username", friendUserName);
//        params.put("target", usernameOthers);

    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }


}
