@file:Suppress("MemberVisibilityCanBePrivate")

package com.flod.android.arch.base.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment


abstract class SimpleFragment : Fragment() {
    @get:LayoutRes
    abstract val contentViewId: Int


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflaterView(inflater, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view, savedInstanceState)
        initData(savedInstanceState)
    }

    protected open fun inflaterView(inflater: LayoutInflater, container: ViewGroup?): View? {
        return inflater.inflate(contentViewId, container, false)
    }


    protected abstract fun initView(view: View, savedInstanceState: Bundle?)


    protected open fun initData(savedInstanceState: Bundle?) {

    }
}