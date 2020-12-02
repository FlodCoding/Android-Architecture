@file:Suppress("Annotator")

package com.flod.android.arch.base.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject



abstract class BaseFragment<VM : ViewModel> : SimpleFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory<VM>
    val viewModel: VM by lazy {
        ViewModelProvider(if (useActivityViewModel) requireActivity() else this, viewModelFactory)
            .get(viewModelFactory.getViewModelClass())
    }

    protected open val useActivityViewModel = false

}