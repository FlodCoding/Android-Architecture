package com.flod.android.arch.base.net;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * SimpleDes:
 * Creator: Flood
 * Date: 2020-01-05
 * UseDes:
 */
public interface InterceptHandler {

    /**
     * 在HTTP请求之前执行，用来添加请求头等
     *
     * @param chain   {@link Interceptor.Chain}
     * @param request {@link Request}
     * @return {@link Request}
     */
    @NonNull
    Request interceptRequest(@NonNull Interceptor.Chain chain, @NonNull Request request);

    @NonNull
    Response interceptResponse(@Nullable String responseBodyStr, @NonNull Interceptor.Chain chain, @NonNull Response response);
}
