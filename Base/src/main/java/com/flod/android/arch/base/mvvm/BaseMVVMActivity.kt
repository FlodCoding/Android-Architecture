package com.flod.android.arch.base.mvvm

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.android.AndroidInjection
import javax.inject.Inject

/**
 * SimpleDes:
 * Creator: Flood
 * Date: 2019-11-19
 * UseDes:
 *
 */
abstract class BaseMVVMActivity<VDB : ViewDataBinding, VM : ViewModel> : SimpleActivity() {

    lateinit var mBinding: VDB

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<VM>
    lateinit var mViewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        initViewModel()
    }


    @CallSuper
    protected open fun initViewModel() {
        mViewModel = ViewModelProvider(this, viewModelFactory).get(viewModelFactory.getViewModelClass())
    }


    override fun setContentViewId(layoutResID: Int) {
        mBinding = DataBindingUtil.setContentView(this, contentViewId)
    }


}