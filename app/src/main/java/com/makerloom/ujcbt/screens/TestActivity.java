package com.makerloom.ujcbt.screens;

import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.franmontiel.fullscreendialog.FullScreenDialogFragment;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.makerloom.common.activity.MyBackToolbarActivity;
import com.makerloom.common.utils.Constants;
import com.makerloom.common.utils.Keys;
import com.makerloom.common.utils.UI;
import com.makerloom.ujcbt.R;
import com.makerloom.ujcbt.adapters.OptionAdapter;
import com.makerloom.ujcbt.adapters.QuestionNumberAdapter;
import com.makerloom.ujcbt.events.MessageEvent;
import com.makerloom.ujcbt.events.QuestionsUpdateEvent;
import com.makerloom.ujcbt.models.Course;
import com.makerloom.ujcbt.models.Question;
import com.makerloom.ujcbt.models.Test;
import com.makerloom.ujcbt.utils.Commons;
import com.makerloom.ujcbt.utils.TextViewUtils;
import com.takusemba.spotlight.CustomTarget;
import com.takusemba.spotlight.OnSpotlightEndedListener;
import com.takusemba.spotlight.OnSpotlightStartedListener;
import com.takusemba.spotlight.Spotlight;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import mehdi.sakout.fancybuttons.FancyButton;

// Removing Ads
//import com.google.android.gms.ads.AdListener;
//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;
//import com.google.android.gms.ads.InterstitialAd;
//import com.google.android.gms.ads.MobileAds;

/**
 * Created by michael on 4/11/18.
 */

public class TestActivity extends MyBackToolbarActivity implements
        PassageFragment.OnFragmentInteractionListener,
        FullScreenDialogFragment.OnConfirmListener,
        FullScreenDialogFragment.OnDiscardListener,
        FullScreenDialogFragment.OnDiscardFromExtraActionListener {

    @Override
    public void onConfirm(@Nullable Bundle result) {
        Log.d(TAG, "onConfirm");
    }

    @Override
    public void onDiscard() {
        Log.d(TAG, "onDiscard");
    }

    @Override
    public void onDiscardFromExtraAction(int actionId, @Nullable Bundle result) {
        Log.d(TAG, "onDiscardFromExtraAction");
    }

    @Override
    public void onFragmentInteraction(@NotNull Uri uri) {
        Log.d(TAG, "onFragmentInteraction");
    }

    private RecyclerView questionNumberRV;

    // Removing Ads
//    private InterstitialAd interstitialAd;

    private TextView questionTV;

    private RecyclerView optionRV;

    private FancyButton showPassageBtn;

    public List<Question> questions;

    public List<FancyButton> numberBtns;

    public int lastNumberBtnIndex = 0;

    private FancyButton nextBtn, previousBtn, finishBtn;

    private TextView totalQuestionsTV, currentQuestionNumberTV;

    private final String TAG = TestActivity.class.getSimpleName();

    private String getDeptName () {
        if (getIntent().hasExtra(Keys.DEPARTMENT_NAME_KEY)) {
            return getIntent().getStringExtra(Keys.DEPARTMENT_NAME_KEY);
        }
        else {
            return Constants.DEFAULT_DEPARTMENT_NAME;
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

    final static public int timeInMinutes = 15;

    final static public int numberOfQuestions = 25;

    final static private float almostDoneThresh = 0.2f;

    // Removing Ads
//    private View adLayout;
//
//    private TextView adInfo;
//
//    private AdView adView;

    private NestedScrollView scrollView;

    private long scrollMillis = 500;
    private void focusBottom () {
        if (null != scrollView) {
            ObjectAnimator.ofInt(scrollView, "scrollY", scrollView.getBottom())
                    .setDuration(scrollMillis).start();
            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    scrollView.fullScroll(View.FOCUS_DOWN);
                                }
                            });
                        }
                    }, scrollMillis);
                }
            });
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Commons.goToWelcomeIfNotSignedIn(this);
        setContentView(R.layout.activity_test);

        EventBus.getDefault().register(this);

        setTitle(String.format("%s Exam", getCourseName()));

        numberBtns = new ArrayList<>(questionsLen);

        scrollView = findViewById(R.id.scroll_view);

        totalQuestionsTV = findViewById(R.id.total_questions);

        currentQuestionNumberTV = findViewById(R.id.current_question_num);

        questionNumberRV = findViewById(R.id.number_recycler_view);

        previousBtn = findViewById(R.id.prev);
        previousBtn.setOnClickListener(onPrevious);
        nextBtn = findViewById(R.id.next);
        nextBtn.setOnClickListener(onNext);
        finishBtn = findViewById(R.id.finish);
        finishBtn.setOnClickListener(onFinish);
        finishBtn.getTextViewObject().setTypeface(Typeface.DEFAULT_BOLD);

        questionTV = findViewById(R.id.question);
        questionTV.setText("");

        optionRV = findViewById(R.id.option_rv);

        showPassageBtn = findViewById(R.id.show_passage_btn);
        showPassageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPassage();
            }
        });

        // Find out why this messes up onRestoreInstanceState
