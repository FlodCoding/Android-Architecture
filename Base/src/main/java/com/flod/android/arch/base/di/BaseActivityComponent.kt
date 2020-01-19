package com.flod.android.arch.base.di

import android.app.Activity
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent
interface BaseActivityComponent : AndroidInjector<Activity> {
    @Subcomponent.Factory
    abstract class Builder : AndroidInjector.Factory<Activity>
}