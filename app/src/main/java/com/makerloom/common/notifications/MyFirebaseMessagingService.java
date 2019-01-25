package com.makerloom.common.notifications;

import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.makerloom.common.utils.UI;

import java.util.Locale;

/**
 * Created by michael on 4/24/18.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private final static String TAG = MyFirebaseMessagingService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG, String.format(Locale.ENGLISH, "From: %s", remoteMessage.getFrom()));

        if (!remoteMessage.getData().isEmpty()) {
            Log.d(TAG, String.format(Locale.ENGLISH, "Message Data Payload: %s", remoteMessage.getData()));
        }

        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, String.format(Locale.ENGLISH, "Message Notification Body: %s", remoteMessage.getNotification().getBody()));
            sendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        }
        else {
            sendNotification(null, null);
        }
    }

    private void sendNotification (@Nullable String title, @Nullable String messageBody) {
        UI.sendNotification(MyFirebaseMessagingService.this, title, messageBody);
    }
}
