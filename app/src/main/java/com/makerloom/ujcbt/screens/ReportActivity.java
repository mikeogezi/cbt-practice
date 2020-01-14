package com.makerloom.ujcbt.screens;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.makerloom.common.activity.MyBackToolbarActivity;
import com.makerloom.common.utils.Constants;
import com.makerloom.common.utils.Keys;
import com.makerloom.ujcbt.R;
import com.makerloom.ujcbt.events.MessageEvent;
import com.makerloom.ujcbt.events.QuestionsUpdateEvent;
import com.makerloom.ujcbt.utils.Commons;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Locale;

import mehdi.sakout.fancybuttons.FancyButton;

// Removing Ads
//import com.google.android.gms.ads.AdListener;
//import com.google.android.gms.ads.AdLoader;
//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;
//import com.google.android.gms.ads.InterstitialAd;
//import com.google.android.gms.ads.MobileAds;
//import com.google.android.gms.ads.reward.RewardedVideoAd;

/**
 * Created by michael on 4/11/18.
 */

public class ReportActivity extends MyBackToolbarActivity {
    private FancyButton seeDeptsBtn, seeCoursesBtn, newTestBtn, quitBtn, correctionBtn;

    private RelativeLayout adLayout;

    private TextView adInfo;

    // Removing Ads
//    private AdView adView;
//
//    private InterstitialAd interstitialAd;
//
//    private RewardedVideoAd rewardedVideoAd;
//
//    private AdLoader nativeAdLoader;

    private String getDeptName () {
        if (getIntent().hasExtra(Keys.DEPARTMENT_NAME_KEY)) {
            return getIntent().getStringExtra(Keys.DEPARTMENT_NAME_KEY);
        }
        else {
            return Constants.DEFAULT_DEPARTMENT_NAME;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (event instanceof QuestionsUpdateEvent) { }
    }

    private String getCourseName () {
        if (getIntent().hasExtra(Keys.COURSE_NAME_KEY)) {
            return getIntent().getStringExtra(Keys.COURSE_NAME_KEY);
        }
        else {
            return Constants.DEFAULT_COURSE_NAME;
        }
    }

    private String getCorrectionQuestionsJSON () {
        if (getIntent().hasExtra(Keys.CORRECTION_QUESTIONS_JSON_KEY)) {
            return getIntent().getStringExtra(Keys.CORRECTION_QUESTIONS_JSON_KEY);
        }
        else {
            return "[]";
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Commons.goToWelcomeIfNotSignedIn(this);
        setContentView(R.layout.activity_report);

        EventBus.getDefault().register(this);

        String shortDeptName = "";
        try {
            shortDeptName = getCourseName().split(" ")[0];
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        setTitle(String.format(Locale.ENGLISH, "%s Exam Report", getCourseName()));

        Integer correctAnswers, questionsAnswered, totalQuestions;
        Double score;
        Character grade;

        runReportIntro();

        correctAnswers = getIntent().getIntExtra(Keys.CORRECT_ANSWERS_KEY, 0);
        questionsAnswered = getIntent().getIntExtra(Keys.QUESTIONS_ANSWERED_KEY, 0);
        totalQuestions = getIntent().getIntExtra(Keys.TOTAL_QUESTIONS_KEY, 0);
        score = getIntent().getDoubleExtra(Keys.SCORE_KEY, 0.0);
        grade = getIntent().getCharExtra(Keys.GRADE_KEY, '\0');

        TextView correctAnswersTV, questionsAnsweredTV, totalQuestionsTV, scoreTV, gradeTV, summaryTV;

        correctAnswersTV = findViewById(R.id.correct_answers);
        questionsAnsweredTV = findViewById(R.id.questions_answered);
        totalQuestionsTV = findViewById(R.id.total_questions);
        scoreTV = findViewById(R.id.score);
        gradeTV = findViewById(R.id.grade);
        summaryTV = findViewById(R.id.summary);
        summaryTV.setText(String.format(Locale.ENGLISH,
                "You have successfully completed the %s Exam. See your report below.", getCourseName()));

        correctAnswersTV.setText(String.valueOf(correctAnswers));
        questionsAnsweredTV.setText(String.valueOf(questionsAnswered));
        totalQuestionsTV.setText(String.valueOf(totalQuestions));
        scoreTV.setText(String.format(Locale.ENGLISH, "%.2f%%", score));
        gradeTV.setText(String.valueOf(grade));

        seeDeptsBtn = findViewById(R.id.see_departments);
        seeDeptsBtn.setOnClickListener(onSeeDeptsClick);

        seeCoursesBtn = findViewById(R.id.see_courses);
        seeCoursesBtn.setText(String.format(Locale.ENGLISH, "%s Courses", shortDeptName));
        seeCoursesBtn.setOnClickListener(onSeeCoursesClick);

        newTestBtn = findViewById(R.id.new_test);
        newTestBtn.setOnClickListener(onNewTestClick);

        quitBtn = findViewById(R.id.quit);
        quitBtn.setOnClickListener(onQuitClick);

        correctionBtn = findViewById(R.id.correction);
        correctionBtn.setOnClickListener(onCorrectionClick);
    }

    View.OnClickListener onSeeDeptsClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent depts = new Intent(ReportActivity.this, DepartmentsActivity.class);
            depts.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(depts);
            finish();
        }
    };

    View.OnClickListener onSeeCoursesClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent courses = new Intent(ReportActivity.this, CoursesActivity.class);
            courses.putExtra(Keys.DEPARTMENT_NAME_KEY, getDeptName());
            startActivity(courses);
            finish();
        }
    };

    private boolean isCorrection = false;
    View.OnClickListener onCorrectionClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            isCorrection = true;
