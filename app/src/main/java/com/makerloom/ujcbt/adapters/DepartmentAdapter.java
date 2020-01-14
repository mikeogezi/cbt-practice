package com.makerloom.ujcbt.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.makerloom.ujcbt.R;
import com.makerloom.common.activity.MyAppCompatActivity;
import com.makerloom.common.utils.Constants;
import com.makerloom.common.utils.Keys;
import com.makerloom.common.utils.UI;
import com.makerloom.ujcbt.holders.DepartmentHolder;
import com.makerloom.ujcbt.models.Department;
import com.takusemba.spotlight.CustomTarget;
import com.takusemba.spotlight.OnSpotlightEndedListener;
import com.takusemba.spotlight.OnSpotlightStartedListener;
import com.takusemba.spotlight.Spotlight;

import java.util.List;
import java.util.Locale;

/**
 * Created by michael on 4/11/18.
 */

public class DepartmentAdapter extends RecyclerView.Adapter<DepartmentHolder> {
    private List<Department> departments;
    private Context context;

    public DepartmentAdapter (Context context, List<Department> departments) {
        this.context = context;
        this.departments = departments;
    }

    @Override
    public DepartmentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.department_card, null);
        return new DepartmentHolder(view, context);
    }

    @Override
    public void onBindViewHolder(final DepartmentHolder holder, int position) {
        Department department = departments.get(position);

        holder.setDepartment(department);

        holder.facultyNameTV.setText(department.getFaculty());
        holder.departmentNameTV.setText(department.getName());
        holder.cardView.setOnClickListener(holder);

//        TextDrawable drawable = TextDrawable.builder()
//                .beginConfig()
//                .textColor(ContextCompat.getColor(context, R.color.white))
//                .useFont(Typeface.createFromAsset(context.getAssets(), "fonts/Aller/aller_bd.ttf"))
//                .bold()
//                .toUpperCase()
//                .fontSize(60)
//                .endConfig()
//                .buildRect(department.getShortName(), ColorGenerator.MATERIAL.getColor(department.getName()));
        holder.shortNameTV.setText(department.getShortName());
        holder.shortNameCV.setCardBackgroundColor(ColorGenerator.MATERIAL.getColor(department.getName()));

        if (position == 0) {
            setSpotlightRunnable(holder);
        }

        if (position == departments.size() - 1) {
            runSpotlightRunnable();
        }
    }

    @Override
    public int getItemCount() {
        return departments.size();
    }

    private Runnable spotlightRunnable;

    CustomTarget target;

    public void hideSpotlight () {
        if (null != target) {
            target.closeTarget();
        }
    }

    private void setSpotlightRunnable (final DepartmentHolder holder) {
        spotlightRunnable = new Runnable() {
            @Override
            public void run() {
                int[] point = new int[2];
                holder.shortNameTV.getLocationOnScreen(point);
                int max = Math.max(holder.shortNameTV.getWidth(), holder.shortNameTV.getHeight());

                target = new CustomTarget.Builder((Activity) context)
                        .setPoint(point[0] + holder.shortNameTV.getWidth() / 2.0f,
                                point[1] + holder.shortNameTV.getHeight() / 2.0f)
                        .setRadius((max + UI.spotlightPadding * max) / 2.0f)
                        // .setRadius(130.0f)
                        .setView(R.layout.spotlight_department)
                        .build();

                target.getView().setOnTouchListener(new View.OnTouchListener() {
                    long lastTouchDown;

                    @Override
                    public boolean onTouch(final View v, final MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                lastTouchDown = System.currentTimeMillis();
                                break;
                            case MotionEvent.ACTION_UP:
                                if (UI.isClick(lastTouchDown)) {
                                    DepartmentAdapter.this.event = event;
                                    if (Constants.VERBOSE) {
                                        Toast.makeText(context, String.format(Locale.ENGLISH, "Click at %.2f, %.2f", event.getX(), event.getY()), Toast.LENGTH_LONG)
                                                .show();
                                    }
                                }
                                break;
                        }
                        target.closeTarget();
                        return true;
                    }
                });

                final MyAppCompatActivity activity = (MyAppCompatActivity) context;

                Spotlight.with((Activity) context)
                        .setOverlayColor(ContextCompat.getColor(context, R.color.transparent_bg))
                        .setDuration(5L)
                        .setAnimation(new DecelerateInterpolator(2.0f))
                        .setTargets(target)
                        .setClosedOnTouchedOutside(true)
                        .setOnSpotlightStartedListener(new OnSpotlightStartedListener() {
                            @Override
                            public void onStarted() {
                                activity.isSpotlightShowing = true;
                            }
                        })
                        .setOnSpotlightEndedListener(new OnSpotlightEndedListener() {
                            @Override
                            public void onEnded() {
                                if (activity.isSpotlightShowing && null != event) {
                                    if (Constants.VERBOSE) {
                                        Toast.makeText(context, "Clicking on screen", Toast.LENGTH_LONG)
                                                .show();
                                    }
                                    UI.clickOnScreen((MyAppCompatActivity) context, event.getX(), event.getY());
                                }
                                activity.isSpotlightShowing = false;
                            }
                        })
                        .start();
            }
        };
    }

    private MotionEvent event = null;

    private void runSpotlightRunnable () {
        if (null != spotlightRunnable) {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    SharedPreferences preferences = PreferenceManager
                            .getDefaultSharedPreferences(context);

                    Boolean isFirstDepts = preferences.getBoolean(Keys.IS_FIRST_DEPARTMENTS_KEY, true);
                    // isFirstDepts = true;

                    if (isFirstDepts || Constants.SPOTLIGHT_DEBUG) {
                        ((MyAppCompatActivity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                spotlightRunnable.run();
                            }
                        });

                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                setNotFirstDepts();
                            }
                        });
                    }
                }
            });
        }
    }

    private void setNotFirstDepts () {
        SharedPreferences.Editor editor = PreferenceManager
                .getDefaultSharedPreferences(context).edit();
        editor.putBoolean(Keys.IS_FIRST_DEPARTMENTS_KEY, false);
        editor.apply();
    }
}
