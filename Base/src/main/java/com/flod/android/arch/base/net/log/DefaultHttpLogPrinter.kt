@file:Suppress("unused", "SpellCheckingInspection")

package com.flod.android.arch.base.net.log

import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import okio.GzipSource
import java.io.IOException
import java.nio.charset.Charset
import java.util.*

/**
 * SimpleDes:
 * Creator: Flood
 * Date: 2019-03-14
 * UseDes:
 */

class DefaultHttpLogPrinter(
    private val logLevel: Level = Level.NONE,
    private val formatBody: Boolean = true
) : Interceptor {

    enum class Level {
        NONE,     //不打印
        REQUEST,  //只打印请求信息
        RESPONSE, //只打印响应信息
        ALL       //打印全部
    }

    companion object {

        private const val TAG = "HttpLogPrinter"
        private val LogTagExtras =
            arrayOf("-\uD83D\uDE03-", "-\uD83D\uDE11-", "-\uD83D\uDE33-", "-\uD83D\uDE06-")
        private const val BOUND_REQUEST_TOP =
            "────── Request ────────────────────────────────────────────────────────────────────────────────────────────"
        private const val BOUND_RESPONSE_TOP =
            "────── Response ───────────────────────────────────────────────────────────────────────────────────────────"
        private const val BOUND_CENTER_LEFT = "│ "
        private const val BOUND_BOTTOM =
            "───────────────────────────────────────────────────────────────────────────────────────────────────────────"
        private const val BOUND_CORNER_TOP = "┌"
        private const val BOUND_CORNER_CENTER = "├"
        private const val BOUND_CORNER_BOTTOM = "└"
        private const val MARK_URL = "URL: "
        private const val MARK_METHOD = "Method: @"
        private const val MARK_HEADERS = "Headers: "
        private const val MARK_BODY = "Body: "
        private const val MARK_STATUS_CODE = "Code: "
        private const val MARK_TIME = "Time: "
        private const val N = "\n"
    }


    private var tagIndex: Int = 0


    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val logReq = logLevel == Level.ALL || logLevel == Level.REQUEST
        val logRsp = logLevel == Level.ALL || logLevel == Level.RESPONSE

        if (logReq)
            logRequest(request, requestBodyToStr(request))

        val t1 = System.currentTimeMillis()
        val response = chain.proceed(request)
        val t2 = System.currentTimeMillis()
        val chainMs = t2 - t1

        if (logRsp) {
            logResponse(chainMs, response, responseBodyToStr(response))
        }

        return response
    }


    private fun logRequest(request: Request, bodyString: String?) {

        val tag = "$TAG-Request"
        logMulti(
            tag,
            BOUND_REQUEST_TOP,
            MARK_URL + request.url,
            MARK_METHOD + request.method,
            N,
            MARK_HEADERS,
            formatHeaders(request.headers.toString()),
            MARK_BODY,
            formatBody(bodyString),
            BOUND_BOTTOM
        )
    }

    private fun logResponse(chainMs: Long, response: Response, bodyString: String?) {
        val tag = "$TAG-Response"
        logMulti(
            tag,
            BOUND_RESPONSE_TOP,
            MARK_URL + response.request.url,
            MARK_METHOD + response.request.method,
            MARK_STATUS_CODE + response.code + " / " + response.message,
            MARK_TIME + chainMs + "ms",
            N,
            MARK_HEADERS,
            formatHeaders(response.headers.toString()),
            MARK_BODY,
            formatBody(bodyString),
            BOUND_BOTTOM
        )
    }


    private fun log(tag: String, string: String) {
        Log.d(getTag(tag), string)
    }

    /**
     * 执行多条打印
     *
     * @param tag     传入日志的tag，在tag前会额外添加多余的字符，为了防止日志被合并成一条
     * @param strings 传入的String array
     */
    private fun logMulti(tag: String, vararg strings: String) {

        for (i in strings.indices) {
            val extra = when (i) {
                0 -> {
                    BOUND_CORNER_TOP
                }
                strings.size - 1 -> {
                    BOUND_CORNER_BOTTOM
                }
                else -> {
                    BOUND_CENTER_LEFT
                }
            }
            val childStr = strings[i].split(N).toTypedArray()
            if (childStr.isNotEmpty()) {
                for (s in childStr) {
                    log(tag, extra + s)
                }
                continue
            }
            log(tag, extra + strings[i])
        }

    }

    private fun formatHeaders(str: String): String {
        if (str.isBlank()) return "Empty"

        val headers = str.split(N).toMutableList()
        headers.removeAt(headers.size - 1)
        val builder = StringBuilder()
        var extra: String
        if (headers.size > 1) {
            for (i in headers.indices) {
                extra = when (i) {
                    0 -> {
                        BOUND_CORNER_TOP
                    }
                    headers.size - 1 -> {
                        BOUND_CORNER_BOTTOM
                    }
                    else -> {
                        BOUND_CORNER_CENTER
                    }
                }
                builder.append(extra).append(" ").append(headers[i]).append(N)
            }
        } else {
            builder.append("• ").append(str)
        }
        return builder.toString()
    }

    @Suppress("ConstantConditionIf")
    private fun formatBody(bodyString: String?): String {

        if (bodyString.isNullOrBlank()) return "Omitted Body"

        return if (formatBody && bodyString.startsWith("{")) {

            return kotlin.runCatching {
                val gson =
                    GsonBuilder().setPrettyPrinting().disableHtmlEscaping().setLenient().create()
                val jsonElement = JsonParser.parseString(bodyString)
                gson.toJson(jsonElement)
            }.getOrElse {
                log("$TAG-FormatErr", it.stackTraceToString())
                bodyString
            }
        } else bodyString
    }

    /**
     * 在快速连续打印时日志会把多条合并成一条，导致日志不能对齐，解决方案时是不断改变每条log的tag
     */
    private fun getTag(tag: String): String {
        if (tagIndex > LogTagExtras.size - 1) {
            tagIndex = 0
        }
        val extra = LogTagExtras[tagIndex]
        tagIndex++
        return extra + tag
    }


    private fun requestBodyToStr(request: Request): String? {

        if (!isPlaintext(request.body?.contentType())) return null
        val body = request.newBuilder().build().body ?: return null

        return try {
            val buffer = Buffer()
            body.writeTo(buffer)
            //设置读取的字符集类型
            val charset = getCharset(body.contentType())
            buffer.readString(charset)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }


    /**
     * 从Response获取String
     */
    private fun responseBodyToStr(response: Response): String? {
        val body = response.body ?: return null

        if (!isPlaintext(body.contentType())) return null

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
            buffer.clone().readString(charset)
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
        val subtype = mediaType.subtype.toLowerCase(Locale.getDefault())
        return subtype.contains("form")
                || subtype.contains("json")
                || subtype.contains("xml")
                || subtype.contains("html")
                || subtype.contains("x-javascript")
                || subtype.contains("plain")


    }

    private fun getCharset(contentType: MediaType?): Charset {
        return contentType?.charset(Charsets.UTF_8) ?: Charsets.UTF_8
    }
}