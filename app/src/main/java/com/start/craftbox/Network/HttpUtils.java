package com.start.craftbox.Network;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import androidx.annotation.NonNull;
import java.io.File;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;
public class HttpUtils {
    private static final OkHttpClient client = new OkHttpClient();
    private static final boolean DEBUG = false;
    public static final String BASE_URL = DEBUG ? "http://192.168.1.9/craftbox/" : "http://8.133.173.88/";

    public static void post(String url, FormBody formBody, Callback callback) {
        Request request = new Request.Builder()
                .url(BASE_URL + url)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(callback);
    }
    //TODO 仅测试,请在信息编辑页重写
    public void uploadAvatar(String token, String filePath) {
        OkHttpClient client = new OkHttpClient();

        File file = new File(filePath);
        if (!file.exists()) {
            Log.e("Upload", "文件不存在: " + filePath);
            return;
        }

        MediaType mediaType = MediaType.parse("image/png");

        RequestBody fileBody = RequestBody.create(file, mediaType);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("token", token)
                .addFormDataPart("avatar", file.getName(), fileBody)
                .build();

        Request request = new Request.Builder()
                .url(BASE_URL + "upload_avatar.php")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("Upload", "网络请求失败: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonResult = response.body().string();
                    Log.d("Upload", "服务器返回: " + jsonResult);
                } else {
                    Log.e("Upload", "服务器报错，状态码: " + response.code());
                }
            }
        });
    }
    public static boolean jumpWebSite(Activity context, String url) {
        if (context == null) return false;
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url != null ?url: "https://cn.bing.com/")));
        return true;
    }

}
