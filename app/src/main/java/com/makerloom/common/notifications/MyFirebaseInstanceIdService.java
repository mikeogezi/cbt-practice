package com.makerloom.common.notifications;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.Locale;

/**
 * Created by michael on 4/24/18.
 */

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {
    private final static String TAG = MyFirebaseInstanceIdService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, String.format(Locale.ENGLISH, "Refreshed Token: %s", token));

        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer (String token) {}
}
