package com.start.craftbox.Services;

interface IUserService {
    void destroy() = 16777114;

    void exit() = 1;

    String execLine(String command) = 2;

    String execArr(in String[] command) = 3;

    void installApk(String apkPath, String packageName) = 4;
}