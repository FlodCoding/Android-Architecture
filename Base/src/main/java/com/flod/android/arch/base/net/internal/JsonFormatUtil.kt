@file:Suppress("MemberVisibilityCanBePrivate")

package com.flod.android.arch.base.net.internal

import android.text.TextUtils
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * SimpleDes:
 * Creator: Flood
 * Date: 2020-01-21
 * UseDes:
 *
 */
internal object JsonFormatUtil {

    /**
     * 把json字符串格式化为可读的字符串格式
     * 使用的关键方法：[JSONObject.toString]
     *
     * @param indentSpaces "{" 或 "[" 离文字的空格数量
     */
    fun jsonFormat(json: String, indentSpaces: Int): String? {
        if (TextUtils.isEmpty(json)) return json
        val formatStr: String
        formatStr = try {
            when {
                json.startsWith("{") -> {
                    val jsonObject = JSONObject(json)
                    jsonObject.toString(indentSpaces)
                }
                json.startsWith("[") -> {
                    val jsonArray = JSONArray(json)
                    jsonArray.getString(indentSpaces)
                }
                else -> {
                    json
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            val maxLogChar = 3000
            if (json.length > maxLogChar) {
                val spitCount = json.length / maxLogChar + 1
                val builder = StringBuilder()
                var i = 0
                while (i < spitCount) {
                    if (i != spitCount - 1) builder.append(json.substring(i * maxLogChar, (i + 1) * maxLogChar)) else builder.append(json.substring(i * maxLogChar))
                    builder.append("\n")
                    i++
                }
                builder.toString()
            } else {
                json
            }
        }
        return formatStr
    }

    fun jsonFormat(json: String): String? {
        return jsonFormat(json, 4)
    }
}