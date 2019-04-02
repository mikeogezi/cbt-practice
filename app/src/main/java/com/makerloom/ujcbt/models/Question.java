package com.makerloom.ujcbt.models;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.makerloom.common.utils.Constants;
import com.makerloom.ujcbt.screens.TestActivity;

import java.util.List;

import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by michael on 4/11/18.
 */

public class Question {
    // For long form questions like comprehensions and
    // multiple questions based on one piece of information
    private String passage;

    private String question;

    private List<String> options;

    // CheckBoxes for the question
    private transient AppCompatCheckBox [] optionCheckBoxes;

    // Number Button for the question
    private transient FancyButton numberBtn;

    private String correctOption;

    private String selectedOption;

    public Boolean isCorrect () {
        return correctOption.equals(selectedOption);
    }

    public Boolean isAnswered () {
        return !TextUtils.isEmpty(selectedOption);
    }

    public Boolean hasPassage () {
        return !TextUtils.isEmpty(getPassage());
    }

    public Question() {
        super();
        optionCheckBoxes = new AppCompatCheckBox [Constants.MAX_OPTIONS];
        wrongOptionCheckBoxes = new AppCompatCheckBox [Constants.MAX_OPTIONS];
        correctOptionCheckBoxes = new AppCompatCheckBox [Constants.MAX_OPTIONS];
    }

    public Question(String question, List<String> options) {
        this.question = question;
        this.options = options;
        this.optionCheckBoxes = new AppCompatCheckBox [getOptions().size()];
        this.wrongOptionCheckBoxes = new AppCompatCheckBox [getOptions().size()];
        this.correctOptionCheckBoxes = new AppCompatCheckBox [getOptions().size()];
    }

    private transient boolean hasCleanedOptions = false;

    public String getPassage() {
        return passage;
    }

    public void setPassage(String passage) {
        this.passage = passage;
    }

    public List<String> getOptions() {
        if (!hasCleanedOptions) {
            for (int i = 0, len = options.size(); i < len; ++i) {
                options.set(i, options.get(i).replaceAll("\\xa0", " "));
            }

            hasCleanedOptions = true;
        }

        return options;
    }

    private transient boolean hasCleanedCorrectOption = false;

    public String getCorrectOption() {
        if (!hasCleanedCorrectOption) {
            setCorrectOption(correctOption.replaceAll("\\xa0", " "));
            hasCleanedCorrectOption = true;
        }
        
        return correctOption;
    }

    private transient boolean hasCleanedQuestion = false;

    public String getQuestion() {
        if (!hasCleanedQuestion) {
            setQuestion(question.replaceAll("\\xa0", " "));
            hasCleanedQuestion = true;
        }

        return question;
    }

    public String getSelectedOption() {
        return selectedOption;
    }

    public void setCorrectOption(String correctOption) {
        this.correctOption = correctOption;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setSelectedOption(String selectedOption) {
        this.selectedOption = selectedOption;
    }

    public void selectOption (String selectedOption, int index) {
        setSelectedOption(selectedOption);
        for (int i = 0, len = getOptions().size(); i < len; ++i) {
            if (getOptionCheckBoxes()[i].isChecked() && !getOptions().get(i).equals(selectedOption)) {
                getOptionCheckBoxes()[i].setChecked(false);
            }
        }
        if (getOptionCheckBoxes()[index].isEnabled()) {
            getOptionCheckBoxes()[index].setChecked(true);
        }
    }

    public void addOptionCheckBoxes(AppCompatCheckBox optionCheckBox, int index) {
        this.optionCheckBoxes[index] = optionCheckBox;
    }

    public AppCompatCheckBox [] getOptionCheckBoxes() {
        return optionCheckBoxes;
    }

    public FancyButton getNumberBtn() {
        return numberBtn;
    }

    public void setNumberBtn(FancyButton numberBtn) {
        this.numberBtn = numberBtn;
    }

    private final String TAG = Question.class.getSimpleName();

    public void markSelected () {
        for (int i = 0, len = getOptions().size(); i < len; ++i) {
            AppCompatCheckBox checkBox = getOptionCheckBoxes()[i];
            String option = getOptions().get(i);

            if (option.equals(selectedOption)) {
                checkBox.setChecked(true);
            }
            else {
                checkBox.setChecked(false);
            }
        }
    }

    public void boldenNumberBtn(Context context) {
        if (null != getNumberBtn() && context instanceof TestActivity) {
            getNumberBtn().getTextViewObject().setTypeface(Typeface.DEFAULT_BOLD);
        }
    }

    public void unBoldenNumberBtn(Context context) {
        if (null != getNumberBtn() && context instanceof TestActivity) {
            getNumberBtn().getTextViewObject().setTypeface(Typeface.DEFAULT);
        }
    }

    public void tickAnsweredCheckBox () {
        if (isAnswered() && null != getOptionCheckBoxes()) {
            for (int i = 0, len = getOptions().size(); i < len; ++i) {
                boolean isSelected = getOptions().get(i).equals(getSelectedOption());
                getOptionCheckBoxes()[i].setChecked(false);
                if (isSelected) {
                    getOptionCheckBoxes()[i].setChecked(true);
                }
            }
        }
    }

    private transient AppCompatCheckBox [] wrongOptionCheckBoxes;

    private transient AppCompatCheckBox [] correctOptionCheckBoxes;

    public void addWrongOptionCheckBox(AppCompatCheckBox optionCheckBox, int index) {
        this.wrongOptionCheckBoxes[index] = optionCheckBox;
    }

    public void addCorrectOptionCheckBox(AppCompatCheckBox optionCheckBox, int index) {
        optionCheckBox.setClickable(false);
        this.correctOptionCheckBoxes[index] = optionCheckBox;
    }

    public AppCompatCheckBox [] getWrongOptionCheckBoxes() {
        return wrongOptionCheckBoxes;
    }

    public AppCompatCheckBox [] getCorrectOptionCheckBoxes () {
        return correctOptionCheckBoxes;
    }

    public void doCorrection () {
        Log.d(TAG, String.format("Question: %s\n%s\n%s", getQuestion(), getCorrectOption(), getOptions().toString()));

        for (int i = 0, len = getOptions().size(); i < len; ++i) {
            String option = getOptions().get(i);

            // Correct -> Tick Green
            if (option.equals(getCorrectOption())) {
                getCorrectOptionCheckBoxes()[i].setVisibility(View.VISIBLE);
                getCorrectOptionCheckBoxes()[i].setChecked(true);
                getCorrectOptionCheckBoxes()[i].setEnabled(true);
                getOptionCheckBoxes()[i].setVisibility(View.GONE);
                getWrongOptionCheckBoxes()[i].setVisibility(View.GONE);
            }
            // Wrong Selected -> Tick Red
            else if (option.equals(getSelectedOption())) {
                getWrongOptionCheckBoxes()[i].setVisibility(View.VISIBLE);
                getWrongOptionCheckBoxes()[i].setChecked(true);
                getWrongOptionCheckBoxes()[i].setEnabled(true);
                getOptionCheckBoxes()[i].setVisibility(View.GONE);
                getCorrectOptionCheckBoxes()[i].setVisibility(View.GONE);
            }
            // Wrong Unselected -> Ignore
            else {
                getOptionCheckBoxes()[i].setVisibility(View.VISIBLE);
                getOptionCheckBoxes()[i].setChecked(false);
                getOptionCheckBoxes()[i].setEnabled(false);
                getWrongOptionCheckBoxes()[i].setVisibility(View.GONE);
                getCorrectOptionCheckBoxes()[i].setVisibility(View.GONE);
            }
        }
    }
}
