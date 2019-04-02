package com.makerloom.ujcbt.models;

import android.content.Context;

import com.makerloom.ujcbt.utils.QuestionsFilesUtils;

/**
 * Created by michael on 4/11/18.
 */

public class Course {
    private String courseCode;

    private String name;

    private Integer maxQuestions = 50;

    private Integer time = 30;

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public Integer getMaxQuestions() {
        return maxQuestions;
    }

    public void setMaxQuestions(Integer maxQuestions) {
        this.maxQuestions = maxQuestions;
    }

    public Course (String courseCode, String name) {
        this.courseCode = courseCode;
        this.name = name;
    }

    public Course (String courseCode) {
        this.courseCode = courseCode;
        this.name = this.courseCode;
    }

//    public Course () {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public static Test generateTest (Course course, Context context, int questionLen) {
        return course.generateTest(context, questionLen);
    }

    public Test generateTest (Context context, int questionLen) {
//        String filename = "questions_" + getCourseCode().replace(" ", "").toLowerCase(Locale.ENGLISH);
//
//        Gson gson = new Gson();
//
//        InputStream rawInputStream = context.getResources().openRawResource(
//                context.getResources().getIdentifier(filename, "raw", context.getPackageName()));
//        Reader reader = new BufferedReader(new InputStreamReader(rawInputStream));
//
//        Questions questions = gson.fromJson(reader, Questions.class);
        Questions questions = QuestionsFilesUtils.Companion.getQuestionsFile(context, getCourseCode());
        questions.prepare(questionLen);

        Test test = new Test();
        test.setCourse(Course.this);
        test.setDepartment(new Department());
        test.setQuestions(questions.getQuestions());

        return test;
    }
}
