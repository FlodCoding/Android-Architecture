package com.flod.android.arch.base.net.log;

import androidx.annotation.Nullable;

import okhttp3.Request;
import okhttp3.Response;

/**
 * SimpleDes:
 * Creator: Flood
 * Date: 2019-03-06
 * UseDes:
 */
public interface HttpLogPrinter {
    void printRequest(Request request, @Nullable String bodyString);

    void printRequestOmitted(Request request);

    void printResponse(long chainMs, Response response, @Nullable String bodyString);

    void printResponseOmitted(long chainMs, Response response);
}
