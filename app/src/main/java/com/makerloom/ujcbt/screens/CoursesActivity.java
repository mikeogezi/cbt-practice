package com.makerloom.ujcbt.screens;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.makerloom.ujcbt.R;
import com.makerloom.common.activity.MyBackToolbarActivity;
import com.makerloom.ujcbt.adapters.CourseAdapter;
import com.makerloom.ujcbt.models.Department;
import com.makerloom.common.utils.Constants;
import com.makerloom.common.utils.Keys;
import com.makerloom.common.utils.UI;
import com.makerloom.ujcbt.utils.Commons;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by michael on 4/11/18.
 */

public class CoursesActivity  extends MyBackToolbarActivity {
    private RecyclerView courseRV;

    private RelativeLayout adLayout;

    private TextView adInfo;

    private AdView adView;

    private String getDeptName () {
        if (getIntent().hasExtra(Keys.DEPARTMENT_NAME_KEY)) {
            return getIntent().getStringExtra(Keys.DEPARTMENT_NAME_KEY);
        }
        else {
            return Constants.DEFAULT_DEPARTMENT_NAME;
        }
    }

    RecyclerView.LayoutManager manager;

    CourseAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Commons.goToWelcomeIfNotSignedIn(this);
        setContentView(R.layout.activity_courses);

        spinKitView = findViewById(R.id.progress_bar);
        showProgress();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Gson gson = new Gson();
                String departmentName = getDeptName();

                InputStream rawInputStream = getResources().openRawResource(R.raw.departments);
                Reader reader = new BufferedReader(new InputStreamReader(rawInputStream));

                Type listType = new TypeToken<List<Department>>() {}.getType();
                List<Department> departments = gson.fromJson(reader, listType);
                Department department = null;
                for (Department dept : departments) {
                    if (dept.getName().equals(departmentName)) {
                        department = dept;
                        break;
                    }
                }


                if (null != department) {
                    final String shortName = department.getShortName();
                    manager = new LinearLayoutManager(CoursesActivity.this);
                    adapter = new CourseAdapter(CoursesActivity.this, department.getCourses());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setTitle(String.format("%s Courses", shortName));
                            showCourses();
                            hideProgress();
                        }
                    });
                }
            }
        });

        // Banner
        adLayout = findViewById(R.id.ad_layout);
        adInfo = findViewById(R.id.ad_info);
        adView = findViewById(R.id.ad_view);
        UI.loadFooterBannerAd(CoursesActivity.this, adLayout, adView, adInfo);
    }

    private void showCourses () {
        courseRV = findViewById(R.id.recycler_view);
        courseRV.setAdapter(adapter);
        courseRV.setLayoutManager(manager);
    }

    private void hideSpotlight () {
        if (null != adapter) {
            adapter.hideSpotlight();
        }
    }

    @Override
    public void onBackPressed() {
        if (isSpotlightShowing) {
            hideSpotlight();
        }
        else {
            super.onBackPressed();
        }
    }
}
