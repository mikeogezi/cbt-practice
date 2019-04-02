package com.makerloom.common.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ProgressBar;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.makerloom.common.startup.IntroActivity;
import com.makerloom.common.startup.MainActivity;
import com.makerloom.ujcbt.R;

/**
 * Created by michael on 2/25/18.
 */

public class MyAppCompatActivity extends AppCompatActivity {
    private FirebaseAnalytics firebaseAnalytics;

    protected Toolbar toolbar;

    public boolean isSpotlightShowing = false;

    public FirebaseAnalytics getFirebaseAnalytics () {
        return firebaseAnalytics;
    }

    public void setupToolbar () {
        toolbar = (Toolbar) this.findViewById(R.id.toolbar);

        if (null == toolbar) {
            return;
        }

        try {
            toolbar.setTitleTextAppearance(this, R.style.ToolbarTitle);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        if (null != toolbar) {
            this.setSupportActionBar(toolbar);

            ActionBar actionBar = this.getSupportActionBar();
            if (null != actionBar) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeButtonEnabled(true);
            }
        }
    }

    public void restartActivity () {
        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        startActivity(intent);
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAnalytics = FirebaseAnalytics.getInstance(MyAppCompatActivity.this);
    }

    public void goToMain () {
        Intent main = new Intent(MyAppCompatActivity.this, MainActivity.class);
        startActivity(main);
        finish();
    }

    public void goToIntro () {
        Intent intro = new Intent(MyAppCompatActivity.this, IntroActivity.class);
        startActivity(intro);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    protected void setProgressBar (ProgressBar progressBar) {

    }

    protected void showProgress () {
        // TODO
    }

    protected void hideProgress () {
        // TODO
    }
}
