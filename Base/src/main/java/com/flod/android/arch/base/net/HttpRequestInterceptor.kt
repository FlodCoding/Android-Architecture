package com.flod.android.arch.base.net

import com.flod.android.arch.base.net.log.DefaultHttpLogPrinter
import com.flod.android.arch.base.net.log.HttpLogPrinter
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import okio.GzipSource
import java.io.IOException
import java.nio.charset.Charset
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * SimpleDes:
 * Creator: Flood
 * Date: 2019-03-05
 * UseDes:
 */
class HttpRequestInterceptor @JvmOverloads constructor(var printLevel: Level? = Level.NONE,
                                                       var interceptHandler: InterceptHandler?,
                                                       var httpLogPrinter: HttpLogPrinter? = DefaultHttpLogPrinter()
) : Interceptor {

    init {
        printLevel = printLevel ?: Level.NONE
        httpLogPrinter = httpLogPrinter ?: DefaultHttpLogPrinter()
    }


    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        //从Chain拿到Request
        var request = chain.request()

        val logReq = printLevel == Level.ALL || printLevel == Level.REQUEST
        val logRsp = printLevel == Level.ALL || printLevel == Level.RESPONSE

        request = interceptHandler?.interceptRequest(chain, request) ?: request

        val printer = httpLogPrinter!!
        if (logReq) {
            printer.printRequest(request, requestBodyToStr(request))
        }

        //计算请求所需时间
        val t1 = if (logRsp) System.nanoTime() else 0
        val originalResponse = chain.proceed(request)
        val t2 = if (logRsp) System.nanoTime() else 0
        val chainMs = TimeUnit.NANOSECONDS.toMillis(t2 - t1)


        var rspBodyStr: String? = null

        if (logRsp) {
            //rspBodyStr 只有在有Level.ALL和Level.RESPONSE
            rspBodyStr = responseBodyToStr(originalResponse)
            printer.printResponse(chainMs, originalResponse, rspBodyStr)
        }


        return interceptHandler?.interceptResponse(rspBodyStr, chain, originalResponse)
                ?: originalResponse
    }

    private fun requestBodyToStr(request: Request): String? {
        request.body ?: return null
        if (!isPlaintext(request.body!!.contentType())) return null
        val body = request.newBuilder().build().body ?: return null

        return try {
            val buffer = Buffer()
            body.writeTo(buffer)
            //设置读取的字符集类型
            val charset = getCharset(body.contentType())
            buffer.readString(charset!!)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 从Response获取String
     */
    private fun responseBodyToStr(response: Response): String? {
        response.body ?: return null
        if (!isPlaintext(response.body!!.contentType())) return null

        val responseBody = response.newBuilder().build().body ?: return null


        return try {

            //通过response.body直接读取string会直接关闭流,流只能使用一次
            val source = responseBody.source()
            source.request(Long.MAX_VALUE)
            //拿到Buffer
            var buffer = source.buffer
            //获取content的编码类型（可能是gzip）
            val encoding = response.headers["Content-Encoding"]
            if (encoding != null && encoding.equals("gzip", ignoreCase = true)) {
                GzipSource(buffer.clone()).use { gzippedResponseBody ->
                    buffer = Buffer()
                    buffer.writeAll(gzippedResponseBody)
                }
            }
            //设置读取的字符集类型
            val charset = getCharset(responseBody.contentType())
            buffer.clone().readString(charset!!)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 是否是纯文本格式
     */
    private fun isPlaintext(mediaType: MediaType?): Boolean {
        if (mediaType == null) return false
        if (mediaType.type.toLowerCase(Locale.getDefault()) == "text") {
            return true
        }
        var subtype = mediaType.subtype

        subtype = subtype.toLowerCase(Locale.getDefault())
        return (subtype.contains("x-www-form-urlencoded") || subtype.contains("json") || subtype.contains("xml") || subtype.contains("html")
                || subtype.contains("x-javascript") || subtype.contains("plain"))


    }

    private fun getCharset(contentType: MediaType?): Charset? {
        return if (contentType != null) contentType.charset(Charsets.UTF_8) else Charsets.UTF_8
    }

    enum class Level {
        NONE,     //不打印
        REQUEST,  //只打印请求信息
        RESPONSE, //只打印响应信息
        ALL       //打印全部
    }

}