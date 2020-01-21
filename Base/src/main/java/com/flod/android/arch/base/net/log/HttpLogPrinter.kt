package com.flod.android.arch.base.net.log

import okhttp3.Request
import okhttp3.Response

/**
 * SimpleDes: Http实现数据打印的接口
 * Creator: Flood
 * Date: 2019-03-06
 * UseDes:需要自定义打印格式的可以实现该接口，默认则使用[DefaultHttpLogPrinter]
 */
interface HttpLogPrinter {

    fun printRequest(request: Request, bodyString: String?)

    fun printResponse(chainMs: Long, response: Response, bodyString: String?)
}