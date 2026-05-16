package com.start.craftbox.Entity;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.start.craftbox.Network.HttpUtils;
import com.start.craftbox.Tools.SPTool;

public class User {
    private static User currentUser;
    private String userName;
    private String password;

    //运行时 TODO 也许需要实现鉴权(token时效?)
    private transient String token;
    private transient Drawable avatar;

    //需要本地缓存
    private int id;
    private String nickName;
    private String avatar_path;
    private String bio;
    private int level;
    private int exp;
    private int role;

    public User() {}

    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }


    public static User getCurrentUser(Context context) {
        if (currentUser == null) {
            String userJson = SPTool.getString(context, "cached_user_info", "");
            if (!userJson.isEmpty()) {
                currentUser = new Gson().fromJson(userJson, User.class);
            } else {
                String u = SPTool.getString(context, "userName", "");
                String p = SPTool.getString(context, "password", "");
                currentUser = new User(u, p);
            }
        }
        return currentUser;
    }

    public void save(Context context) {
        SPTool.saveString(context, "userName", this.userName);
        SPTool.saveString(context, "password", this.password);
        SPTool.saveBoolean(context, "isLogin", true);
        String userJson = new Gson().toJson(this);
        SPTool.saveString(context, "cached_user_info", userJson);

        currentUser = this;
    }

    public static void logout(Context context) {
        currentUser = null;
        SPTool.saveBoolean(context, "isLogin", false);
        SPTool.saveString(context, "password", "");
        SPTool.saveString(context, "cached_user_info", "");
    }

    public static boolean isLogin(Context context) {
        return SPTool.getBoolean(context, "isLogin", false);
    }

    public String getUserName() { return userName; }
    public String getPassword() { return password; }
    public String getToken() { return token; }
    public int getId() { return id; }
    public String getNickName() { return nickName; }
    public String getAvatar_path() {
        return HttpUtils.BASE_URL + "uploads/avatars/" + avatar_path;
    }
    public Drawable getAvatar() { return avatar; }
    public String getBio() { return bio; }
    public int getLevel() { return level; }
    public int getExp() { return exp; }
    public int getRole() { return role; }

    public User setUserName(String userName) { this.userName = userName; return this; }
    public User setPassword(String password) { this.password = password; return this; }
    public User setToken(String token) { this.token = token; return this; }
    public User setId(int id) { this.id = id; return this; }
    public User setNickName(String nickName) { this.nickName = nickName; return this; }
    public User setAvatar_path(String avatar_path) {
        this.avatar_path = avatar_path;
        return this;
    }
    public User setAvatar(Drawable avatar) { this.avatar = avatar; return this; }
    public User setBio(String bio) { this.bio = bio; return this; }
    public User setLevel(int level) { this.level = level; return this; }
    public User setExp(int exp) { this.exp = exp; return this; }
    public User setRole(int role) { this.role = role; return this; }

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", token='" + token + '\'' +
                ", avatar=" + avatar +
                ", id=" + id +
                ", nickName='" + nickName + '\'' +
                ", avatar_path='" + avatar_path + '\'' +
                ", bio='" + bio + '\'' +
                ", level=" + level +
                ", exp=" + exp +
                ", role=" + role +
                '}';
    }
}