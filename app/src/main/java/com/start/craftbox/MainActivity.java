package com.start.craftbox;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.color.DynamicColors;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.start.craftbox.Activitys.LoginActivity;
import com.start.craftbox.Activitys.UserActivity;
import com.start.craftbox.Entity.User;
import com.start.craftbox.Network.HttpUtils;
import com.start.craftbox.Page.AboutFragment;
import com.start.craftbox.Page.CodeEditorFragment;
import com.start.craftbox.Page.DebugFragment;
import com.start.craftbox.Page.HistoryFragment;
import com.start.craftbox.Page.HomeFragment;
import com.start.craftbox.Page.PixelEditorFragment;
import com.start.craftbox.Page.SettingsFragment;
import com.start.craftbox.Page.TexturePackGeneratorFragment;
import com.start.craftbox.databinding.ActivityMainBinding;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Fragment homeFragment, aboutFragment, historyFragment, settingsFragment, pixelFragment, textureFragment, codeFragment,debugFragment;
    ShapeableImageView profileImageView;
    TextView nicknameTextView;
    TextView levelTextView;
    TextView idTextView;

    @Override
    protected void onResume() {
        super.onResume();
        User user = User.getCurrentUser(this);
        if (User.isLogin(this)) {
            updateSidebarUI(user);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //this.setTheme(R.style.GreenTheme);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        initFragment();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DynamicColors.applyToActivityIfAvailable(this);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        profileImageView = headerView.findViewById(R.id.nav_user_avatar);
        nicknameTextView = headerView.findViewById(R.id.nav_user_nickname);
        levelTextView = headerView.findViewById(R.id.nav_user_level);
        idTextView = headerView.findViewById(R.id.nav_user_id);

        User user = User.getCurrentUser(this);
        profileImageView.setOnClickListener(view ->
                startActivity(User.isLogin(this) ? new UserActivity() : new LoginActivity())
        );

        if (User.isLogin(this)) {
            updateSidebarUI(user);
        }
        performSilentLogin(user);

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                replaceFragment(homeFragment);
            } else if (id == R.id.nav_history) {
                replaceFragment(historyFragment);
            } else if (id == R.id.nav_settings) {
                replaceFragment(settingsFragment);
            } else if (id == R.id.nav_about) {
                replaceFragment(aboutFragment);
            } else if (id == R.id.nav_debug) {
                replaceFragment(debugFragment);
            }
            drawerLayout.closeDrawers();
            return true;
        });


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


    }

    private void initFragment() {
        if (homeFragment == null) homeFragment = new HomeFragment();
        if (aboutFragment == null) aboutFragment = new AboutFragment();
        if (historyFragment == null) historyFragment = new HistoryFragment();
        if (settingsFragment == null) settingsFragment = new SettingsFragment();
        if (pixelFragment == null) pixelFragment = new PixelEditorFragment();
        if (textureFragment == null) textureFragment = new TexturePackGeneratorFragment();
        if (codeFragment == null) codeFragment = new CodeEditorFragment();
        if (debugFragment == null) debugFragment = new DebugFragment();
        replaceFragment(homeFragment);
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }

    private void startActivity(Activity newactivity) {
        Intent intent = new Intent(MainActivity.this, newactivity.getClass());
        startActivity(intent);
    }

    void performSilentLogin(User user) {
        FormBody formBody = new FormBody.Builder()
                .add("username", user.getUserName())
                .add("password", user.getPassword())
                .build();

        HttpUtils.post("login.php", formBody, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "当前处于离线模式", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                //Log.d("Login", json);
                try {
                    LoginActivity.LoginResponse apiResponse = new Gson().fromJson(json, LoginActivity.LoginResponse.class);
                    if (apiResponse.code == 200) {
                        User newUser = new User(user.getUserName(), user.getPassword())
                                .setToken(apiResponse.data.token)
                                .setId(apiResponse.data.id)
                                .setNickName(apiResponse.data.nickname)
                                .setLevel(apiResponse.data.level)
                                .setExp(apiResponse.data.exp)
                                .setRole(apiResponse.data.role)
                                .setBio(apiResponse.data.bio)
                                .setAvatar_path(apiResponse.data.avatar_url);
                        newUser.save(MainActivity.this);
                        //Log.d("Login", newUser.toString());
                        runOnUiThread(() -> updateSidebarUI(newUser));
                    } else {
                        runOnUiThread(() -> {
                            User.logout(MainActivity.this);
                            //showLoginPrompt();
                        });
                    }
                } catch (Exception e) {
                    Log.d("Login", e.getMessage());
                }

            }
        });
    }

    @SuppressLint("SetTextI18n")
    void updateSidebarUI(User user) {
        nicknameTextView.setText(user.getNickName());
        levelTextView.setText("Lv." + user.getLevel());
        idTextView.setText("ID:" + user.getId());
        if (user.getAvatar_path() != null) {
            Glide.with(this)
                    .load(user.getAvatar_path())
                    .circleCrop()
                    .placeholder(R.mipmap.ic_launcher_round)
                    .error(R.mipmap.ic_launcher_round)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(profileImageView);
        }

    }


}