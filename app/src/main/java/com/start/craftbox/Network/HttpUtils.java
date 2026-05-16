package com.start.craftbox.Network;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HttpUtils {
    private static final OkHttpClient client = new OkHttpClient();
    private static final boolean DEBUG = false;
    public static final String BASE_URL = DEBUG ? "http://8.133.173.88/" : "http://192.168.1.9/craftbox/";

    public static void post(String url, FormBody formBody, Callback callback) {
        Request request = new Request.Builder()
                .url(BASE_URL + url)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(callback);
    }
}
