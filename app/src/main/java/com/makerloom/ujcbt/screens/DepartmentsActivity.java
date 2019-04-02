package com.makerloom.ujcbt.screens;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.makerloom.common.activity.MyPlainToolbarActivity;
import com.makerloom.common.utils.Constants;
import com.makerloom.common.utils.Keys;
import com.makerloom.ujcbt.R;
import com.makerloom.ujcbt.adapters.DepartmentAdapter;
import com.makerloom.ujcbt.events.MessageEvent;
import com.makerloom.ujcbt.events.QuestionsUpdateEvent;
import com.makerloom.ujcbt.models.Course;
import com.makerloom.ujcbt.models.Department;
import com.makerloom.ujcbt.utils.Commons;
import com.makerloom.ujcbt.utils.DepartmentsFileUtils;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.BadgeStyle;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Locale;

// Removing Ads
//import com.google.android.gms.ads.AdView;

/**
 * Created by michael on 4/11/18.
 */

public class DepartmentsActivity extends MyPlainToolbarActivity {
    private RecyclerView departmentRV;

    private RelativeLayout adLayout;

    private TextView adInfo;

    // Removing Ads
//    private AdView adView;

    private Drawer drawer;

    private RecyclerView.LayoutManager manager;

    private DepartmentAdapter adapter;

