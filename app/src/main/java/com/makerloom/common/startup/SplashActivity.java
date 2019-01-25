package com.makerloom.common.startup;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.makerloom.common.activity.MyAppCompatActivity;
import com.makerloom.common.utils.Constants;
import com.makerloom.common.utils.Keys;

/**
 * Created by michael on 4/11/18.
 */

public class SplashActivity extends MyAppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());

        Boolean isFirstStart = preferences.getBoolean(Keys.IS_FIRST_START_KEY, true);
        // isFirstStart = true;

        if (isFirstStart || Constants.ONBOARDING_DEBUG) {
            goToIntro();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            setNotFirstStart();
                        }
                    });
                }
            }, 5000);
        }
        else {
            goToMain();
        }
    }

    public void setNotFirstStart () {
        SharedPreferences.Editor editor = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext()).edit();
        editor.putBoolean(Keys.IS_FIRST_START_KEY, false);
        editor.apply();
    }
}
