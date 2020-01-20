package com.flod.android.arch.base.lifecycle.fuction

import androidx.fragment.app.Fragment
import dagger.android.support.AndroidSupportInjection

/**
 * SimpleDes:为普通的Fragment提供Dagger的功能
 * Creator: Flood
 * Date: 2019-04-24
 * UseDes:需要实现dagger功能的Fragment实现该接口
 */
interface FFragmentInjection {
    fun injectFragment(fragment: Fragment) {
        AndroidSupportInjection.inject(fragment)
    }
}