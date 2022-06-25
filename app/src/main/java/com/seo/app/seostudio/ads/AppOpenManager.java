package com.seo.app.seostudio.ads;

import android.app.Activity;
import android.app.Application;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;


import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.seo.app.seostudio.BuildConfig;
import com.seo.app.seostudio.R;
import com.seo.app.seostudio.activties.App;
import com.seo.app.seostudio.activties.MainActivity;
import com.seo.app.seostudio.billing.billingUtil;
import com.seo.app.seostudio.utils.utils;

import java.util.Date;

import static androidx.lifecycle.Lifecycle.Event.ON_START;

//*Prefetches App Open Ads.

public class AppOpenManager implements LifecycleObserver, Application.ActivityLifecycleCallbacks {
    private static final String LOG_TAG = "AppOpenManager";
    private static final String AD_UNIT_ID = BuildConfig.DEBUG ? utils.Companion.getAppOpenAdID_debug() : utils.Companion.getAppOpenAdID();
    private AppOpenAd appOpenAd = null;

    private AppOpenAd.AppOpenAdLoadCallback loadCallback;

    private final App myApplication;
    private Activity currentActivity;

    private static boolean isShowingAd = false;

    private ConstraintLayout constraintLayout = null;
    private View blackView = null;

    private long loadTime = 0; // to keep track of time because ad expires 4 hours after loading
    private static boolean dontshow = false;

//*Constructor

    public AppOpenManager(App myApplication) {
        this.myApplication = myApplication;
        this.myApplication.registerActivityLifecycleCallbacks(this);//register interface for current activity to listen to all current activity events.
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this); //listen for foregrounding events in your
    }

//*LifecycleObserver methods

    @OnLifecycleEvent(ON_START)
    public void onStart() {
        if (!billingUtil.isPremium) {
            showAdIfAvailable();
        }
        Log.d(LOG_TAG, "onStart");
    }

//*Request an ad

    public void fetchAd() {

        if (!billingUtil.isPremium) {
            // Have unused ad, no need to fetch another.
            if (isAdAvailable()) {
                return;
            }

            loadCallback =
                    new AppOpenAd.AppOpenAdLoadCallback() {
                        //**Called when an app open ad has loaded.**@param ad the loaded app open ad.


                        @Override
                        public void onAdLoaded(@NonNull AppOpenAd appOpenAd) {
                            //   super.onAdLoaded(appOpenAd);
                            AppOpenManager.this.appOpenAd = appOpenAd;
                            AppOpenManager.this.loadTime = (new Date()).getTime();
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // super.onAdFailedToLoad(loadAdError);
                            Log.i("Ads", "onAdFailedToLoad: app open ad ");
                        }
                    };
            try {
                AdRequest request = getAdRequest();
                AppOpenAd.load(
                        myApplication, AD_UNIT_ID, request,
                        AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback);
            } catch (Exception e) {
                Log.i("exception", "fetchAd: $e");
            }
        }
    }


//*Utility method to check if ad was loaded more than n hours ago.

    private boolean wasLoadTimeLessThanNHoursAgo(long numHours) {
        long dateDifference = (new Date()).getTime() - this.loadTime;
        long numMilliSecondsPerHour = 3600000;
        return (dateDifference < (numMilliSecondsPerHour * numHours));
    }

//*Utility method that checks if ad exists and can be shown.

    public boolean isAdAvailable() {
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4);
    }


//*Shows the ad if one isn't already showing.

    public void showAdIfAvailable() {
        if (!billingUtil.isPremium) {
            // Only show ad if there is not already an app open ad currently showing
            // and an ad is available.
            Log.i(LOG_TAG, "showAdIfAvailable:  intertiital shown " + MainActivity.Companion.getIntertitialShown());
            if (!isShowingAd && isAdAvailable() && !MainActivity.Companion.getIntertitialShown()) {
                Log.d(LOG_TAG, "Will show ad.");

                FullScreenContentCallback fullScreenContentCallback =
                        new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Set the reference to null so isAdAvailable() returns false.
                                try {
                                    if (blackView != null || constraintLayout != null) {
                                        constraintLayout.removeView(blackView);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                AppOpenManager.this.appOpenAd = null;
                                isShowingAd = false;
                                fetchAd();
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                isShowingAd = true;
                                try {
                                    blackView = new View(currentActivity);
                                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                                    lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                                    ViewCompat.setElevation(blackView, 50f);
                                    blackView.setElevation(50f);
                                    blackView.setLayoutParams(lp);
                                    blackView.setBackgroundColor(Color.BLACK);
                                    blackView.invalidate();
                                    constraintLayout = (currentActivity).findViewById(R.id.main_layout);
                                    if (constraintLayout != null && blackView != null) {
                                        constraintLayout.addView(blackView);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        };

                appOpenAd.setFullScreenContentCallback(fullScreenContentCallback);
                appOpenAd.show(currentActivity);

            } else {
                Log.d(LOG_TAG, "Can not show ad.");
                fetchAd();
            }
        }
    }


//*Creates and returns ad request .

    private AdRequest getAdRequest() {
        return new AdRequest.Builder().build();
    }


//*Utility method that checks if ad exists and can be shown.

/*    public boolean isAdAvailable() {
        return appOpenAd != null;

    }*/


    //track of current activity
    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        currentActivity = activity;

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        currentActivity = activity;

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        currentActivity = null;
    }
}
