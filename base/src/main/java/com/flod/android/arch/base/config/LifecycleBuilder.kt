package com.flod.android.arch.base.config

import android.app.Application
import android.content.Context
import androidx.fragment.app.FragmentManager

import com.flod.android.arch.base.lifecycle.AppLifecycle


interface LifecycleBuilder {

    fun addAppLifecycle(context: Context, appLifecycleCallbacks: MutableList<AppLifecycle>)

    fun addActivityLifecycle(context: Context, activityLifeCallbacks: MutableList<Application.ActivityLifecycleCallbacks>)

    fun addFragmentLifecycle(context: Context, fragmentLifeCallbacks: MutableList<FragmentManager.FragmentLifecycleCallbacks>)
}
