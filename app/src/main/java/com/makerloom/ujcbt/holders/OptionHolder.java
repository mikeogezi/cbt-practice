package com.makerloom.ujcbt.holders;

import android.content.Context;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.makerloom.ujcbt.R;
import com.makerloom.ujcbt.models.Department;

/**
 * Created by michael on 4/11/18.
 */

public class OptionHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private Context context;
    private String option;

    public TextView optionTV;
    public AppCompatCheckBox optionCB, optionCBWrong, optionCBCorrect;
    public CardView cardView;

    public OptionHolder (View view, Context context) {
        super(view);

        optionTV = view.findViewById(R.id.option);
        optionCB = view.findViewById(R.id.checkbox);
        optionCBCorrect = view.findViewById(R.id.checkbox_correct);
        optionCBWrong = view.findViewById(R.id.checkbox_wrong);
        cardView = view.findViewById(R.id.card_view);

        this.context = context;
    }

    public boolean isCorrection () {
        return null != optionCBWrong && null != optionCBCorrect;
    }

    public void setOption(String option) {
        this.option = option;
    }

    @Override
    public void onClick(View v) {}
}
