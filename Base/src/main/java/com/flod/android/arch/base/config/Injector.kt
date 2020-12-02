package com.flod.android.arch.base.config

/**
 * SimpleDes:
 * Creator: Flood
 * Date: 2020-01-19
 * UseDes:
 *
 */
interface LifecycleInjector {
    fun provideLifecycleBuilders(): List<LifecycleBuilder>?
}

interface HttpConfigInjector {
    fun provideHttpConfig(): HttpConfigBuilder?
}