package com.example.startraders

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.pm.ActivityInfo
import android.os.Bundle

class StarTradersApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            @SuppressLint("SourceLockedOrientationActivity")
            override fun onActivityCreated(activity : Activity, savedInstanceState : Bundle?) {
               activity.requestedOrientation= ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }

            override fun onActivityStarted(activity : Activity) {

            }

            override fun onActivityResumed(activity : Activity) {

            }

            override fun onActivityPaused(activity : Activity) {

            }

            override fun onActivityStopped(activity : Activity) {

            }

            override fun onActivitySaveInstanceState(activity : Activity, outState : Bundle) {

            }

            override fun onActivityDestroyed(activity : Activity) {

            }

        })
    }
}