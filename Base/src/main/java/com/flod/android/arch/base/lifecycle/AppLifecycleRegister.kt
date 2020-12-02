@file:Suppress("unused")

package com.flod.android.arch.base.lifecycle

import android.app.Activity
import android.app.Application
import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.flod.android.arch.base.config.LifecycleBuilder
import com.flod.android.arch.base.config.LifecycleInjector
import java.util.*

/**
 * SimpleDes:
 * Creator: Flood
 * Date: 2020-01-19
 * UseDes:
 *
 */
val application: Application
    get() = AppLifecycleRegister.application

internal class AppLifecycleRegister : ContentProvider(), LifecycleObserver {
    companion object {
        lateinit var application: Application
    }

    override fun onCreate(): Boolean {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        return true
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onApplicationCreate() {
        val context = context as Application
        application = context
        if (context is LifecycleInjector) {

            attachLifecycle(context, context.provideLifecycleBuilders())
        }
    }

    private fun attachLifecycle(application: Application, list: List<LifecycleBuilder>?) {
        list ?: return

        val appLifeCallbacks = ArrayList<AppLifecycle>()
        val activityLifeCallbacks = ArrayList<Application.ActivityLifecycleCallbacks>()
        val fragmentLifeCallbacks = ArrayList<FragmentManager.FragmentLifecycleCallbacks>()

        list.forEach {
            it.addAppLifecycle(application, appLifeCallbacks)
            it.addActivityLifecycle(application, activityLifeCallbacks)
            it.addFragmentLifecycle(application, fragmentLifeCallbacks)
        }

        appLifeCallbacks.forEach {
            it.onCreate(application)
        }

        //Add baseActivityLifecycleCallback
        application.registerActivityLifecycleCallbacks(BaseActivityLifecycleCallbacks(fragmentLifeCallbacks))

        //add others
        activityLifeCallbacks.forEach {
            application.registerActivityLifecycleCallbacks(it)
        }


    }


    class BaseActivityLifecycleCallbacks(private val fragmentLifecycleCallbacks: List<FragmentManager.FragmentLifecycleCallbacks>) :
        EmptyActivityLifecycleCallbacks() {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            registerFragmentLifecycle(activity)
        }

        private fun registerFragmentLifecycle(activity: Activity) {
            if (activity is FragmentActivity) {
                for (callback in fragmentLifecycleCallbacks) {
                    activity.supportFragmentManager
                        .registerFragmentLifecycleCallbacks(callback, true)
                }
            }

        }

    }


    //------------------------------- NO used-----------------------------------------------------/
    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        return null
    }


    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        return -1
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        return -1
    }

    override fun getType(uri: Uri): String? {
        return null
    }



}