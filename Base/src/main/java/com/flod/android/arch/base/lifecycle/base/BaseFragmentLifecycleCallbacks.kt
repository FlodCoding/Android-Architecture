package com.flod.android.arch.base.lifecycle.base

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.flod.android.arch.base.lifecycle.fuction.FFragmentInjection


/**
 * SimpleDes:
 * Creator: Flood
 * Date: 2019-12-12
 * UseDes:
 *
 */
internal class BaseFragmentLifecycleCallbacks : FragmentManager.FragmentLifecycleCallbacks() {

    override fun onFragmentPreAttached(fm: FragmentManager, f: Fragment, context: Context) {
        super.onFragmentPreAttached(fm, f, context)
        injectionFragment(f)
    }


    private fun injectionFragment(f: Fragment) {
        if (f is FFragmentInjection) {
            (f as FFragmentInjection).injectFragment(f)
        }
    }
}