package com.makerloom.ujcbt.screens;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.makerloom.common.activity.MyPlainToolbarActivity;
import com.makerloom.ujcbt.R;
import com.makerloom.ujcbt.utils.Commons;
import com.makerloom.ujcbt.utils.NetworkUtils;

import mehdi.sakout.fancybuttons.FancyButton;

public class WelcomeActivity extends MyPlainToolbarActivity {
    private TextView mottoTV;

    private FancyButton signInBtn;

    private FancyButton getInfoBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        mottoTV = findViewById(R.id.motto);

        signInBtn = findViewById(R.id.signin_btn);
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Commons.hasConnection(WelcomeActivity.this)) {
                    startActivity(new Intent(WelcomeActivity.this, AuthActivity.class));
                }
                else {
                    NetworkUtils.Companion.showConnectionErrorMessage(WelcomeActivity.this, "sign in");
                }
            }
        });

        getInfoBtn = findViewById(R.id.info_btn);
        getInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelcomeActivity.this, InfoActivity.class));
            }
        });
    }


}
