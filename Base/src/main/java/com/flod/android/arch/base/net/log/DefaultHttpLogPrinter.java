package com.flod.android.arch.base.net.log;

import android.text.TextUtils;
import android.util.Log;

import com.flod.android.arch.base.utils.JsonFormatUtil;

import okhttp3.Request;
import okhttp3.Response;

/**
 * SimpleDes:
 * Creator: Flood
 * Date: 2019-03-14
 * UseDes:TODO 两个请求同时到达就会重叠
 */
@SuppressWarnings({"unused", "ConstantConditions"})
public class DefaultHttpLogPrinter implements HttpLogPrinter {
    private static final boolean isFormatBody = true; //是否格式化jsonBody
    private static final String TAG = "HttpLogPrinter";
    private static final String[] LogExtra = {"-\uD83D\uDE03-", "-\uD83D\uDE11-", "-\uD83D\uDE33-", "-\uD83D\uDE06-"};

    private static final String MARK_REQUEST_TOP_LINE = "────── Request ────────────────────────────────────────────────────────────────────────────────────────────";
    private static final String MARK_RESPONSE_TOP_LINE = "────── Response ───────────────────────────────────────────────────────────────────────────────────────────";
    private static final String MARK_CENTER_LINE = "│ ";
    private static final String MARK_BOTTOM_LINE = "───────────────────────────────────────────────────────────────────────────────────────────────────────────";

    private static final String MARK_CORNER_TOP = "┌";
    private static final String MARK_CORNER_CENTER = "├";
    private static final String MARK_CORNER_BOTTOM = "└";

    private static final String MARK_URL = "URL: ";
    private static final String MARK_METHOD = "Method: @";
    private static final String MARK_HEADERS = "Headers: ";
    private static final String MARK_BODY = "Body: ";
    private static final String MARK_STATUS_CODE = "Status Code: ";
    private static final String MARK_TIME = "Time: ";

    private static final String N = "\n";
    private static ThreadLocal<Integer> tagIndex = new ThreadLocal<Integer>() {
        @Override
        protected Integer initialValue() {
            return 0;
        }
    };


    private static void logRequest(Request request, String bodyString) {
        String tag = TAG + "-Request";
        logMulti(tag, MARK_REQUEST_TOP_LINE,
                MARK_URL + request.url(),
                MARK_METHOD + request.method(),
                N,
                MARK_HEADERS,
                formatHeaders(request.headers().toString()),
                N,
                MARK_BODY,
                formatBody(bodyString),
                MARK_BOTTOM_LINE
        );
    }

    private static void logResponse(long chainMs, Response response, String bodyString) {
        String tag = TAG + "-Response";
        logMulti(tag, MARK_RESPONSE_TOP_LINE,
                MARK_URL + response.request().url(),
                MARK_METHOD + response.request().method(),
                MARK_STATUS_CODE + response.code() + " / " + response.message(),
                MARK_TIME + chainMs + "ms", N,
                MARK_HEADERS,
                formatHeaders(response.headers().toString()),
                N,
                MARK_BODY,
                formatBody(bodyString),
                MARK_BOTTOM_LINE);
    }

    private static void log(String tag, String string) {
        Log.d(getTag(tag),string);
    }

    /**
     * 执行多条打印
     *
     * @param tag     传入日志的tag，在tag前会额外添加多余的字符，为了防止日志被合并成一条
     * @param strings 传入的String array
     */
    private static void logMulti(String tag, String... strings) {
        String extra;
        for (int i = 0; i < strings.length; i++) {
            if (i == 0) {
                extra = MARK_CORNER_TOP;
            } else if (i == strings.length - 1) {
                extra = MARK_CORNER_BOTTOM;
            } else {
                extra = MARK_CENTER_LINE;
            }

            String[] childStr = strings[i].split(N);
            if (childStr.length > 0) {
                for (String s : childStr) {
                    log(tag, extra + s);
                }
                continue;
            }
            log(tag, extra + strings[i]);
        }
    }

    private static String formatHeaders(String str) {
        if (TextUtils.isEmpty(str)) {
            return "Empty";
        }

        String[] headers = str.split(N);
        StringBuilder builder = new StringBuilder();
        String extra;
        if (headers.length > 1) {
            for (int i = 0; i < headers.length; i++) {
                if (i == 0) {
                    extra = MARK_CORNER_TOP;
                } else if (i == headers.length - 1) {
                    extra = MARK_CORNER_BOTTOM;
                } else {
                    extra = MARK_CORNER_CENTER;
                }
                builder.append(extra).append(" ").append(headers[i]).append(N);
            }
        } else {
            builder.append("• ").append(str);
        }

        return builder.toString();

    }

    private static String formatBody(String bodyString) {
        if (TextUtils.isEmpty(bodyString))
            return "Omitted Body";
        //bodyString = unicodeToString(bodyString);
        if (isFormatBody)
            return JsonFormatUtil.jsonFormat(bodyString);
        else return bodyString;
    }

/*

    public static String unicodeToString(String str) {
        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
        Matcher matcher = pattern.matcher(str);
        char ch;
        while (matcher.find()) {
            String group = matcher.group(2);
            ch = (char) Integer.parseInt(group, 16);
            String group1 = matcher.group(1);
            str = str.replace(group1, ch + "");
        }
        return str;
    }
*/

    /**
     * 在快速连续打印时日志会把多条合并成一条，导致日志不能对齐，解决方案时是不断改变每条log的tag
     */
    private static String getTag(String tag) {
        int index = tagIndex.get();
        if (index > LogExtra.length - 1) {
            tagIndex.set(0);
            index = 0;
        }
        String extra = LogExtra[index];
        tagIndex.set(++index);
        return extra + tag;
    }

    @Override
    public void printRequest(Request request, String bodyString) {
        logRequest(request, bodyString);
    }

    @Override
    public void printRequestOmitted(Request request) {
        logRequest(request, null);
    }

    @Override
    public void printResponse(long chainMs, Response response, String bodyString) {
        logResponse(chainMs, response, bodyString);
    }

    @Override
    public void printResponseOmitted(long chainMs, Response response) {
        logResponse(chainMs, response, null);
    }
}
