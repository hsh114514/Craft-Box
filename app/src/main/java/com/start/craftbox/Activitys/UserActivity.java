package com.start.craftbox.Activitys;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.start.craftbox.Entity.User;
import com.start.craftbox.R;

public class UserActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user);
        User user = User.getCurrentUser(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.user_activity_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        User user = User.getCurrentUser(this);
        ImageView avatar = findViewById(R.id.profile_avatar);
        TextView nickname = findViewById(R.id.tv_nickname);
        TextView bio = findViewById(R.id.tv_bio);
        nickname.setText(user.getNickName());
        bio.setText(user.getBio());

        Glide.with(this)
                .load(user.getAvatar_path())
                .circleCrop()
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(avatar);
    }
}