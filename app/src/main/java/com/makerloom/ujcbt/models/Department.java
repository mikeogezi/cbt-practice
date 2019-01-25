package com.makerloom.ujcbt.models;

import java.util.List;

/**
 * Created by michael on 4/11/18.
 */

public class Department {
    private List<Course> courses;

    private String faculty;

    private String name;

    private String shortName;

    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }
}
