package com.flod.android.arch.base.net.internal

/**
 * SimpleDes:
 * Creator: Flood
 * Date: 2020-01-21
 * UseDes:
 *
 */
enum class HttpLogLevel {
    NONE,     //不打印
    REQUEST,  //只打印请求信息
    RESPONSE, //只打印响应信息
    ALL       //打印全部
}