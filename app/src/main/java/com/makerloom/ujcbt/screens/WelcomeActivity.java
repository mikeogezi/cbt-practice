package com.makerloom.ujcbt.screens;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.makerloom.common.activity.MyAppCompatActivity;
import com.makerloom.common.activity.MyPlainToolbarActivity;
import com.makerloom.ujcbt.R;
import com.makerloom.ujcbt.utils.Commons;

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
//        mottoTV.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/Aller/Aller_Rg.ttf"));

        signInBtn = findViewById(R.id.signin_btn);
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Commons.hasConnection(WelcomeActivity.this)) {
                    startActivity(new Intent(WelcomeActivity.this, AuthActivity.class));
//                    finish();
                }
                else {
                    showConnectionErrorMessage();
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

    public void showConnectionErrorMessage () {
        AlertDialog.Builder builder = new AlertDialog.Builder(WelcomeActivity.this)
                .setCancelable(true)
                .setTitle("Internet Connection Error")
                .setMessage("You need an internet connection to sign in. Please check that you're online then try again.")
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
