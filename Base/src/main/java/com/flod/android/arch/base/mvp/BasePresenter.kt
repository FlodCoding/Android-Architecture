@file:Suppress("PropertyName", "ProtectedInFinal", "unused", "MemberVisibilityCanBePrivate")

package com.flod.android.arch.base.mvp

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.flod.android.arch.base.mvp.contract.IModel
import com.flod.android.arch.base.mvp.contract.IPresenter
import com.flod.android.arch.base.mvp.contract.IView
import io.reactivex.annotations.Nullable
import javax.inject.Inject

/**
 * SimpleDes:
 * Creator: Flood
 * Date: 2019-11-13
 * UseDes:
 *
 */
abstract class BasePresenter<M : IModel, V : IView> : IPresenter<V>, LifecycleObserver {

    protected val TAG: String by lazy { this.javaClass.simpleName }


    lateinit var mView: V

    @Inject
    @Nullable
    lateinit var mModel: M


    override fun takeView(view: V) {
        this.mView = view
        if (view is LifecycleOwner) {
            val viewLifecycle = (view as LifecycleOwner).lifecycle
            viewLifecycle.addObserver(this)
            if (mModel is LifecycleObserver) {
                viewLifecycle.addObserver(mModel as LifecycleObserver)
            }
        }
    }


    override fun onDestroy() {
        // mModel = null
    }


}