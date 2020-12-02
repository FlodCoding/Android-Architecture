@file:Suppress("unused", "SpellCheckingInspection")

package com.flod.android.arch.base.di

import android.app.Application
import com.flod.android.arch.base.config.HttpConfigInjector
import com.flod.android.arch.base.config.HttpSettings
import com.flod.android.arch.base.net.log.DefaultHttpLogPrinter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

/**
 * SimpleDes:
 * Creator: Flood
 * Date: 2019-11-06
 * UseDes:
 */

@Module
@InstallIn(ApplicationComponent::class)
object HttpModule {

    @Singleton
    @Provides
    fun provideHttpSettings(application: Application): HttpSettings {
        val httpSettings = HttpSettings()
        if (application is HttpConfigInjector) {
            val config = application.provideHttpConfig()
            config?.applyConfig(application, httpSettings)
        }
        return httpSettings
    }


    @Singleton
    @Provides
    internal fun provideGson(httpSettings: HttpSettings): Gson {
        val gsonBuilder = GsonBuilder()
        httpSettings.gsonConfig?.invoke(gsonBuilder)
        return gsonBuilder.create()
    }


    @Singleton
    @Provides
    internal fun provideOkHttpClient(httpSettings: HttpSettings): OkHttpClient {
        val builder = OkHttpClient.Builder()
        httpSettings.okHttpConfig?.invoke(builder)
        builder.apply {
            if (httpSettings.logLevel != DefaultHttpLogPrinter.Level.NONE)
                addInterceptor(DefaultHttpLogPrinter(httpSettings.logLevel))
        }
        return builder.build()
    }

    @Singleton
    @Provides
    internal fun provideRetrofit(httpSettings: HttpSettings, okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        val builder = Retrofit.Builder()
        builder.addConverterFactory(GsonConverterFactory.create(gson))
        httpSettings.retrofitConfig?.invoke(builder)
        return builder.client(okHttpClient).build()
    }
}


