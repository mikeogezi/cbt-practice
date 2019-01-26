package com.makerloom.common.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.makerloom.ujcbt.R;
import com.makerloom.common.activity.MyAppCompatActivity;
import com.makerloom.common.startup.SplashActivity;

import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by michael on 3/14/18.
 */

public abstract class UI {
//    private final static float TB_ELEVATION = 20.0f;

    public static void setupToolbar (AppCompatActivity appCompatActivity) {
        setupToolbar(appCompatActivity, true);
    }

    public static Toolbar setupToolbar (AppCompatActivity appCompatActivity, Boolean elevate) {
        Toolbar toolbar = (Toolbar) appCompatActivity.findViewById(R.id.toolbar);

        try {
            toolbar.setTitleTextAppearance(appCompatActivity, R.style.ToolbarTitle);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        if (null != toolbar) {
            appCompatActivity.setSupportActionBar(toolbar);

            ActionBar actionBar = appCompatActivity.getSupportActionBar();
            if (null != actionBar) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeButtonEnabled(true);
            }
        }

        return toolbar;
    }

    private final static int LANDSCAPE_SPAN_COUNT = 2;
    private final static int POTRAIT_SPAN_COUNT = 1;

    public static int getSpanCount (Context context) {
        int orientation = context.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return LANDSCAPE_SPAN_COUNT;
        }
        else {
            return POTRAIT_SPAN_COUNT;
        }
    }

    public static AdRequest getAdRequest (Context context) {
        Bundle extras = new Bundle();
        extras.putString("max_ad_content_rating", "MA"); // G | PG | T | MA

        Location jos = new Location("Jos");
            jos.setLatitude(9.89);
            jos.setLongitude(8.85);

        AdRequest.Builder builder = new AdRequest.Builder()
                .setLocation(jos)
                .setBirthday(new GregorianCalendar(1995, 1, 1).getTime())
                .setGender(AdRequest.GENDER_UNKNOWN)
                .setIsDesignedForFamilies(false)
                .tagForChildDirectedTreatment(false)
                .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                .addKeyword("University")
                .addKeyword("Ecommerce")
                .addKeyword("Sports");

        AdRequest request;
        request = builder.build();

        return request;
    }

    public static void loadFooterBannerAd (final Context context, final @Nullable View adLayout,
                                           final @NonNull AdView adView, @Nullable final TextView adInfo) {
        loadFooterBannerAd(context, adLayout, adView, adInfo, false);
    }

    public static void loadFooterBannerAd (final Context context, final @Nullable View adLayout,
                                           final @NonNull AdView adView, @Nullable final TextView adInfo, final boolean showLoading) {
        if (null != adInfo) {
            adInfo.setVisibility(View.VISIBLE);
            adInfo.setText("Advert loading ...");
        }

        final LinearLayout reportLayout = ((MyAppCompatActivity) context).findViewById(R.id.report_layout);
        if (null != reportLayout) {
            removeAdPaddingBottom(reportLayout);
        }

        MobileAds.initialize(context, context.getString(R.string.app_admob_id));

        final AdRequest adRequest = getAdRequest(context);
        final AdListener bannerAdListener = new AdListener() {
            @Override
            public void onAdClicked() {
                super.onAdClicked();
                if (Constants.VERBOSE) {
                    Toast.makeText(context, "Ad clicked", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                if (Constants.VERBOSE) {
                    Toast.makeText(context, "Ad closed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                if (null != adInfo) {
                    adInfo.setText("Ad failed to load");
                }
                if (Constants.VERBOSE) {
                    Toast.makeText(context, "Ad failed to load", Toast.LENGTH_SHORT).show();
                }
                if (null != adLayout) {
                    adLayout.setVisibility(View.GONE);
                }
                if (null != reportLayout) {
                    removeAdPaddingBottom(reportLayout);
                }
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
                if (Constants.VERBOSE) {
                    Toast.makeText(context, "Ad impression", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
                if (Constants.VERBOSE) {
                    Toast.makeText(context, "Ad left application", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                adView.setVisibility(View.VISIBLE);
                if (null != adLayout) {
                    adLayout.setVisibility(View.VISIBLE);
                }
                if (null != adInfo) {
                    adInfo.setVisibility(View.INVISIBLE);
                }
                if (Constants.VERBOSE) {
                    Toast.makeText(context, "Ad loaded", Toast.LENGTH_SHORT).show();
                }
                if (null != reportLayout) {
                    applyAdPaddingBottom(reportLayout);
                }
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
                if (Constants.VERBOSE) {
                    Toast.makeText(context, "Ad opened", Toast.LENGTH_SHORT).show();
                }
            }
        };
        
        adView.loadAd(adRequest);
        adView.setAdListener(bannerAdListener);
        if (showLoading && null != adLayout) {
            adLayout.setVisibility(View.VISIBLE);
        }
    }

    private static void applyAdPaddingBottom(View view) {
        if (null != view) {
            int top, bottom, left, right;
            top = view.getPaddingTop();
            bottom = view.getPaddingBottom();
            left = view.getPaddingLeft();
            right = view.getPaddingRight();
            final float newBottom = view.getContext().getResources().getDimension(R.dimen.ad_bottom_padding);
            view.setPadding(left, top, right, (int) newBottom);
        }
    }

    private static void removeAdPaddingBottom(View view) {
        if (null != view) {
            int top, bottom, left, right;
            top = view.getPaddingTop();
            bottom = view.getPaddingBottom();
            left = view.getPaddingLeft();
            right = view.getPaddingRight();
            final float newBottom = view.getContext().getResources().getDimension(R.dimen.layout_padding);
            view.setPadding(left, top, right, (int) newBottom);
        }
    }

    public static void sendNotification (Context context, @Nullable String title, @Nullable String messageBody) {
        Intent intent = new Intent(context, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = context.getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(!TextUtils.isEmpty(title) ? title : String.format(Locale.ENGLISH, "%s", context.getString(R.string.app_name)))
                .setContentText(!TextUtils.isEmpty(messageBody) ? messageBody : context.getString(R.string.default_notification_message_body))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    String.format(Locale.ENGLISH, "%s", context.getString(R.string.app_name)),
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, notificationBuilder.build());
    }

    public static float spotlightPadding = 0.5f;

    private static int CLICK_ACTION_THRESHOLD = 200;

    public static boolean isClick (float startX, float endX, float startY, float endY) {
        float dX = Math.abs(startX - endX);
        float dY = Math.abs(startY - endY);
        return !(dX > CLICK_ACTION_THRESHOLD || dY > CLICK_ACTION_THRESHOLD);
    }

    public static boolean isClick (long lastTouchDown) {
        return System.currentTimeMillis() - lastTouchDown < CLICK_ACTION_THRESHOLD;
    }

    public static void clickOnScreen (MyAppCompatActivity activity, float x, float y) {
        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis();

        MotionEvent firstEvent = MotionEvent.obtain(downTime, eventTime,
                MotionEvent.ACTION_DOWN, x, y, 0);
        MotionEvent secondEvent = MotionEvent.obtain(downTime, eventTime,
                MotionEvent.ACTION_UP, x, y, 0);

        try {
            activity.dispatchTouchEvent(firstEvent);
            activity.dispatchTouchEvent(secondEvent);
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }
}