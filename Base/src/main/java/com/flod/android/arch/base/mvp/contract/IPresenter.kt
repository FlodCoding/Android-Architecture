package com.flod.android.arch.base.mvp.contract

/**
 * SimpleDes:
 * Creator: Flood
 * Date: 2019-01-22
 * UseDes:
 */
interface IPresenter<in V : IView> {

    fun takeView(view: V)

    fun onDestroy()
}