//        AsyncTask.execute(setupQuestionsTask);
        // In the mean time
        setupQuestionsTask.run();
    }


    final Runnable setupQuestionsTask = new Runnable() {
        @Override public void run() {
            Course course = new Course(getCourseName());
            Test test = Course.generateTest(course, TestActivity.this, numberOfQuestions);
            questions = test.getQuestions();
            Log.d(TAG, "Generate Questions");
            questionsLen = questions.size();
            minQuestionIndex = 0;
            maxQuestionIndex = questionsLen - 1;
            questionIndex = minQuestionIndex;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    totalQuestionsTV.setText(String.valueOf(questionsLen));
                    renderNumbers(questionsLen);
                    runWelcomeDialog();
                }
            });
        }
    };

//    private void afterInterstitial () {
//        submit();
//    }
//
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

    private String passageDialogTag = "passageDialog";

    private String passageText;

    void showPassage () {
        showPassage(passageText);
    }

    void showPassage (final String passageText) {
        Bundle args = new Bundle();

        args.putString(Commons.PASSAGE_KEY, passageText);
        boolean isInst = passageText.length() <= Commons.MAX_INSTRUCTION_LENGTH;

        FullScreenDialogFragment passageDialog = new FullScreenDialogFragment.Builder(this)
                .setTitle(isInst ? R.string.inst_dialog_title : R.string.passage_dialog_title)
                .setConfirmButton(R.string.passage_dialog_subtitle)
                .setFullScreen(true)
                .setContent(PassageFragment.class, args)
                .setOnConfirmListener(this)
                .setOnDiscardFromActionListener(this)
                .setOnDiscardListener(this)
                .build();

        passageDialog.show(getSupportFragmentManager(), passageDialogTag);
    }

    private void disablePrevBtn () {
        if (null != previousBtn && previousBtn.isEnabled()) {
            previousBtn.setEnabled(false);
        }
    }

    private void enablePrevBtn () {
        if (null != previousBtn && !previousBtn.isEnabled()) {
            previousBtn.setEnabled(true);
        }
    }

    private void showNextBtn () {
        if (null != nextBtn && null != finishBtn) {
            nextBtn.setVisibility(View.VISIBLE);
            finishBtn.setVisibility(View.GONE);
        }
    }

    private void showFinishBtn () {
        if (null != nextBtn && null != finishBtn) {
            nextBtn.setVisibility(View.GONE);
            finishBtn.setVisibility(View.VISIBLE);
        }
    }

    private void renderNumbers(int questionsLen) {
        FlexboxLayoutManager numberManager = new FlexboxLayoutManager(TestActivity.this);
        numberManager.setFlexWrap(FlexWrap.WRAP);
        numberManager.setAlignItems(AlignItems.CENTER);

        List<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= questionsLen; ++i) {
            numbers.add(i);
        }

        QuestionNumberAdapter adapter = new QuestionNumberAdapter(TestActivity.this, numbers);

        questionNumberRV.setAdapter(adapter);
        questionNumberRV.setLayoutManager(numberManager);
        ViewCompat.setNestedScrollingEnabled(questionNumberRV, false);
    }

    private int questionIndex;

    private int minQuestionIndex;

    private int maxQuestionIndex;

    private int questionsLen;

    private int getCurrentQuestionNumber () {
        return questionIndex + 1;
    }

    private void setCurrentQuestionNumber (int questionNumber) {
        questionIndex = questionNumber - 1;
    }

    private boolean isValidQuestionIndex (int questionNumber) {
        if (questionNumber < minQuestionIndex || questionNumber > maxQuestionIndex) {
            return false;
        }
        return true;
    }

    private boolean isFirstQuestion () {
        boolean isFirst = questionIndex == minQuestionIndex;
        Log.d(TAG, "isFirst: " + isFirst + ", currNum: " + getCurrentQuestionNumber());
        return isFirst;
    }

    private boolean isLastQuestion () {
        boolean isLast = questionIndex == maxQuestionIndex;
        Log.d(TAG, "isLast: " + isLast + ", currNum: " + getCurrentQuestionNumber());
        return isLast;
    }

    public void goToQuestion () {
        goToQuestion(getCurrentQuestionNumber());
    }

    public void goToQuestion (int questionNumber) {
        if (!isValidQuestionIndex(questionNumber - 1)) {
            return;
        }

        currentQuestionNumberTV.setText(String.valueOf(questionNumber));
        renderQuestionIndex(questionNumber - 1);
    }

    private Runnable spotlightTimerRunnable;

    private CustomTarget target;

    private float spotlightPadding = 0.38f;

    private void setSpotlightTimerRunnable() {
        spotlightTimerRunnable = new Runnable() {
            @Override
            public void run() {
                int[] point = new int[2];
                getToolbar().getLocationInWindow(point);
                CustomTarget.Builder builder = new CustomTarget.Builder(TestActivity.this)
                        .setView(R.layout.spotlight_test_timer);

                if (null != timerPoint) {
                    if (Constants.TOAST_VERBOSE) {
                        Toast.makeText(TestActivity.this, "Using generated timer point", Toast.LENGTH_SHORT)
                                .show();
                    }
                    float halfX = timerView.getWidth() / 2.0f, halfY = timerView.getHeight() / 2.0f;
                    builder.setPoint(timerPoint[0] + halfX, timerPoint[1] + halfY);
                    builder.setRadius(Math.max(halfX, halfY) * 2.0f * spotlightPadding);
                }
                else {
                    if (Constants.TOAST_VERBOSE) {
                        Toast.makeText(TestActivity.this, "Using default timer point", Toast.LENGTH_SHORT)
                                .show();
                    }
                    builder.setRadius(60.0f);
                    builder.setPoint(point[0] - 170.0f + getToolbar().getWidth(),
                                point[1] + getToolbar().getHeight() / 2.0f);
                }

                target = builder.build();
                Spotlight.with(TestActivity.this)
                        .setOverlayColor(ContextCompat.getColor(TestActivity.this, R.color.transparent_bg_dark))
                        .setDuration(5L)
                        .setAnimation(new DecelerateInterpolator(2.0f))
                        .setTargets(target)
                        .setClosedOnTouchedOutside(true)
                        .setOnSpotlightStartedListener(new OnSpotlightStartedListener() {
                            @Override
                            public void onStarted() {
                                isSpotlightShowing = true;
                            }
                        })
                        .setOnSpotlightEndedListener(new OnSpotlightEndedListener() {
                            @Override
                            public void onEnded() {
                                isSpotlightShowing = false;
                            }
                        })
                        .start();
            }
        };
    }

    public void setNotFirstTest () {
        SharedPreferences.Editor editor = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext()).edit();
        editor.putBoolean(Keys.IS_FIRST_TEST_KEY, false);
        editor.apply();
    }

    private boolean isFirstTest = false;

    private void runWelcomeDialog () {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                SharedPreferences preferences = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());

                isFirstTest = preferences.getBoolean(Keys.IS_FIRST_TEST_KEY, true);

                if (isFirstTest || Constants.DAILOG_DEBUG) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            stopTimer();
                            setToFullTime();
                            AlertDialog.Builder builder = new AlertDialog.Builder(TestActivity.this)
                                    .setCancelable(true)
                                    .setTitle("Hello")
                                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                        @Override
                                        public void onCancel(DialogInterface dialog) {
                                            startTimer();
                                            setSpotlightTimerRunnable();
                                            runSpotlightTimerRunnable();
                                        }
                                    })
                                    .setMessage(String.format(Locale.getDefault(), "Welcome to %s, an app built to help Unijos students do better in their exams. Everyone at UJHub and Makerloom Software hopes that this app helps you to do better in your exams and build a brighter future. Have a blast!",
                                            getString(R.string.app_name)))
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
                }
            }
        });
    }

    private void runSpotlightTimerRunnable() {
        if (null != spotlightTimerRunnable) {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    SharedPreferences preferences = PreferenceManager
                            .getDefaultSharedPreferences(getBaseContext());

                    isFirstTest = preferences.getBoolean(Keys.IS_FIRST_TEST_KEY, true);
                    // isFirstTest = true;

                    if (isFirstTest || Constants.SPOTLIGHT_DEBUG) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                spotlightTimerRunnable.run();
                            }
                        });

                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                setNotFirstTest();
                            }
                        });
                    }
                }
            });
        }
    }

    View.OnClickListener onNext = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            nextQuestion();
            focusBottom();
        }
    };

    View.OnClickListener onPrevious = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            previousQuestion();
            focusBottom();
        }
    };

    View.OnClickListener onFinish = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Removing Ads
