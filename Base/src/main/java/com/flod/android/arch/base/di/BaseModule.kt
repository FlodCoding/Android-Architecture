@file:Suppress("unused")

package com.flod.android.arch.base.di

import android.app.Application
import com.flod.android.arch.base.config.HttpConfigBuilder
import com.flod.android.arch.base.config.HttpSettings
import dagger.Module
import dagger.Provides
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

/**
 * SimpleDes:
 * Creator: Flood
 * Date: 2020-01-08
 * UseDes:
 *
 */
@Module(includes = [AndroidInjectionModule::class,  HttpModule::class])
internal class BaseModule {

    @Singleton
    @Provides
    fun provideHttpSettings(application: Application, httpConfigBuilder: HttpConfigBuilder): HttpSettings {
        val httpSettings = HttpSettings()
        httpConfigBuilder.build(application, httpSettings)
        return httpSettings
    }


}