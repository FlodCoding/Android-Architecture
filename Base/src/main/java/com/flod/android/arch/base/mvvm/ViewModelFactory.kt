package com.flod.android.arch.base.mvvm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Lazy
import javax.inject.Inject

/**
 * SimpleDes:
 * Creator: Flood
 * Date: 2019-11-24
 * UseDes:
 *
 */
@Suppress("UNCHECKED_CAST")
class ViewModelFactory<T : ViewModel> @Inject constructor(private val viewModel: Lazy<T>) :
    ViewModelProvider.Factory {

    fun getViewModelClass():Class<T>{
        return viewModel.get().javaClass
    }

    override fun <T : ViewModel?> create(modelClass: Class<T>): T = viewModel.get() as T
}