package com.start.craftbox;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.start.craftbox.Tools.ShizukuTool;

import java.security.PublicKey;

public class CraftBoxApplication extends Application {
    private static CraftBoxApplication instance;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        ShizukuTool.getInstance(getInstance()).initShizuku();
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {

            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {
                ShizukuTool.getInstance(getInstance()).stopCheckService();
            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {
                ShizukuTool.getInstance(getInstance()).startCheckService();
            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {

            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {

            }
        });
    }


    public static CraftBoxApplication getInstance() {
        return instance;
    }


}
