@file:Suppress("unused", "SpellCheckingInspection")

package com.flod.android.arch.base.di

import com.flod.android.arch.base.config.HttpSettings
import com.flod.android.arch.base.net.internal.OkHttpInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * SimpleDes:
 * Creator: Flood
 * Date: 2020-01-08
 * UseDes:
 */
@Module
internal class HttpModule {

    @Singleton
    @Provides
     fun provideGson(httpSettings: HttpSettings): Gson {
        val gsonBuilder = GsonBuilder()
        httpSettings.gsonConfig?.invoke(gsonBuilder)
        return gsonBuilder.create()
    }


    @Singleton
    @Provides
    fun provideOkHttpClient(httpSettings: HttpSettings): OkHttpClient {
        val builder = OkHttpClient.Builder()
        httpSettings.okHttpConfig?.invoke(builder)

        builder.addInterceptor(
            OkHttpInterceptor(
                httpSettings.httpLogLevel, httpSettings.httpInterceptor, httpSettings.httpLogPrinter
            )
        )
        builder.build()

        return builder.build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(
        httpSettings: HttpSettings,
        okHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit {
        val builder = Retrofit.Builder()
        builder.addConverterFactory(GsonConverterFactory.create(gson))
        httpSettings.retrofitConfig?.invoke(builder)

        return builder.client(okHttpClient).build()

    }

}


