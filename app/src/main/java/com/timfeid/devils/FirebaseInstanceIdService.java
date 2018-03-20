package com.timfeid.devils;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

/**
 * Created by Tim on 2/9/2018.
 * TODO implement this class
 */

public class FirebaseInstanceIdService extends com.google.firebase.iid.FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        String recentToken = FirebaseInstanceId.getInstance().getToken();
    }
}
