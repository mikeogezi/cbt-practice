package com.makerloom.ujcbt.holders;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.makerloom.ujcbt.R;
import com.makerloom.ujcbt.models.Course;
import com.makerloom.ujcbt.screens.TestActivity;
import com.makerloom.common.utils.Constants;
import com.makerloom.common.utils.Keys;

/**
 * Created by michael on 4/11/18.
 */

public class CourseHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private Context context;
    private Course course;

    public TextView courseCodeTV, courseTitleTV;
    public TextView numberOfQuestionsTV, timeAllowedTV;
    public CardView cardView;
    public ImageView shortNameIV, codeIV;

    public CourseHolder (View view, Context context) {
        super(view);

        courseCodeTV = view.findViewById(R.id.code);
        courseTitleTV = view.findViewById(R.id.course);
        numberOfQuestionsTV = view.findViewById(R.id.number_of_questions);
        timeAllowedTV = view.findViewById(R.id.time_allowed);
        cardView = view.findViewById(R.id.card_view);
        shortNameIV = view.findViewById(R.id.short_name_image);
        codeIV = view.findViewById(R.id.course_code_image);

        this.context = context;
    }

    public void setCourse (Course course) {
        this.course = course;
    }

    private String getDeptName (AppCompatActivity context) {
        if (context.getIntent().hasExtra(Keys.DEPARTMENT_NAME_KEY)) {
            return context.getIntent().getStringExtra(Keys.DEPARTMENT_NAME_KEY);
        }
        else {
            return Constants.DEFAULT_DEPARTMENT_NAME;
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(context, TestActivity.class);
        intent.putExtra(Keys.DEPARTMENT_NAME_KEY, getDeptName((AppCompatActivity) context));
        intent.putExtra(Keys.COURSE_NAME_KEY, course.getCourseCode());
        context.startActivity(intent);
    }
}
