package com.start.craftbox.Activitys;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.start.craftbox.MainActivity;
import com.start.craftbox.Page.Onboarding.OnboardingFragment;
import com.start.craftbox.Page.Onboarding.OnboardingPage;
import com.start.craftbox.Page.Onboarding.OnboardingPageBinder;
import com.start.craftbox.Page.Onboarding.OnboardingPageFactory;
import com.start.craftbox.R;
import com.start.craftbox.Tools.SPTool;

import java.util.Arrays;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity implements OnboardingPageBinder {

    private ViewPager2 viewPager;
    private FloatingActionButton btnNext;
    private LinearProgressIndicator progressIndicator;
    
    private List<OnboardingPage> pages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_onboarding);
        if (SPTool.getBoolean(this, "isinit", false)) {
            startActivityToMain();
            return;
        }

        viewPager = findViewById(R.id.viewPager);
        btnNext = findViewById(R.id.btnNext);
        progressIndicator = findViewById(R.id.progressIndicator);

        pages = Arrays.asList(
                OnboardingPageFactory.createDefaultPage(
                        "欢迎",
                        "学习,编辑,测试您的 MinecraftPE 客户端 一切尽在Craft Box",
                        R.drawable.minecraft
                ),
                OnboardingPageFactory.createCustomPage(
                        R.layout.fragment_permission_request,
                        view -> {
                            MaterialButton btnRequestPermission = view.findViewById(R.id.btnRequestPermission);
                            if (btnRequestPermission != null) {
                                btnRequestPermission.setOnClickListener(v -> {
                                });
                            }
                        }
                ),
                OnboardingPageFactory.createDefaultPage(
                        "准备就绪",
                        "点击下方按钮开始您的开发之旅",
                        R.drawable.play
                )
        );

        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @Override
            public int getItemCount() {
                return pages.size();
            }

            @Override
            public androidx.fragment.app.Fragment createFragment(int position) {
                return OnboardingFragment.newInstance(pages.get(position).getLayoutRes(), position);
            }
        });

        progressIndicator.setProgress(0);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                int progress = (int) (((position + 1) / (float) pages.size()) * 100);
                progressIndicator.setProgress(progress, true);
            }
        });

        btnNext.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() < pages.size() - 1) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            } else {
                SPTool.saveBoolean(this, "isinit", true);
                startActivityToMain();
            }
        });
    }

    @Override
    public void bindPage(View view, int position) {
        if (position >= 0 && position < pages.size()) {
            pages.get(position).getBindView().bind(view);
        }
    }

    private void startActivityToMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}