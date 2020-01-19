package com.flod.android.arch.base.mvvm

import androidx.lifecycle.ViewModel
import com.uber.autodispose.lifecycle.CorrespondingEventsFunction
import com.uber.autodispose.lifecycle.LifecycleEndedException
import com.uber.autodispose.lifecycle.LifecycleScopeProvider
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

/**
 * SimpleDes:
 * Creator: Flood
 * Date: 2019-11-23
 * UseDes: 实现LifecycleScopeProvider，使RxJava在 [BaseViewModel.onCleared]时自动取消订阅
 *
 */
open class BaseViewModel : ViewModel(),
    LifecycleScopeProvider<BaseViewModel.ViewModelLifecycleEvent> {

    enum class ViewModelLifecycleEvent {
        CREATED, CLEARED
    }

    private val lifecycleEvents = BehaviorSubject.createDefault(ViewModelLifecycleEvent.CREATED)

    companion object {
        /**
         * 生命周期事件对：CREATED对应CLEARED
         */
        private val CORRESPONDING_EVENTS = CorrespondingEventsFunction<ViewModelLifecycleEvent> { event ->
            when (event) {
                ViewModelLifecycleEvent.CREATED -> ViewModelLifecycleEvent.CLEARED
                else -> throw LifecycleEndedException(
                    "Cannot bind to ViewModel lifecycle after onCleared."
                )
            }
        }
    }


    override fun lifecycle(): Observable<ViewModelLifecycleEvent> {
        //返回一个可观察的生命周期，并隐藏RxJava的订阅警告
        return lifecycleEvents.hide()
    }

    override fun correspondingEvents(): CorrespondingEventsFunction<ViewModelLifecycleEvent> {
        return CORRESPONDING_EVENTS
    }

    override fun peekLifecycle(): ViewModelLifecycleEvent? {
        return lifecycleEvents.value
    }


    override fun onCleared() {
        lifecycleEvents.onNext(ViewModelLifecycleEvent.CREATED)
        super.onCleared()
    }

}