    private Drawer.OnDrawerItemClickListener getWebsiteIntentListener (final String websiteUrl) {
        return new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(websiteUrl));
                startActivity(intent);
                return false;
            }
        };
    }

    private void showFromUnlockActivityDialog () {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder builder = new AlertDialog.Builder(DepartmentsActivity.this)
                                .setCancelable(true)
                                .setTitle("Welcome")
                                .setMessage(String.format(Locale.getDefault(), "Welcome to %s. Thank you for purchasing a PIN. Your PIN will allow you to access the app for two months. Good luck!",
                                        getString(R.string.app_name)))
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                });
            }
        });
    }

    private Drawer.OnDrawerItemClickListener goToActivity (final Class clazz) {
        return new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                Intent intent = new Intent(DepartmentsActivity.this, clazz);
                startActivity(intent);
                return false;
            }
        };
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (event instanceof QuestionsUpdateEvent) {
            if (hasWindowFocus()) {
                restartActivity();
            }
            else {
                hasOnResumeUpdateJob = true;
            }
        }
    }

    private Boolean hasOnResumeUpdateJob = false;

    @Override
    protected void onResume() {
        super.onResume();
        if (hasOnResumeUpdateJob) {
            restartActivity();
            hasOnResumeUpdateJob = false;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Commons.goToWelcomeIfNotSignedIn(this);
        setContentView(R.layout.activity_departments);

        setTitle(String.format(Locale.ENGLISH, "%s Departments", getString(R.string.app_name)));

        if (getIntent().hasExtra(Keys.FROM_UNLOCK_ACTIVITY) || Constants.DEMO_VERSION) {
            showFromUnlockActivityDialog();
        }

        setProgressBar(findViewById(R.id.progress_bar));
        showProgress();

//        AccountHeader header = new AccountHeaderBuilder()
//                .withActivity(DepartmentsActivity.this)
//                .withCompactStyle(true)
//                .withHeightDp(0)
//                .addProfiles(new ProfileDrawerItem()
//                        .withIcon(R.drawable.ic_notification)
//                        .withName("")
//                        .withEmail(""))
//                .withHeaderBackground(R.color.colorPrimary)
//                .build();

        drawer = new DrawerBuilder()
                .withActivity(this)
//                .withAccountHeader(header)
                .withToolbar(getToolbar())
                .build();

        EventBus.getDefault().register(this);

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Gson gson = new Gson();

//                InputStream rawInputStream = getResources().openRawResource(R.raw.departments);
//                Reader reader = new BufferedReader(new InputStreamReader(rawInputStream));
//
//                Type listType = new TypeToken<List<Department>> () {}.getType();
//                List<Department> departments = gson.fromJson(reader, listType);
                List<Department> departments = DepartmentsFileUtils.Companion
                        .getDeptsFile(DepartmentsActivity.this);

                final PrimaryDrawerItem dev = new PrimaryDrawerItem()
                        .withName("Developed By Makerloom Software Ltd.").withSelectable(false);
                dev.withOnDrawerItemClickListener(getWebsiteIntentListener("http://www.makerloom.com/"))
                        .withIcon(R.drawable.makerloom_drawer);
                final PrimaryDrawerItem signOut = new PrimaryDrawerItem()
                        .withName("Sign Out").withSelectable(false);
                signOut.withIcon(R.drawable.ic_open_in_new_black_24dp)
                        .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                            @Override
                            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                signOutAndGoToWelcomeScreen();
                                return false;
                            }
                        });
                final PrimaryDrawerItem info = new PrimaryDrawerItem()
                        .withName("Information & Enquiries").withSelectable(false);
                info.withIcon(R.drawable.ic_question_answer_black_24dp)
                        .withOnDrawerItemClickListener(goToActivity(InfoActivity.class));

                final PrimaryDrawerItem sponsor = new PrimaryDrawerItem().withName("Sponsored By UJHub").withSelectable(false);
                sponsor.withOnDrawerItemClickListener(getWebsiteIntentListener("http://www.ujhub.org/"))
                        .withIcon(R.drawable.ujhub_drawer);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        drawer.addStickyFooterItem(info);
                        drawer.addStickyFooterItem(signOut);
                        drawer.addStickyFooterItem(dev);
                        drawer.addStickyFooterItem(sponsor);
                    }
                });

                BadgeStyle badgeStyle = new BadgeStyle().withTextColor(ContextCompat.getColor(DepartmentsActivity.this, R.color.white))
                        .withColor(ContextCompat.getColor(DepartmentsActivity.this, R.color.colorPrimary));
                final PrimaryDrawerItem allDepts = new PrimaryDrawerItem().withName(("All Departments").toUpperCase())
                        .withSelectable(false)
                        .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                            @Override
                            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                return false;
                            }
                        });

                // .withBadge(String.valueOf(departments.size()))
                // .withBadgeStyle(badgeStyle));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        drawer.addItem(allDepts);
                        drawer.addItem(new DividerDrawerItem());
                    }
                });


                for (Department department : departments) {
                    final String deptName = department.getName();
                    final PrimaryDrawerItem primaryDrawerItem = new PrimaryDrawerItem();
                    primaryDrawerItem.withName(deptName);
                    primaryDrawerItem.withSelectable(false);
                    TextDrawable drawable = TextDrawable.builder()
                            .beginConfig()
                            .textColor(ContextCompat.getColor(DepartmentsActivity.this, R.color.white))
                            .useFont(ResourcesCompat.getFont(DepartmentsActivity.this, R.font.montserrat_bold))
                            .bold()
                            .toUpperCase()
                            .fontSize(getResources().getInteger(R.integer.department_font_size))
                            .endConfig()
                            .buildRoundRect(department.getShortName(),
                                    ColorGenerator.MATERIAL.getColor(department.getName()), 5);
                    primaryDrawerItem.withIcon(drawable);
                    primaryDrawerItem.withOnDrawerItemClickListener(getDeptClickListener(deptName));
                    BadgeStyle deptBadgeStyle = new BadgeStyle().withTextColor(ContextCompat.getColor(DepartmentsActivity.this, R.color.white))
                            .withColor(ColorGenerator.MATERIAL.getColor(department.getName()));
                    // primaryDrawerItem.withBadge(String.valueOf(department.getCourses().size()));
                    // .withBadgeStyle(deptBadgeStyle);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            drawer.addItem(primaryDrawerItem);
                        }
                    });

                    for (Course course : department.getCourses()) {
                        String courseShortName = "";
                        String courseCodeNumber = "";

                        try {
                            courseShortName = course.getCourseCode().split(" ")[0];
                            courseCodeNumber = course.getCourseCode().split(" ")[1];
                        }
                        catch (Exception ex) {
                            ex.printStackTrace();
                        }

                        final String courseName = course.getCourseCode();
                        final SecondaryDrawerItem secondaryDrawerItem = new SecondaryDrawerItem();
                        secondaryDrawerItem.withName(courseName);
                        secondaryDrawerItem.withOnDrawerItemClickListener(
                                getCourseClickListener(deptName, courseName));
                        TextDrawable secondaryDrawable = TextDrawable.builder()
                                .beginConfig()
                                .textColor(ContextCompat.getColor(DepartmentsActivity.this, R.color.white))
                                .useFont(ResourcesCompat.getFont(DepartmentsActivity.this, R.font.montserrat_bold))
                                .bold()
                                .toUpperCase()
                                .fontSize(getResources().getInteger(R.integer.course_font_size))
                                .endConfig()
                                .buildRoundRect(String.format(Locale.ENGLISH, " %s ", courseCodeNumber),
                                        ColorGenerator.MATERIAL.getColor(courseCodeNumber), 3);
                        secondaryDrawerItem.withIcon(secondaryDrawable);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                drawer.addItem(secondaryDrawerItem);
                            }
                        });
                    }

                    final DividerDrawerItem dividerDrawerItem = new DividerDrawerItem();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            drawer.addItem(dividerDrawerItem);
                        }
                    });
                }

                adapter = new DepartmentAdapter(DepartmentsActivity.this, departments);
                manager = new LinearLayoutManager(DepartmentsActivity.this);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showDepartments();
                        hideProgress();
                    }
                });
            }
        });

        // Banner
        // Removing Ads
