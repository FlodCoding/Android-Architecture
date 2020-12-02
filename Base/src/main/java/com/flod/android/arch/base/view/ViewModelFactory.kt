package com.flod.android.arch.base.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Lazy
import javax.inject.Inject


@Suppress("UNCHECKED_CAST")
class ViewModelFactory<T : ViewModel> @Inject constructor(private val viewModel: Lazy<T>) :
    ViewModelProvider.Factory {

    fun getViewModelClass():Class<T>{
        return viewModel.get().javaClass
    }

    override  fun <T : ViewModel?> create(modelClass: Class<T>): T = viewModel.get() as T
}