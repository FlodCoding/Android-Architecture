package com.flod.android.arch.base.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel



abstract class BaseBindingFragment< VM : ViewModel,VDB : ViewDataBinding> : BaseFragment<VM>() {

    protected lateinit var binding: VDB

    override fun inflaterView(inflater: LayoutInflater, container: ViewGroup?): View? {
        binding = DataBindingUtil.inflate(inflater, contentViewId, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }


}