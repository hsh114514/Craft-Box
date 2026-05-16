package com.start.craftbox.Activitys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.start.craftbox.Entity.User;
import com.start.craftbox.Network.HttpUtils;
import com.start.craftbox.R;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onResume() {
        super.onResume();
        User user = User.getCurrentUser(this);
        TextInputEditText username = findViewById(R.id.et_username);
        TextInputEditText password = findViewById(R.id.et_password);
        if (username.getText().toString().isEmpty() && password.getText().toString().isEmpty()){
            username.setText(user.getUserName());
            password.setText(user.getPassword());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        Activity context = this;
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login_container), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextInputLayout usernamelayout = findViewById(R.id.til_username);
        TextInputEditText username = findViewById(R.id.et_username);
        TextInputLayout passwordlayout = findViewById(R.id.til_password);
        TextInputEditText password = findViewById(R.id.et_password);

        User user = User.getCurrentUser(context);
        username.setText(user.getUserName());
        password.setText(user.getPassword());


        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {

            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                usernamelayout.setError(null);
            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {

            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                passwordlayout.setError(null);
            }
        });

        findViewById(R.id.btn_login).setOnClickListener(view -> {
            if (username.getText().toString().isEmpty()) {
                usernamelayout.setError("请输入用户名");
                return;
            }
            if (password.getText().toString().isEmpty()) {
                passwordlayout.setError("请输入密码");
                return;
            }
            FormBody formBody = new FormBody.Builder()
                    .add("username", username.getText().toString())
                    .add("password", password.getText().toString())
                    .build();
            HttpUtils.post("login.php", formBody, new Callback() {
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    String json = response.body().string();
                    //Log.d("LoginActivity", json);
                    Gson gson = new Gson();
                    try {
                        LoginResponse loginResponse = gson.fromJson(json, LoginResponse.class);
                        switch (loginResponse.code) {
                            case 200:
                                User user = new User()
                                        .setUserName(username.getText().toString())
                                        .setPassword(password.getText().toString())
                                        .setToken(loginResponse.data.token)
                                        .setId(loginResponse.data.id)
                                        .setNickName(loginResponse.data.nickname)
                                        .setLevel(loginResponse.data.level)
                                        .setExp(loginResponse.data.exp)
                                        .setRole(loginResponse.data.role)
                                        .setAvatar_path(loginResponse.data.avatar_url);
                                //Log.d("LoginActivity", user.toString());
                                user.save(context);
                                runOnUiThread(() -> {
                                    Toast.makeText(context, loginResponse.msg, Toast.LENGTH_SHORT).show();
                                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                        if (!isFinishing() && !isDestroyed()) finish();
                                    }, 1000);
                                });
                                break;
                            case 401:
                                handler.post(() -> {
                                    Toast.makeText(context, loginResponse.msg, Toast.LENGTH_SHORT).show();
                                });
                                break;
                        }
                    } catch (Exception e) {
                        Log.d("LoginActivity", e.getMessage());
                    }

                }

                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.d("LoginActivity", e.getMessage());
                    handler.post(() -> {
                        Toast.makeText(context, "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            });

        });

        findViewById(R.id.btn_to_register).setOnClickListener(view -> {
            startActivity(new RegisterActivity());
        });
    }

    public class LoginResponse {
        public int code;
        public String msg;
        public UserData data;

        public static class UserData {
            public int id;
            public String token;
            public String nickname;
            public int level;
            public int exp;
            public int role;
            public String avatar_url;
        }
    }

    private void startActivity(Activity newactivity) {
        Intent intent = new Intent(LoginActivity.this, newactivity.getClass());
        startActivity(intent);
    }
}