package com.makerloom.ujcbt.screens;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import android.view.View;
import android.widget.TextView;

import com.makerloom.common.activity.MyBackToolbarActivity;
import com.makerloom.ujcbt.R;

import in.championswimmer.libsocialbuttons.BtnSocial;
import mehdi.sakout.fancybuttons.FancyButton;

public class InfoActivity extends MyBackToolbarActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        personOneTV = findViewById(R.id.person_1);
//        setTypeface(personOneTV);
        personTwoTV = findViewById(R.id.person_2);
//        setTypeface(personTwoTV);
        personThreeTV = findViewById(R.id.person_3);
//        setTypeface(personThreeTV);

        // Ogezi
        callPersonOneBtn = findViewById(R.id.call_person_1);
        callPersonOneBtn.setOnClickListener(dialNumberOnClick("+2349034099658"));
        // Biodun
        callPersonTwoBtn = findViewById(R.id.call_person_2);
        callPersonTwoBtn.setOnClickListener(dialNumberOnClick("+2348064874715"));
        // Shekinah
        callPersonThreeBtn = findViewById(R.id.call_person_3);
        callPersonThreeBtn.setOnClickListener(dialNumberOnClick("+2348068533781"));

        joinGroupBtn = findViewById(R.id.join_group_btn);
        joinGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(getString(R.string.whatsapp_group_link)));
                startActivity(intent);
            }
        });

        whatsappCV = findViewById(R.id.whatsapp_cv);
        whatsappCV.setPreventCornerOverlap(true);

        checkPlayStore = findViewById(R.id.check_play_store);
        checkPlayStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(getString(R.string.playstore_link)));
                startActivity(intent);
            }
        });
    }

    private View.OnClickListener dialNumberOnClick (String phoneNumber) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent call = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
                    startActivity(call);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    BtnSocial joinGroupBtn;

    FancyButton callPersonOneBtn;
    FancyButton callPersonTwoBtn;
    FancyButton callPersonThreeBtn;

    FancyButton checkPlayStore;

    CardView whatsappCV;

    TextView personOneTV;
    TextView personTwoTV;
    TextView personThreeTV;
}
