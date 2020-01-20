@file:Suppress("PropertyName", "ProtectedInFinal")

package com.flod.android.arch.base.mvp

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.flod.android.arch.base.mvp.contract.IModel

/**
 * SimpleDes:
 * Creator: Flood
 * Date: 2019-11-13
 * UseDes:
 *
 */
open class BaseModel : IModel, LifecycleObserver {
    protected val TAG: String by lazy { this.javaClass.simpleName }

    /**
     * 当没有使用Presenter时可以用该方法关联Activity的生命周期
     */
    fun attachLifecycle(owner: LifecycleOwner) {
        owner.lifecycle.addObserver(this)
    }

    override fun onDestroy() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onViewDestroy(owner: LifecycleOwner) {
        onDestroy()
    }

}