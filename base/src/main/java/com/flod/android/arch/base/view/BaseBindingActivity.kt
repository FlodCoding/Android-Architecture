package com.flod.android.arch.base.view

import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel


abstract class BaseBindingActivity<VM : ViewModel, VDB : ViewDataBinding> : BaseActivity<VM>() {

    protected lateinit var binding: VDB

    override fun setContentView() {
        binding = DataBindingUtil.setContentView(this, contentViewId)
        binding.lifecycleOwner = this
    }

}