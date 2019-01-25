package com.makerloom.common.startup;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.makerloom.common.activity.MyAppCompatActivity;
import com.makerloom.ujcbt.screens.AuthActivity;
import com.makerloom.ujcbt.screens.CheckPINValidityActivity;
import com.makerloom.ujcbt.screens.DepartmentsActivity;
import com.makerloom.ujcbt.screens.WelcomeActivity;

public class MainActivity extends MyAppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // Not signed in
        if (null == user) {
            startActivity(new Intent(MainActivity.this, WelcomeActivity.class));
        }
        // User is signed in
        else {
            startActivity(new Intent(MainActivity.this, CheckPINValidityActivity.class));
        }

        finish();
    }

}
