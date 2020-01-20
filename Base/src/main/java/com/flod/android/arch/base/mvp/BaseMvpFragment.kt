package com.flod.android.arch.base.mvp

import android.content.Context
import android.os.Bundle
import androidx.annotation.CallSuper
import com.flod.android.arch.base.mvp.contract.IPresenter
import com.flod.android.arch.base.mvp.contract.IView
import com.flod.android.arch.base.view.SimpleFragment
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

/**
 * SimpleDes:
 * Creator: Flood
 * Date: 2019-11-14
 * UseDes:
 *
 */
abstract class BaseMvpFragment<P : IPresenter<*>> : SimpleFragment(), IView {

    @Inject
    lateinit var mPresenter: P

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)

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