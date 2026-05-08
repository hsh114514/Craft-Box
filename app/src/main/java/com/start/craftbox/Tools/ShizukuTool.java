package com.start.craftbox.Tools;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import com.start.craftbox.BuildConfig;
import com.start.craftbox.Services.IUserService;
import com.start.craftbox.Services.UserService;

import java.io.File;

import rikka.shizuku.Shizuku;

public class ShizukuTool {
    private static final String TAG = "ShizukuTool";
    private final static int PERMISSION_CODE = 10001;
    public boolean shizukuServiceState = false;
    private static IUserService mUserService;
    private static final int MAX_RETRY_TIMES = 5;
    private int retryCount = 0;
    private static final Handler handler = new Handler(Looper.getMainLooper());
    private Context context;
    private static ShizukuTool shizukuToolInstance;
    public static ShizukuTool getInstance(Context context) {
        if (shizukuToolInstance == null) {
            shizukuToolInstance = new ShizukuTool(context);
        }
        return shizukuToolInstance;
    }

    public ShizukuTool(Context context) {
        this.context = context;
    }

    public void initShizuku() {
        Shizuku.addRequestPermissionResultListener(onRequestPermissionResultListener);
        Shizuku.addBinderReceivedListenerSticky(onBinderReceivedListener);
        Shizuku.addBinderDeadListener(onBinderDeadListener);
    }

    public boolean getPermissions() {
        boolean hasPermission = PermissionTool.areAllPermissionsGranted(context);
        if (!hasPermission) {
            try {
                String packageName = context.getPackageName();
                mUserService.execLine("appops set " + packageName + " MANAGE_EXTERNAL_STORAGE allow");
                mUserService.execLine("appops set " + packageName + " SYSTEM_ALERT_WINDOW allow");
                mUserService.execLine("pm grant " + packageName + " android.permission.POST_NOTIFICATIONS");
                hasPermission = PermissionTool.areAllPermissionsGranted(context);
            } catch (Exception err) {
                Log.d(TAG, "获取权限失败: " + err.getMessage());
            }
        }
        return hasPermission;
    }

    private final ServiceConnection userServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder binder) {
            if (binder != null && binder.pingBinder()) {
                mUserService = IUserService.Stub.asInterface(binder);
                Toast.makeText(context, "已连接 Shizuku 服务", Toast.LENGTH_SHORT).show();
            } else {
                Log.i(TAG, " Shizuku binder 为 null 或者 binder.pingBinder() 有问题");
            }
        }
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Toast.makeText(context, "已断开 Shizuku 服务", Toast.LENGTH_SHORT).show();
            mUserService = null;
        }
    };

    private final Shizuku.UserServiceArgs userServiceArgs =
            new Shizuku.UserServiceArgs(new ComponentName(BuildConfig.APPLICATION_ID, UserService.class.getName()))
                    .daemon(false)
                    .processNameSuffix("service")
                    .debuggable(BuildConfig.DEBUG)
                    .version(BuildConfig.VERSION_CODE);

    private final Shizuku.OnRequestPermissionResultListener onRequestPermissionResultListener = new Shizuku.OnRequestPermissionResultListener() {
        @Override
        public void onRequestPermissionResult(int requestCode, int grantResult) {
            boolean granted = grantResult == PackageManager.PERMISSION_GRANTED;
            if (granted) {
                Shizuku.bindUserService(userServiceArgs, userServiceConnection);
            } else {
                Toast.makeText(context, "权限被拒绝", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private final Shizuku.OnBinderReceivedListener onBinderReceivedListener = () -> {
        shizukuServiceState = true;
    };

    private final Shizuku.OnBinderDeadListener onBinderDeadListener = () -> {
        shizukuServiceState = false;
        mUserService = null;
        Toast.makeText(context, "Shizuku 服务被终止", Toast.LENGTH_SHORT).show();
    };

    public final Runnable checkServiceRunnable = new Runnable() {
        @Override
        public void run() {
            if (shizukuServiceState) {
                handler.removeCallbacks(this);
                handler.post(() -> {
                    checkShizikuPermissionFull();
                });
            } else if (retryCount < MAX_RETRY_TIMES) {
                retryCount++;
                handler.postDelayed(this, 1000);
            } else {
                handler.removeCallbacks(this);
                handler.post(() -> {
                    Toast.makeText(context, "服务初始化超时", Toast.LENGTH_SHORT).show();
                });
                Log.e(TAG, "Shizuku 服务初始化超时");
            }
        }
    };

    public void checkShizikuPermissionFull() {
        if (!shizukuServiceState) {
            Toast.makeText(context, "请检查 Shizuku 服务是否正常启动", Toast.LENGTH_SHORT).show();
            return;
        }
        if (Shizuku.isPreV11()) {
            Toast.makeText(context, "不支持动态服务绑定，请更新Shizuku应用", Toast.LENGTH_SHORT).show();
            return;
        }
        if (checkPermission()) {
            if (mUserService == null) {
                Shizuku.bindUserService(userServiceArgs, userServiceConnection);
            } else {
                Log.d(TAG, "服务正常，等待工作中");
                //Toast.makeText(context, "服务正常，等待工作中", Toast.LENGTH_SHORT).show();
            }
        } else {
            Shizuku.requestPermission(PERMISSION_CODE);
        }
    }

    public void startCheckService() {
        if (!shizukuServiceState) {
            handler.postDelayed(checkServiceRunnable, 1000); // 1秒后首次检查
        } else {
            checkShizikuPermissionFull();
        }
    }

    public void stopCheckService() {
        handler.removeCallbacks(checkServiceRunnable);
    }

    private static boolean checkPermission() {
        return Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED;
    }

    public void SilentInstall(File signedApkFile) throws Exception {
        if (!shizukuServiceState || !checkPermission() || mUserService == null) {
            throw new Exception("请检查 Shizuku 服务是否正常启动");
        }
        String apkPath = signedApkFile.getAbsolutePath();
        String targetPath = "/data/local/tmp/" + signedApkFile.getName();
        String command1 = "cp -f " + apkPath + " /data/local/tmp/";
        String command2 = "chmod 644 " + targetPath;
        String command3 = "pm install -r -t " + targetPath;
        String command4 = "rm " + targetPath;

        try {
            mUserService.execLine(command1);
            mUserService.execLine(command2);
            String resultOutput = mUserService.execLine(command3);
            mUserService.execLine(command4);
            if (resultOutput != null && resultOutput.trim().contains("Success")) {
                Toast.makeText(context, "安装成功", Toast.LENGTH_SHORT).show();
            } else {
                throw new Exception("安装失败: " + resultOutput);
            }
        } catch (Exception e) {
            throw new Exception("发生错误：" + e);
        }
    }

}
