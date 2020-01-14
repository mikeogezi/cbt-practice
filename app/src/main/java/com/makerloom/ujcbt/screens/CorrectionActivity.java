package com.makerloom.ujcbt.screens;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.makerloom.common.activity.MyBackToolbarActivity;
import com.makerloom.common.utils.Constants;
import com.makerloom.common.utils.Keys;
import com.makerloom.ujcbt.R;
import com.makerloom.ujcbt.adapters.QuestionAdapter;
import com.makerloom.ujcbt.events.MessageEvent;
import com.makerloom.ujcbt.events.QuestionsUpdateEvent;
import com.makerloom.ujcbt.models.Question;
import com.makerloom.ujcbt.utils.Commons;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Locale;

// Removing Ads
//import com.google.android.gms.ads.AdView;

/**
 * Created by michael on 4/11/18.
 */

public class CorrectionActivity extends MyBackToolbarActivity {
    private RecyclerView correctionRV;

    private RelativeLayout adLayout;

    private TextView adInfo;

    // Removing Ads
//    private AdView adView;

    private String getCorrectionQuestionsJSON () {
        if (getIntent().hasExtra(Keys.CORRECTION_QUESTIONS_JSON_KEY)) {
            return getIntent().getStringExtra(Keys.CORRECTION_QUESTIONS_JSON_KEY);
        }
        else {
            return "[]";
        }
    }

    private String getCourseName () {
        if (getIntent().hasExtra(Keys.COURSE_NAME_KEY)) {
            return getIntent().getStringExtra(Keys.COURSE_NAME_KEY);
        }
        else {
            return Constants.DEFAULT_COURSE_NAME;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (event instanceof QuestionsUpdateEvent) { }
    }

    private String TAG = CorrectionActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Commons.goToWelcomeIfNotSignedIn(this);
        setContentView(R.layout.activity_correction);

        EventBus.getDefault().register(this);

        setTitle(String.format(Locale.ENGLISH, "%s Correction", getCourseName()));

        correctionRV = findViewById(R.id.correction_r_v);

        LinearLayoutManager manager = new LinearLayoutManager(CorrectionActivity.this);

        Gson gson = new Gson();
        Type listType = new TypeToken<List<Question>>() {}.getType();
        List<Question> questions = gson.fromJson(getCorrectionQuestionsJSON(), listType);

        Log.d(TAG, "Questions: " + getCorrectionQuestionsJSON());

        QuestionAdapter adapter = new QuestionAdapter(CorrectionActivity.this, questions);

        correctionRV.setHasFixedSize(true);
        correctionRV.setItemViewCacheSize(15);
        correctionRV.setDrawingCacheEnabled(true);
        correctionRV.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        correctionRV.setAdapter(adapter);
        correctionRV.setLayoutManager(manager);

        // Removing Ads
//        adLayout = findViewById(R.id.ad_layout);
//        adInfo = findViewById(R.id.ad_info);
//        adView = findViewById(R.id.ad_view);
//        UI.loadFooterBannerAd(CorrectionActivity.this, adLayout, adView, adInfo, false);

        runCorrectionIntro();
    }

    public void setNotFirstCorrection() {
        SharedPreferences.Editor editor = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext()).edit();
        editor.putBoolean(Keys.IS_FIRST_CORRECTION_KEY, false);
        editor.apply();
    }

    public void runCorrectionIntro() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                SharedPreferences preferences = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());

                isFirstCorrection = preferences.getBoolean(Keys.IS_FIRST_CORRECTION_KEY, true);
                // isFirstTest = true;

                if (isFirstCorrection || Constants.DAILOG_DEBUG) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog.Builder builder = new AlertDialog.Builder(CorrectionActivity.this)
                                    .setCancelable(true)
                                    .setTitle("Exam Correction")
                                    .setMessage("The exam correction tells you the questions you answered correctly and the ones you didn't. The correct answers are ticked in green while wrong answers are ticked in red.")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    });

                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            setNotFirstCorrection();
                        }
                    });
                }
            }
        });
    }

    private boolean isFirstCorrection = false;

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }
}
