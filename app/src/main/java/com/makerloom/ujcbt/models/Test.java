package com.makerloom.ujcbt.models;

import java.util.List;

/**
 * Created by michael on 4/11/18.
 */

public class Test {
    private List<Question> questions;

    private Department department;

    private Course course;

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

//    public Test () {}

    public Course getCourse() {
        return course;
    }

    public Department getDepartment() {
        return department;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }
}
