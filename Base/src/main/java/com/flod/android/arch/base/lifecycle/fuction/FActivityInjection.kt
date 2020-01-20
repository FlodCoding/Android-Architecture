package com.flod.android.arch.base.lifecycle.fuction

import android.app.Activity
import dagger.android.AndroidInjection

/**
 * SimpleDes:为普通的Activity提供Dagger的功能
 * Creator: Flood
 * Date: 2019-04-24
 * UseDes:需要实现dagger功能的Activity实现该接口
 */
interface FActivityInjection {
    fun injectActivity(activity: Activity) {
        AndroidInjection.inject(activity)
    }
}