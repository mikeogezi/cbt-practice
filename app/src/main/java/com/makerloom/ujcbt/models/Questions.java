package com.makerloom.ujcbt.models;

import com.makerloom.common.utils.Constants;

import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by michael on 4/11/18.
 */

public class Questions {
    private List<Question> questions;

    private Integer maxQuestions;

    private Integer time;

//    public Questions () {}

    public Integer getMaxQuestions() {
        return maxQuestions;
    }

    public void setMaxQuestions(Integer maxQuestions) {
        this.maxQuestions = maxQuestions;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public void prepare () {
        prepare(Constants.MAX_QUESTIONS);
    }

    public void prepare (int questionCount) {
        if (questionCount > questions.size()) {
            questionCount = questions.size();
        }

        // According to Neil Coffey (https://www.javamex.com/tutorials/random_numbers/random_sample.shtml),
        // this method is "Semi Naive"
        shuffle();
        trimTo(questionCount);
        shuffle();

        for (int i = 0, len = questions.size(); i < len; ++i) {
            Collections.shuffle(questions.get(i).getOptions());
        }
    }

    public void shuffle () {
        Collections.shuffle(getQuestions());
    }

    public void trimTo (int questionCount) {
        setQuestions(questions.subList(0, questionCount));
    }

    public void trimTo () {
        trimTo(Constants.MAX_QUESTIONS);
    }
}
