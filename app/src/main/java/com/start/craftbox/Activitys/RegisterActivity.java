package com.start.craftbox.Activitys;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.start.craftbox.Entity.User;
import com.start.craftbox.Network.HttpUtils;
import com.start.craftbox.R;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    private TextWatcher createClearErrorWatcher(TextInputLayout textInputLayout) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (textInputLayout.getError() != null) {
                    textInputLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.reg_activaty), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        TextInputLayout usernamelayout = findViewById(R.id.til_register_username);
        TextInputLayout passwordlayout = findViewById(R.id.til_register_password);
        TextInputLayout confirmlayout = findViewById(R.id.til_register_confirm_password);

        TextInputEditText username = findViewById(R.id.et_register_username);
        TextInputEditText password = findViewById(R.id.et_register_password);
        TextInputEditText confirm = findViewById(R.id.et_register_confirm_password);

        MaterialButton register_btn = findViewById(R.id.btn_register);
        LinearProgressIndicator progress = findViewById(R.id.progress_register);
        MaterialButton login_btn = findViewById(R.id.btn_go_to_login);

        username.addTextChangedListener(createClearErrorWatcher(usernamelayout));
        password.addTextChangedListener(createClearErrorWatcher(passwordlayout));
        confirm.addTextChangedListener(createClearErrorWatcher(confirmlayout));

        login_btn.setOnClickListener(view -> {
            finish();
        });

        register_btn.setOnClickListener(view -> {
            String uText = username.getText().toString().trim(); // 顺手 trim 一下更安全
            String pText = password.getText().toString();
            String cText = confirm.getText().toString();

            if (uText.isEmpty()) { usernamelayout.setError("用户名不能为空"); return; }
            else if (uText.length() < 3) { usernamelayout.setError("用户名过短"); return; }
            else if (uText.length() > 20) { usernamelayout.setError("用户名过长"); return; }
            else if (uText.contains(" ")) { usernamelayout.setError("用户名不能包含空格"); return; }

            if (pText.isEmpty()) { passwordlayout.setError("密码不能为空"); return; }
            else if (pText.length() < 6) { passwordlayout.setError("密码过短"); return; }
            else if (pText.length() > 20) { passwordlayout.setError("密码过长"); return; }
            else if (pText.contains(" ")) { passwordlayout.setError("密码不能包含空格"); return; }

            if (cText.isEmpty()) { confirmlayout.setError("确认密码不能为空"); return; }
            if (!pText.equals(cText)) { confirmlayout.setError("密码不一致"); return; }

            progress.setVisibility(View.VISIBLE);
            register_btn.setEnabled(false);

            FormBody formBody = new FormBody.Builder()
                    .add("username", uText)
                    .add("password", pText)
                    .build();

            HttpUtils.post("register.php", formBody, new Callback() {
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    switch (response.code()) {
                        case 200:
                            User.logout(RegisterActivity.this);
                            User user = new User(uText, pText);
                            user.save(RegisterActivity.this);
                            runOnUiThread(() -> {
                                progress.setVisibility(View.GONE);
                                register_btn.setEnabled(true);
                                Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                    if (!isFinishing() && !isDestroyed()) finish();
                                }, 1000);
                            });
                            break;
                        case 409:
                            runOnUiThread(() -> {
                                usernamelayout.setError("用户已存在");
                                progress.setVisibility(View.GONE);
                                register_btn.setEnabled(true);
                            });
                            break;
                        case 500:
                            runOnUiThread(() -> {
                                Toast.makeText(RegisterActivity.this, "服务器错误", Toast.LENGTH_SHORT).show();
                                progress.setVisibility(View.GONE);
                                register_btn.setEnabled(true);
                            });
                            break;
                    }
                }

                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    runOnUiThread(() -> {
                        Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                        progress.setVisibility(View.GONE);
                        register_btn.setEnabled(true);
                    });
                }
            });


        });

    }
}