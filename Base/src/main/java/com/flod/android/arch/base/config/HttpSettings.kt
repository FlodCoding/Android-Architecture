@file:Suppress("SpellCheckingInspection")

package com.flod.android.arch.base.config

import com.flod.android.arch.base.net.HttpInterceptor
import com.flod.android.arch.base.net.HttpLogPrinter
import com.flod.android.arch.base.net.internal.HttpLogLevel
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit

/**
 * SimpleDes:
 * Creator: Flood
 * Date: 2020-01-19
 * UseDes:
 *
 */
data class HttpSettings(
    var retrofitConfig: ((Retrofit.Builder) -> Unit)? = null,
    var okHttpConfig: ((OkHttpClient.Builder) -> Unit)? = null,
    var gsonConfig: ((GsonBuilder) -> Unit)? = null,

    var httpLogLevel: HttpLogLevel? = null,
    var httpInterceptor: HttpInterceptor? = null,
    var httpLogPrinter: HttpLogPrinter? = null
)