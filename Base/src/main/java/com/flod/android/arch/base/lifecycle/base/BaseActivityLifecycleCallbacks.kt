package com.flod.android.arch.base.lifecycle.base

import android.app.Activity
import android.app.Application
import android.os.Bundle

import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.flod.android.arch.base.lifecycle.fuction.FActivityInjection

/**
 * SimpleDes:
 * Creator: Flood
 * Date: 2019-05-05
 * UseDes:
 */
internal class BaseActivityLifecycleCallbacks(private val fragmentLifecycleCallbacks: List<FragmentManager.FragmentLifecycleCallbacks>) :
    Application.ActivityLifecycleCallbacks {

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        registerFragmentLifecycle(activity)

        injectActivity(activity)

    }

    private fun injectActivity(activity: Activity) {
        if (activity is FActivityInjection) {
            (activity as FActivityInjection).injectActivity(activity)
        }
    }


    override fun onActivityStarted(activity: Activity) {

    }

    override fun onActivityResumed(activity: Activity) {

    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStopped(activity: Activity) {

    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {

    }


    private fun registerFragmentLifecycle(activity: Activity) {
        if (activity is FragmentActivity) {
            activity.supportFragmentManager.registerFragmentLifecycleCallbacks(
                BaseFragmentLifecycleCallbacks(),
                true
            )
            for (callback in fragmentLifecycleCallbacks) {
                activity.supportFragmentManager
                    .registerFragmentLifecycleCallbacks(callback, true)
            }
        }

    }

}
