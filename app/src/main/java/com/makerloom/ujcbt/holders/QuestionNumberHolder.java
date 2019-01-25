package com.makerloom.ujcbt.holders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.makerloom.ujcbt.R;
import com.makerloom.ujcbt.screens.TestActivity;

import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by michael on 4/11/18.
 */

public class QuestionNumberHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private Context context;
    private Integer number;

    public FancyButton numberFB;

    public QuestionNumberHolder (View view, Context context) {
        super(view);

        numberFB = view.findViewById(R.id.number);
        numberFB.setOnClickListener(this);

        this.context = context;
    }

    public void setQuestionNumber (Integer number) {
        this.number = number;
    }

    @Override
    public void onClick(View v) {
        if (context instanceof TestActivity) {
            TestActivity testActivity = (TestActivity) context;
            testActivity.goToQuestion(number);
        }
    }
}