//            showInterstitialIfPossible();
            submit();
        }
    };

    private void refreshBtns () {
        if (isFirstQuestion()) {
            disablePrevBtn();
        }
        else {
            enablePrevBtn();
        }

        if (isLastQuestion()) {
            showFinishBtn();
        }
        else {
            showNextBtn();
        }
    }

    public void handleNumberBtns () {
        for (int i = 0, len = numberBtns.size(); i < len; ++i) {
            boolean selected = i == questionIndex;
            boolean answered = questions.get(i).isAnswered();

            if (answered && selected) {
                showQuestionButtonUnanswered(numberBtns.get(i));
                showQuestionButtonAnsweredSelected(numberBtns.get(i));
            }
            else if (answered && !selected) {
                showQuestionButtonUnanswered(numberBtns.get(i));
                showQuestionButtonAnswered(numberBtns.get(i));
            }
            else if (!answered && selected) {
                showQuestionButtonUnanswered(numberBtns.get(i));
                showQuestionButtonSelected(numberBtns.get(i));
            }
            else {
                showQuestionButtonUnanswered(numberBtns.get(i));
            }
        }
    }

    private void handleNumberBtn (int i) {
        // int i = lastNumberBtnIndex;
        boolean selected = i == questionIndex;
        boolean answered = questions.get(i).isAnswered();

        try {
            if (answered && selected) {
                showQuestionButtonUnanswered(numberBtns.get(i));
                showQuestionButtonAnsweredSelected(numberBtns.get(i));
            } else if (answered && !selected) {
                showQuestionButtonUnanswered(numberBtns.get(i));
                showQuestionButtonAnswered(numberBtns.get(i));
            } else if (!answered && selected) {
                showQuestionButtonUnanswered(numberBtns.get(i));
                showQuestionButtonSelected(numberBtns.get(i));
            } else {
                showQuestionButtonUnanswered(numberBtns.get(i));
            }
        }
        catch (IndexOutOfBoundsException ex) {
            ex.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (event instanceof QuestionsUpdateEvent) { }
    }

    private void renderQuestionIndex (int qIndex) {
        Question question = questions.get(qIndex);

        // Check if question has a passage
        if (question.hasPassage()) {
            showPassageBtn.setVisibility(View.VISIBLE);
            showPassageBtn.setEnabled(true);
            passageText = question.getPassage();
            if (!TextUtils.isEmpty(passageText)) {
                boolean isInst = passageText.length() <= Commons.MAX_INSTRUCTION_LENGTH;
                showPassageBtn.setText(isInst ? "Show Instruction" : "Show Passage");
            }
        }
        else {
            showPassageBtn.setVisibility(View.GONE);
            showPassageBtn.setEnabled(false);
        }

        questionTV.setText(TextViewUtils.Companion.fromHtml(question.getQuestion()));
        Log.d(TAG, "Question: " + question.getQuestion());

        OptionAdapter optionAdapter = new OptionAdapter(TestActivity.this, question);
        optionRV.setAdapter(optionAdapter);

        RecyclerView.LayoutManager manager = new GridLayoutManager(TestActivity.this,
                UI.getSpanCount(TestActivity.this));
        optionRV.setLayoutManager(manager);
        ViewCompat.setNestedScrollingEnabled(optionRV, false);

        questionIndex = qIndex;
        refreshBtns();
        handleNumberBtn(lastNumberBtnIndex);
        handleNumberBtn(qIndex);

        lastNumberBtnIndex = questionIndex;

        if (!setNumberBtnsForQuestions) {
            for (int i = 0, len = numberBtns.size(); i < len; ++i) {
                question.setNumberBtn(numberBtns.get(i));
            }
            if (!numberBtns.isEmpty()) {
                setNumberBtnsForQuestions = true;
            }
        }
    }

    private boolean setNumberBtnsForQuestions = false;

    public Question getCurrentQuestion () {
        return questions.get(questionIndex);
    }

    public void showQuestionButtonSelected (FancyButton button) {
        int positiveColor = getResources().getColor(R.color.colorPrimary),
                nuetralColor = getResources().getColor(R.color.white);

        button.setBorderColor(positiveColor);
        button.setBackgroundColor(positiveColor);
        button.setTextColor(nuetralColor);
    }

    public void showQuestionButtonAnswered (FancyButton button) {
        int positiveColor = getResources().getColor(R.color.colorPrimary),
                nuetralColor = getResources().getColor(R.color.white);

        button.setBorderColor(positiveColor);
        button.setTextColor(positiveColor);
        button.setBackgroundColor(nuetralColor);
        button.getTextViewObject().setTypeface(Typeface.DEFAULT_BOLD);
    }

    public void showQuestionButtonAnsweredSelected (FancyButton button) {
        int positiveColor = getResources().getColor(R.color.colorPrimary),
                nuetralColor = getResources().getColor(R.color.white);

        button.setBorderColor(positiveColor);
        button.setTextColor(nuetralColor);
        button.setBackgroundColor(positiveColor);
        button.getTextViewObject().setTypeface(Typeface.DEFAULT_BOLD);
    }

    public void showQuestionButtonUnanswered (FancyButton button) {
        int positiveColor = getResources().getColor(R.color.colorPrimary),
                nuetralColor = getResources().getColor(R.color.white),
                border = getResources().getColor(R.color.md_grey_400),
                focus = getResources().getColor(R.color.white),
                text = getResources().getColor(R.color.md_grey_500);

        button.setTextColor(text);
        button.getTextViewObject().setTypeface(Typeface.DEFAULT);
        button.setFocusBackgroundColor(focus);
        button.setBorderColor(border);
        button.setBackgroundColor(nuetralColor);
    }

    private void nextQuestion () {
        goToQuestion(getCurrentQuestionNumber() + 1);
    }

    private void previousQuestion () {
        goToQuestion(getCurrentQuestionNumber() - 1);
    }

    private String getCorrectionQuestionsJSON () {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Question>>() {}.getType();
        String json = gson.toJson(questions, listType);
        return json;
    }

    private void submit () {
        calculateScores();

        Intent report = new Intent(TestActivity.this, ReportActivity.class);
        report.putExtra(Keys.COURSE_NAME_KEY, getCourseName());
        report.putExtra(Keys.DEPARTMENT_NAME_KEY, getDeptName());
        report.putExtra(Keys.CORRECTION_QUESTIONS_JSON_KEY, getCorrectionQuestionsJSON());
        report.putExtra(Keys.QUESTIONS_ANSWERED_KEY, questionsAnswered);
        report.putExtra(Keys.CORRECT_ANSWERS_KEY, correctAnswers);
        report.putExtra(Keys.TOTAL_QUESTIONS_KEY, totalQuestions);
        report.putExtra(Keys.SCORE_KEY, score);
        report.putExtra(Keys.GRADE_KEY, grade);
        report.putExtra(Keys.TIME_LEFT_KEY, timeLeft);
        report.putExtra(Keys.TIME_USED_KEY, timeUsed);
        startActivity(report);

        finish();
    }

    Integer correctAnswers = 0, questionsAnswered = 0, totalQuestions = 0;
    Double score = 0.0;
    Character grade = '\0';
    Long timeUsed = 0L, timeLeft = 0L;
    private void calculateScores () {
        correctAnswers = 0;
        questionsAnswered = 0;
        for (int i = 0, len = questions.size(); i < len; ++i) {
            if (questions.get(i).isCorrect()) {
                ++correctAnswers;
            }
            if (questions.get(i).isAnswered()) {
                ++questionsAnswered;
            }
        }

        totalQuestions = questions.size();

        score = ((double) correctAnswers / totalQuestions) * 100;

        grade = getGrade(score);

        timeUsed = totalTime - millisRemaining;

        timeLeft = millisRemaining;
    }

    private Character getGrade (Double score) {
        if (score >= 70) {
            return 'A';
        }
        else if (score >= 60) {
            return 'B';
        }
        else if (score >= 50) {
            return 'C';
        }
        else if (score >= 40) {
            return 'D';
        }
        else {
            return 'E';
        }
    }

    Long totalTime = TimeUnit.MINUTES.toMillis(timeInMinutes);

    Long interval = 1000L;

    boolean isTimerAlmostCompleted = false;

    CountDownTimer countDownTimer;

    long millisRemaining = totalTime;

    MenuItem timer;
    
    private int [] timerPoint = null;

    private View timerView = null;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.timer_menu, menu);

        timer = menu.findItem(R.id.action_timer);

        getToolbar().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    getToolbar().getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }

                timerView = getToolbar().findViewById(R.id.action_timer);
                if (null != timerView) {
                    timerPoint = new int [2];
                    timerView.getLocationInWindow(timerPoint);
                    if (Constants.TOAST_VERBOSE) {
                        Toast.makeText(TestActivity.this,
                                String.format(Locale.ENGLISH, "Timer point: %d, %d", timerPoint[0], timerPoint[1]), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        countDownTimer = buildCountDownTimer(millisRemaining, timer);
        countDownTimer.start();

        return true;
    }

    CountDownTimer buildCountDownTimer (long millis, final MenuItem timer) {
        return new CountDownTimer(millis, interval) {
            @Override
            public void onTick(long millisUntilFinished) {
                TestActivity.this.millisRemaining = millisUntilFinished;

                String hms = new SimpleDateFormat("mm:ss").format(millisUntilFinished);
                SpannableString title = new SpannableString(hms);

                boolean almostDone = millisUntilFinished < (totalTime * almostDoneThresh);
                if (almostDone && !isTimerAlmostCompleted || isTimerAlmostCompleted) {
                    isTimerAlmostCompleted = true;
                    title.setSpan(new ForegroundColorSpan(ContextCompat.getColor(TestActivity.this,
                            R.color.md_amber_500)), 0, title.length(), 0);
                }

                timer.setTitle(title);
            }

            @Override
            public void onFinish() {
                TestActivity.this.millisRemaining = 0;

                SpannableString title = new SpannableString("Time up!");
                title.setSpan(new ForegroundColorSpan(ContextCompat.getColor(TestActivity.this,
                        R.color.white)), 0, title.length(), 0);
                timer.setTitle(title);

                submit();
            }
        };
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putLong(Keys.MILLIS_REMAINING_KEY, millisRemaining);
        outState.putInt(Keys.CURRENT_QUESTION_KEY, getCurrentQuestionNumber());

        Gson gson = new Gson();
        Type listType = new TypeToken<List<Question>>() {}.getType();

        final String qStr = gson.toJson(questions, listType);
        Log.d(TAG, qStr);
        outState.putString(Keys.TEST_QUESTIONS_JSON_KEY, qStr);

        stopTimer();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        millisRemaining = savedInstanceState.getLong(Keys.MILLIS_REMAINING_KEY);
        setCurrentQuestionNumber(savedInstanceState.getInt(Keys.CURRENT_QUESTION_KEY));

        Gson gson = new Gson();
        Type listType = new TypeToken<List<Question>>() {}.getType();

        final String qStr = savedInstanceState.getString(Keys.TEST_QUESTIONS_JSON_KEY);
        Log.d(TAG, qStr);
        questions = gson.fromJson(qStr, listType);

        startTimer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopTimer();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startTimer();
    }

    private void stopTimer () {
        if (null != countDownTimer) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    private void setToFullTime () {
        if (null != timer && null == countDownTimer) {
            String hms = new SimpleDateFormat("mm:ss").format(totalTime);
            if (null != timer) {
                timer.setTitle(hms);
            }
        }
    }

    private void startTimer () {
        if (null != timer && null == countDownTimer) {
            countDownTimer = buildCountDownTimer(millisRemaining, timer);
            countDownTimer.start();
        }
    }

    // Removing Ads
//    private boolean interstitialFailedToLoad = false;
//    AdListener interstitialAdListener = new AdListener() {
//        @Override
//        public void onAdClosed() {
//            super.onAdClosed();
//            if (Constants.TOAST_VERBOSE) {
//                Toast.makeText(TestActivity.this, "Interstitial Ad closed", Toast.LENGTH_SHORT)
//                        .show();
//            }
//            afterInterstitial();
//        }
//
//        @Override
//        public void onAdLoaded() {
//            super.onAdLoaded();
//            if (Constants.TOAST_VERBOSE) {
//                Toast.makeText(TestActivity.this, "Interstitial Ad loaded", Toast.LENGTH_SHORT)
//                        .show();
//            }
//        }
//
//        @Override
//        public void onAdClicked() {
//            super.onAdClicked();
//            if (Constants.TOAST_VERBOSE) {
//                Toast.makeText(TestActivity.this, "Interstitial Ad clicked", Toast.LENGTH_SHORT)
//                        .show();
//            }
//        }
//
//        @Override
//        public void onAdFailedToLoad(int i) {
//            super.onAdFailedToLoad(i);
//            if (Constants.TOAST_VERBOSE) {
//                Toast.makeText(TestActivity.this, "Interstitial Ad failed to load", Toast.LENGTH_SHORT)
//                        .show();
//            }
//            interstitialFailedToLoad = true;
//        }
//
//        @Override
//        public void onAdLeftApplication() {
//            super.onAdLeftApplication();
//            if (Constants.TOAST_VERBOSE) {
//                Toast.makeText(TestActivity.this, "Interstitial Ad left application", Toast.LENGTH_SHORT)
//                        .show();
//            }
////            removeInterstitialOnReturn();
//        }
//
//        @Override
//        public void onAdOpened() {
//            super.onAdOpened();
//            if (Constants.TOAST_VERBOSE) {
//                Toast.makeText(TestActivity.this, "Interstitial Ad opened", Toast.LENGTH_SHORT)
//                        .show();
//            }
//        }
//
//        @Override
//        public void onAdImpression() {
//            super.onAdImpression();
//            if (Constants.TOAST_VERBOSE) {
//                Toast.makeText(TestActivity.this, "Interstitial Ad impression", Toast.LENGTH_SHORT)
//                        .show();
//            }
//        }
//    };


    @Override
    public void onBackPressed() {
        if (isSpotlightShowing) {
            hideSpotlight();
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(TestActivity.this)
                    .setCancelable(false)
                    .setTitle("Quit?")
                    .setMessage("Are you sure you want to quit the exam? Your current progress will not be saved.")
                    .setPositiveButton("Quit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            TestActivity.this.finish();
                        }
                    })
                    .setNegativeButton("Continue Exam", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startTimer();
                            dialog.cancel();
                        }
                    });

            final AlertDialog dialog = builder.create();
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    stopTimer();
                    Button buttonPositive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    buttonPositive.setAllCaps(false);
                    buttonPositive.setTextColor(ContextCompat
                            .getColor(TestActivity.this, R.color.red));
                    Button buttonNegative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                    buttonNegative.setTextColor(ContextCompat
                            .getColor(TestActivity.this, R.color.colorPrimary));
                    buttonNegative.setAllCaps(false);
                }
            });
            dialog.show();
        }
    }

    private void hideSpotlight () {
        if (null != target) {
            target.closeTarget();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }
}
