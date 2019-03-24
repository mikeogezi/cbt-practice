package com.makerloom.ujcbt.adapters;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.makerloom.common.utils.UI;
import com.makerloom.ujcbt.R;
import com.makerloom.ujcbt.holders.QuestionHolder;
import com.makerloom.ujcbt.models.Question;

import java.util.List;

/**
 * Created by michael on 4/12/18.
 */

public class QuestionAdapter extends RecyclerView.Adapter<QuestionHolder> {
    private List<Question> questions;
    private Context context;

    public QuestionAdapter (Context context, List<Question> questions) {
        this.context = context;
        this.questions = questions;
    }

    @Override
    public QuestionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.correction_card, null);
        return new QuestionHolder(view, context);
    }

    @Override
    public void onBindViewHolder(QuestionHolder holder, int position) {
        Question question = questions.get(position);

//        boolean last = position == questions.size() - 1;
//        boolean first = 0 == questions.size();

        holder.setQuestion(question);

        holder.numberTV.setText(String.valueOf(position + 1));
        holder.questionTV.setText(question.getQuestion());
        holder.cardView.setOnClickListener(holder);

        OptionAdapter adapter = new OptionAdapter(context, question);
        holder.optionRV.setAdapter(adapter);

        RecyclerView.LayoutManager manager = new GridLayoutManager(context,
                UI.getSpanCount(context));
        holder.optionRV.setLayoutManager(manager);
        ViewCompat.setNestedScrollingEnabled(holder.optionRV, false);

//        if (first && context instanceof CorrectionActivity) {
//            ((CorrectionActivity) context).runCorrectionIntro();
//        }
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }
}

