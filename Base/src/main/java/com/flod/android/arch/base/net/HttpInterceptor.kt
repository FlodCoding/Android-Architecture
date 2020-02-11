package com.flod.android.arch.base.net

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

/**
 * SimpleDes:
 * Creator: Flood
 * Date: 2019-03-05
 * UseDes:
 */
interface HttpInterceptor {
    /**
     * 在HTTP请求之前拦截，可用来添加请求头，加密等
     */
    fun onInterceptRequest(chain: Interceptor.Chain, request: Request): Request

    /**
     * 在HTTP响应后拦截，可预先对Response进行处理
     *
     * @param responseBodyStr 以
     * [com.flod.android.arch.base.net.internal.HttpLogLevel.RESPONSE]或
     * [com.flod.android.arch.base.net.internal.HttpLogLevel.ALL]
     * 为前提，才会有数据
     */
    fun onInterceptResponse(responseBodyStr: String?, chain: Interceptor.Chain, response: Response): Response
}
