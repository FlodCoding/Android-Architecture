package com.flod.android.arch.base.config


interface LifecycleInjector {
    fun provideLifecycleBuilders(): List<LifecycleBuilder>?
}

interface HttpConfigInjector {
    fun provideHttpConfig(): HttpConfigBuilder?
}