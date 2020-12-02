package com.flod.android.arch.base.lifecycle

import android.app.Activity
import android.app.Application
import android.os.Bundle

/**
 * SimpleDes:
 * Creator: Flood
 * Date: 2020-08-14
 * UseDes:
 *
 */
open class EmptyActivityLifecycleCallbacks : Application.ActivityLifecycleCallbacks {

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    }

    override fun onActivityResumed(activity: Activity) {
    }
}