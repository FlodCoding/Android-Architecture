package com.flod.android.arch.base.utils;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * SimpleDes:
 * Creator: Flood
 * Date: 2020-01-19
 * UseDes:
 */
public class JsonFormatUtil {

    /**
     * 把json字符串格式化为可读的字符串格式
     * 使用的关键方法：{@link org.json.JSONObject#toString(int)}
     *
     * @param indentSpaces "{" 或 "[" 离文字的空格数量
     */
    public static String jsonFormat(String json, int indentSpaces) {
        if (TextUtils.isEmpty(json))
            return json;
        String formatStr;
        try {
            if (json.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(json);
                formatStr = jsonObject.toString(indentSpaces);
            } else if (json.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(json);
                formatStr = jsonArray.toString(indentSpaces);
            } else {
                formatStr = json;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            final int maxLogChar = 3000;
            if (json.length() > maxLogChar) {
                int spitCount = json.length() / maxLogChar + 1;
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < spitCount; i++) {
                    if (i != spitCount - 1)
                        builder.append(json.substring(i * maxLogChar, (i + 1) * maxLogChar));

                    else
                        builder.append(json.substring(i * maxLogChar));
                    builder.append("\n");
                }
                formatStr = builder.toString();
            } else {
                formatStr = json;
            }


        }
        return formatStr;
    }

    public static String jsonFormat(String json) {
        return jsonFormat(json, 4);
    }
}
