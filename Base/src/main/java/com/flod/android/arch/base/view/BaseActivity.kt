package com.flod.android.arch.base.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject

abstract class BaseActivity<VM : ViewModel> : SimpleActivity() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory<VM>
    val viewModel: VM by lazy {
        if (!this::viewModelFactory.isInitialized) {
            throw IllegalStateException("Have you inject Activity?")
        }
        ViewModelProvider(this, viewModelFactory).get(viewModelFactory.getViewModelClass())
    }
}