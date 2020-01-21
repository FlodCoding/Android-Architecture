@file:Suppress("unused")

package com.flod.android.arch.base.net.log

import android.text.TextUtils
import android.util.Log
import com.flod.android.arch.base.utils.JsonFormatUtil
import okhttp3.Request
import okhttp3.Response
import java.util.concurrent.Executors

/**
 * SimpleDes:
 * Creator: Flood
 * Date: 2019-03-14
 * UseDes:
 */

class DefaultHttpLogPrinter : HttpLogPrinter {
    override fun printRequest(request: Request, bodyString: String?) {
        logRequest(request, bodyString)
    }


    override fun printResponse(chainMs: Long, response: Response, bodyString: String?) {
        logResponse(chainMs, response, bodyString)
    }

    companion object {

        private const val TAG = "HttpLogPrinter"
        private val LogTagExtras = arrayOf("-\uD83D\uDE03-", "-\uD83D\uDE11-", "-\uD83D\uDE33-", "-\uD83D\uDE06-")
        private const val BOUND_REQUEST_TOP = "────── Request ────────────────────────────────────────────────────────────────────────────────────────────"
        private const val BOUND_RESPONSE_TOP = "────── Response ───────────────────────────────────────────────────────────────────────────────────────────"
        private const val BOUND_CENTER_LEFT = "│ "
        private const val BOUND_BOTTOM = "───────────────────────────────────────────────────────────────────────────────────────────────────────────"
        private const val BOUND_CORNER_TOP = "┌"
        private const val BOUND_CORNER_CENTER = "├"
        private const val BOUND_CORNER_BOTTOM = "└"
        private const val MARK_URL = "URL: "
        private const val MARK_METHOD = "Method: @"
        private const val MARK_HEADERS = "Headers: "
        private const val MARK_BODY = "Body: "
        private const val MARK_STATUS_CODE = "Status Code: "
        private const val MARK_TIME = "Time: "
        private const val N = "\n"
    }


    private var formatBody = true        //是否格式化jsonBody
    private var removeBackslash = false  //去除反斜杠
    private var tagIndex: Int = 0

    //用单线程池执行来打印多条Log，防止两个不同线程Response重叠到一起
    private val singleThreadExecutor = Executors.newSingleThreadExecutor()

    private fun logRequest(request: Request, bodyString: String?) {

        val tag = "$TAG-Request"
        logMulti(tag, BOUND_REQUEST_TOP,
                MARK_URL + request.url,
                MARK_METHOD + request.method,
                N,
                MARK_HEADERS,
                formatHeaders(request.headers.toString()),
                N,
                MARK_BODY,
                formatBody(bodyString)!!,
                BOUND_BOTTOM
        )
    }

    private fun logResponse(chainMs: Long, response: Response, bodyString: String?) {
        val tag = "$TAG-Response"
        logMulti(tag, BOUND_RESPONSE_TOP,
                MARK_URL + response.request.url,
                MARK_METHOD + response.request.method,
                MARK_STATUS_CODE + response.code + " / " + response.message,
                MARK_TIME + chainMs + "ms", N,
                MARK_HEADERS,
                formatHeaders(response.headers.toString()),
                N,
                MARK_BODY,
                formatBody(bodyString)!!,
                BOUND_BOTTOM)
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
        var extra: String
        singleThreadExecutor.execute {
            for (i in strings.indices) {
                extra = when (i) {
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

    }

    private fun formatHeaders(str: String): String {
        if (TextUtils.isEmpty(str)) {
            return "Empty"
        }
        val headers = str.split(N).toTypedArray()
        val builder = StringBuilder()
        var extra: String
        if (headers.size > 1) {
            for (i in headers.indices) {
                extra = if (i == 0) {
                    BOUND_CORNER_TOP
                } else if (i == headers.size - 1) {
                    BOUND_CORNER_BOTTOM
                } else {
                    BOUND_CORNER_CENTER
                }
                builder.append(extra).append(" ").append(headers[i]).append(N)
            }
        } else {
            builder.append("• ").append(str)
        }
        return builder.toString()
    }

    @Suppress("ConstantConditionIf")
    private fun formatBody(bodyString: String?): String? {
        if (TextUtils.isEmpty(bodyString)) return "Omitted Body"
        return if (formatBody) {
            JsonFormatUtil.jsonFormat(if (removeBackslash)
                bodyString!!.replace("\"[", "[")
                        .replace("]\"", "]")
                        .replace("\"{", "{")
                        .replace("}\"", "}")
                        .replace("\\\"", "\"") else bodyString!!)
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


    fun setFormatBody(boolean: Boolean): DefaultHttpLogPrinter {
        formatBody = boolean
        return this
    }

    fun setRemoveBackslash(boolean: Boolean): DefaultHttpLogPrinter {
        removeBackslash = boolean
        return this
    }

}