//            isNewTest = false;
//            isQuit = false;
//            showInterstitialIfPossible();
            doCorrection();
        }
    };

    private void doCorrection() {
        Intent correct = new Intent(ReportActivity.this, CorrectionActivity.class);
        correct.putExtra(Keys.CORRECTION_QUESTIONS_JSON_KEY, getCorrectionQuestionsJSON());
        correct.putExtra(Keys.COURSE_NAME_KEY, getCourseName());
        startActivity(correct);
    }

    private boolean isNewTest = false;
    View.OnClickListener onNewTestClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            isNewTest = true;
//            isCorrection = false;
//            isQuit = false;
//            showInterstitialIfPossible();
            doNewTest();
        }
    };

    private void doNewTest () {
        Intent newTest = new Intent(ReportActivity.this, TestActivity.class);
        newTest.putExtra(Keys.DEPARTMENT_NAME_KEY, getDeptName());
        newTest.putExtra(Keys.COURSE_NAME_KEY, getCourseName());
        startActivity(newTest);
        finish();
    }

    private boolean isQuit = false;
    View.OnClickListener onQuitClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Bad practice to show interstital ad when quitting
//            isQuit = true;
//            isCorrection = false;
//            isNewTest = false;
//            showInterstitialIfPossible();
            quit();
        }
    };

    private void quit () {
        if (Build.VERSION.SDK_INT >= 16) {
            finishAffinity();
        }
        else {
            finish();
            moveTaskToBack(true);
        }
    }

    private void afterInterstitial () {
        if (isQuit) {
            quit();
        }
        else if (isNewTest) {
            doNewTest();
        }
        else if (isCorrection) {
            doCorrection();
        }
    }

    // Removing Ads
//    private void showInterstitialIfPossible () {
//        if (interstitialFailedToLoad) {
//            afterInterstitial();
//        }
//        else if (null != interstitialAd && interstitialAd.isLoaded()) {
//            interstitialAd.show();
//        }
//        else {
//            afterInterstitial();
//        }
//    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (willRemoveInterstitial && null != interstitialAd) {
//            afterInterstitial();
//            interstitialAd.getAdListener().onAdClosed();
//            willRemoveInterstitial = false;
////            ReportActivity.this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,
////                    KeyEvent.KEYCODE_BACK));
////            new Handler().postDelayed(new Runnable() {
////                @Override
////                public void run() {
////                    willRemoveInterstitial = false;
////                }
////            }, 5000);
//        }
    }

    public void setNotFirstReport () {
        SharedPreferences.Editor editor = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext()).edit();
        editor.putBoolean(Keys.IS_FIRST_REPORT_KEY, false);
        editor.apply();
    }

    private void runReportIntro() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                SharedPreferences preferences = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());

                isFirstReport = preferences.getBoolean(Keys.IS_FIRST_REPORT_KEY, true);
                // isFirstTest = true;

                if (isFirstReport || Constants.DAILOG_DEBUG) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ReportActivity.this)
                                    .setCancelable(true)
                                    .setTitle("Exam Report")
                                    .setMessage("The exam report gives you an overview of how you performed in the exam. Use it to gain deeper insights into your exam performance.")
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
                            setNotFirstReport();
                        }
                    });
                }
            }
        });
    }

    private boolean isFirstReport = false;

    private boolean willRemoveInterstitial = false;

    private void removeInterstitialOnReturn () {
        willRemoveInterstitial = true;
    }

    private boolean interstitialFailedToLoad = false;
    // Removing Ads
//    AdListener interstitialAdListener = new AdListener() {
//        @Override
//        public void onAdClosed() {
//            super.onAdClosed();
//            if (Constants.VERBOSE) {
//                Toast.makeText(ReportActivity.this, "Interstitial Ad closed", Toast.LENGTH_SHORT)
//                        .show();
//            }
//            afterInterstitial();
//        }
//
//        @Override
//        public void onAdLoaded() {
//            super.onAdLoaded();
//            if (Constants.VERBOSE) {
//            Toast.makeText(ReportActivity.this, "Interstitial Ad loaded", Toast.LENGTH_SHORT)
//                    .show();
//            }
//        }
//
//        @Override
//        public void onAdFailedToLoad(int i) {
//            super.onAdFailedToLoad(i);
//            if (Constants.VERBOSE) {
//                Toast.makeText(ReportActivity.this, "Interstitial Ad failed to load", Toast.LENGTH_SHORT)
//                        .show();
//            }
//            interstitialFailedToLoad = true;
//        }
//
//        @Override
//        public void onAdLeftApplication() {
//            super.onAdLeftApplication();
//            if (Constants.VERBOSE) {
//                Toast.makeText(ReportActivity.this, "Interstitial Ad left application", Toast.LENGTH_SHORT)
//                        .show();
//            }
////            removeInterstitialOnReturn();
//        }
//
//        @Override
//        public void onAdOpened() {
//            super.onAdOpened();
//            if (Constants.VERBOSE) {
//                Toast.makeText(ReportActivity.this, "Interstitial Ad opened", Toast.LENGTH_SHORT)
//                        .show();
//            }
//        }
//
//        @Override
//        public void onAdImpression() {
//            super.onAdImpression();
//            if (Constants.VERBOSE) {
//                Toast.makeText(ReportActivity.this, "Interstitial Ad impression", Toast.LENGTH_SHORT)
//                        .show();
//            }
//        }
//    };

    @Override
    public void onBackPressed() {
        if (willRemoveInterstitial) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }
}
