package com.start.craftbox.Services;

import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class UserService extends IUserService.Stub {

    @Override
    public void destroy() throws RemoteException {
        System.exit(0);
    }

    @Override
    public void exit() throws RemoteException {
        destroy();
    }

    @Override
    public String execLine(String command) throws RemoteException {
        try {
            Process process = Runtime.getRuntime().exec(command);
            return readResult(process);
        } catch (IOException | InterruptedException e) {
            throw new RemoteException();
        }
    }

    @Override
    public String execArr(String[] command) throws RemoteException {
        try {
            Process process = Runtime.getRuntime().exec(command);
            return readResult(process);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public String readResult(Process process) throws IOException, InterruptedException {
        StringBuilder output = new StringBuilder();
        StringBuilder error = new StringBuilder();
        Thread errorReader = getThread(process, error);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }
        process.waitFor();
        errorReader.join();

        if (error.length() > 0) {
            output.append("\n--- STDERR ---\n").append(error.toString());
        }
        return output.toString();
    }

    @NonNull
    private static Thread getThread(Process process, StringBuilder error) {
        Thread errorReader =
                new Thread(() -> {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            error.append("[ERROR] ").append(line).append("\n");
                        }
                    } catch (IOException e) {
                        Log.e("read Result", "读取错误流失败", e);
                    }
                });

        errorReader.start();
        return errorReader;
    }

    @Override
    public void installApk(String apkPath, String packageName) throws RemoteException {
        //TODO 使用hide api实现?
    }
}
