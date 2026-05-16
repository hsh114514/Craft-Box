package com.start.craftbox.Activitys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.chip.ChipGroup;
import com.start.craftbox.R;

public class MdInfoActivaty extends AppCompatActivity {
    Toolbar toolbar;
    ImageView imageView;
    TextView title_tv;
    TextView time_tv;
    TextView author_tv;
    TextView description_tv;
    ChipGroup tagGroup;
    Button favorite_btn;
    Button share_btn;
    Button open_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_md_info);
        toolbar = findViewById(R.id.toolbar);
        imageView = findViewById(R.id.img_title);
        title_tv = findViewById(R.id.tv_title);
        time_tv = findViewById(R.id.tv_time);
        author_tv = findViewById(R.id.author);
        description_tv = findViewById(R.id.tv_description);
        tagGroup = findViewById(R.id.chip_group_tags);
        favorite_btn = findViewById(R.id.favorite);
        share_btn = findViewById(R.id.share);
        open_btn = findViewById(R.id.btn_read);
        setSupportActionBar(toolbar);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        toolbar.setNavigationOnClickListener(view -> finish());

        open_btn.setOnClickListener(view -> {
            startActivity(new MdDocViewActivaty());
        });



    }

    private void startActivity(Activity newactivity) {
        Intent intent = new Intent(MdInfoActivaty.this, newactivity.getClass());
        startActivity(intent);
    }
}