//        adLayout = findViewById(R.id.ad_layout);
//        adInfo = findViewById(R.id.ad_info);
//        adView = findViewById(R.id.ad_view);
//        UI.loadFooterBannerAd(DepartmentsActivity.this, adLayout, adView, adInfo);
    }

    private void showDepartments () {
        departmentRV = findViewById(R.id.recycler_view);
        departmentRV.setAdapter(adapter);
        departmentRV.setLayoutManager(manager);
    }

    private static int millisecsDrawer = 250;

    private Drawer.OnDrawerItemClickListener getCourseClickListener (final String deptName, final String courseName) {
        return new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                drawer.deselect();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(DepartmentsActivity.this, TestActivity.class);
                        intent.putExtra(Keys.DEPARTMENT_NAME_KEY, deptName);
                        intent.putExtra(Keys.COURSE_NAME_KEY, courseName);
                        startActivity(intent);
                    }
                }, millisecsDrawer);
                return false;
            }
        };
    }

    private Drawer.OnDrawerItemClickListener getDeptClickListener (final String deptName) {
        return new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                drawer.deselect();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(DepartmentsActivity.this, CoursesActivity.class);
                        intent.putExtra(Keys.DEPARTMENT_NAME_KEY, deptName);
                        startActivity(intent);
                    }
                }, millisecsDrawer);
                return false;
            }
        };
    }

    @Override
    public void onBackPressed() {
        if (isSpotlightShowing) {
            hideSpotlight();
        }
        else if (null != drawer && drawer.isDrawerOpen()) {
            drawer.closeDrawer();
        }
        else {
            super.onBackPressed();
        }
    }

    private void hideSpotlight () {
        if (null != adapter) {
            adapter.hideSpotlight();
        }
    }

    private void signOutAndGoToWelcomeScreen() {
        ProgressDialog dialog = new ProgressDialog(DepartmentsActivity.this);
        dialog.setMessage("Signing Out");
        dialog.setCancelable(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.show();

        AuthUI.getInstance()
                .signOut(DepartmentsActivity.this)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        try {
                            dialog.dismiss();
                        }
                        catch (Exception ex) {
                            ex.printStackTrace();
                        }

                        startActivity(new Intent(DepartmentsActivity.this, WelcomeActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DepartmentsActivity.this,
                                "Unable to sign out. Please check your internet connection then try again.",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }
}
