@file:Suppress("unused")

package com.flod.android.arch.base.utils

import android.view.View
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

/**
 * SimpleDes:
 * Creator: Flood
 * Date: 2020-02-11
 * UseDes:
 *
 */

fun View.setOnSingleClickListener(listener: ((View) -> Unit)?) {
    setOnSingleClickListener(1000, listener)
}

fun View.setOnSingleClickListener(interval: Long, listener: ((View) -> Unit)?) {
    if (listener == null) {
        setOnClickListener(null)
        return
    }

    val disposable = Observable.create<View> {
        setOnClickListener(it::onNext)
    }.throttleFirst(interval, TimeUnit.MILLISECONDS)
            .subscribe(listener::invoke)

    addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
        override fun onViewDetachedFromWindow(v: View) {
            disposable.dispose()
        }

        override fun onViewAttachedToWindow(v: View?) {
        }

    })

}