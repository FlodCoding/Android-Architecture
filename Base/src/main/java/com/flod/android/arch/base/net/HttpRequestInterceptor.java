package com.flod.android.arch.base.net;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.flod.android.arch.base.net.log.DefaultHttpLogPrinter;
import com.flod.android.arch.base.net.log.HttpLogPrinter;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.GzipSource;

/**
 * SimpleDes:
 * Creator: Flood
 * Date: 2019-03-05
 * UseDes:
 */
@SuppressWarnings("ALL")
public class HttpRequestInterceptor implements Interceptor {

    private static final Charset UTF8 = Charset.forName("UTF-8");
    private Level printLevel;
    private InterceptHandler mInterceptHandler;
    private HttpLogPrinter mHttpLogPrinter;

    //创建一个单线程池
    private ExecutorService mSingleService = Executors.newSingleThreadExecutor();


    public HttpRequestInterceptor() {
        this(Level.NONE, null, null);
    }

    public HttpRequestInterceptor(@Nullable Level logLevel) {
        this(logLevel, null, null);
    }

    public HttpRequestInterceptor(@Nullable Level logLevel, @Nullable InterceptHandler handler) {
        this(logLevel, handler, null);
    }

    public HttpRequestInterceptor(@Nullable Level level, @Nullable InterceptHandler handler, HttpLogPrinter printer) {
        if (level == null) {
            printLevel = Level.NONE;
        } else
            printLevel = level;

        mInterceptHandler = handler;
        if (printer == null)
            mHttpLogPrinter = new DefaultHttpLogPrinter();
        else
            mHttpLogPrinter = printer;
    }

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        //从Chain拿到Request
        Request request = chain.request();

        boolean logReq = printLevel == Level.ALL || printLevel == Level.REQUEST;
        boolean logRsp = printLevel == Level.ALL || printLevel == Level.RESPONSE;

        if (logReq) {
            Request finalRequest = request;
            mSingleService.execute(() -> {
                if (finalRequest.body() != null && isPlaintext(finalRequest.body().contentType())) {
                    String bodyStr = requestBodyToStr(finalRequest);
                    if (!TextUtils.isEmpty(bodyStr))
                        mHttpLogPrinter.printRequest(finalRequest, bodyStr);
                    else
                        mHttpLogPrinter.printRequestOmitted(finalRequest);
                } else
                    mHttpLogPrinter.printRequestOmitted(finalRequest);
            });

        }

        if (mInterceptHandler != null) {
            request = mInterceptHandler.interceptRequest(chain, request);
        }

        long t1 = logRsp ? System.nanoTime() : 0;
        Response originalResponse;
        originalResponse = chain.proceed(request);
        long t2 = logRsp ? System.nanoTime() : 0;


        String rspBodyStr = null;
        if (originalResponse.body() != null
                && isPlaintext(originalResponse.body().contentType())
                && !(mInterceptHandler == null && !logRsp)) {
            rspBodyStr = responseBodyToStr(originalResponse);
        }

        long chainMs = TimeUnit.NANOSECONDS.toMillis(t2 - t1);
        if (logRsp) {
            Response finalOriginalResponse = originalResponse;
            String finalRspBodyStr = rspBodyStr;
            mSingleService.submit(() -> {
                if (finalOriginalResponse.body() != null
                        && isPlaintext(finalOriginalResponse.body().contentType())
                        && !TextUtils.isEmpty(finalRspBodyStr)) {
                    mHttpLogPrinter.printResponse(chainMs, finalOriginalResponse, finalRspBodyStr);
                } else {
                    mHttpLogPrinter.printResponseOmitted(chainMs, finalOriginalResponse);
                }
            });

        }

        if (mInterceptHandler != null) {
            originalResponse = mInterceptHandler.interceptResponse(rspBodyStr, chain, originalResponse);
        }

        return originalResponse;
    }

    @Nullable
    private String requestBodyToStr(Request request) {
        RequestBody body = request.newBuilder().build().body();
        if (body == null) return null;
        Buffer buffer = new Buffer();
        try {
            body.writeTo(buffer);
            //设置读取的字符集类型
            Charset charset = getCharset(body.contentType());
            return buffer.readString(charset);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 从Response获取String
     */
    private String responseBodyToStr(Response response) {
        try {
            ResponseBody responseBody = response.newBuilder().build().body();
            if (responseBody == null)
                return null;

            //通过response.body直接读取string会直接关闭流,流只能使用一次
            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE);

            //拿到Buffer
            Buffer buffer = source.getBuffer();

            //获取content的编码类型（可能是gzip）
            String encoding = response.headers().get("Content-Encoding");

            if (encoding != null && encoding.equalsIgnoreCase("gzip")) {
                try (GzipSource gzippedResponseBody = new GzipSource(buffer.clone())) {
                    buffer = new Buffer();
                    buffer.writeAll(gzippedResponseBody);
                }
            }
            //设置读取的字符集类型
            Charset charset = getCharset(responseBody.contentType());
            return buffer.clone().readString(charset);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 是否是纯文本格式
     */
    private boolean isPlaintext(MediaType mediaType) {
        if (mediaType == null)
            return false;
        if (mediaType.type() != null && mediaType.type().toLowerCase().equals("text")) {
            return true;
        }
        String subtype = mediaType.subtype();
        if (subtype != null) {
            subtype = subtype.toLowerCase();
            return subtype.contains("x-www-form-urlencoded") || subtype.contains("json") || subtype.contains("xml") || subtype.contains("html")
                    || subtype.contains("x-javascript") || subtype.contains("plain");
        }

        return false;
    }

    private Charset getCharset(MediaType contentType) {
        return contentType != null ? contentType.charset(UTF8) : UTF8;
    }



    public enum Level {

        NONE,         //不打印

        REQUEST,      //只打印请求信息

        RESPONSE,     //只打印响应信息

        ALL,          //打印全部
    }
}
