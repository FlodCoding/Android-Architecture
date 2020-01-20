package com.flod.android.arch.base.mvp

import android.os.Bundle
import androidx.annotation.CallSuper
import com.flod.android.arch.base.mvp.contract.IPresenter
import com.flod.android.arch.base.mvp.contract.IView
import com.flod.android.arch.base.view.SimpleActivity

import dagger.android.AndroidInjection
import javax.inject.Inject

/**
 * SimpleDes:
 * Creator: Flood
 * Date: 2019-11-13
 * UseDes:
 *
 */
abstract class BaseMvpActivity<P : IPresenter<*>> : SimpleActivity(), IView {

    @Inject
    lateinit var mPresenter: P


    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
    }


    @CallSuper
    override fun initViewAfter(savedInstanceState: Bundle?) {
        @Suppress("UNCHECKED_CAST")
        (mPresenter as IPresenter<IView>?)?.takeView(this)
    }


    override fun onDestroy() {
        super.onDestroy()
        mPresenter.onDestroy()

    }


}