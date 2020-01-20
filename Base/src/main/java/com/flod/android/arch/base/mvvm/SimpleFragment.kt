@file:Suppress("MemberVisibilityCanBePrivate")

package com.flod.android.arch.base.mvvm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment

/**
 * SimpleDes:
 * Creator: Flood
 * Date: 2019-11-14
 * UseDes:
 *
 */
abstract class SimpleFragment : Fragment() {
    @get:LayoutRes
    abstract val contentViewId: Int

    protected var mRootView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mRootView = mRootView ?: inflater.inflate(contentViewId, container, false)
        return mRootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view, savedInstanceState)
        initViewAfter(savedInstanceState)
        initData(savedInstanceState)

    }


    protected abstract fun initView(view: View, savedInstanceState: Bundle?)

    protected open fun initViewAfter(savedInstanceState: Bundle?) {

    }

    protected open fun initData(savedInstanceState: Bundle?) {

    }


    override fun onDestroyView() {
        (mRootView?.parent as ViewGroup?)?.removeAllViews()
        super.onDestroyView()
    }
}