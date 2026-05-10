package com.start.craftbox.Activitys;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.start.craftbox.MainActivity;
import com.start.craftbox.Page.Onboarding.OnboardingFragment;
import com.start.craftbox.Page.Onboarding.OnboardingPage;
import com.start.craftbox.Page.Onboarding.OnboardingPageBinder;
import com.start.craftbox.Page.Onboarding.OnboardingPageFactory;
import com.start.craftbox.R;
import com.start.craftbox.Tools.PermissionTool;
import com.start.craftbox.Tools.SPTool;
import com.start.craftbox.Tools.ShizukuTool;

import java.util.Arrays;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity implements OnboardingPageBinder {
    private ViewPager2 viewPager;
    private MaterialButton btnBack;
    private MaterialButton btnNext;
    private LinearProgressIndicator progressIndicator;
    private List<OnboardingPage> pages;
    private MaterialSwitch storageSwitch;
    private MaterialSwitch shizukuSwitch;
    
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
        btnBack = findViewById(R.id.btnBack);
        btnNext = findViewById(R.id.btnNext);
        progressIndicator = findViewById(R.id.progressIndicator);

        pages = Arrays.asList(
                OnboardingPageFactory.createCustomPage(R.layout.onboarding_welcome, view -> {}),
                OnboardingPageFactory.createCustomPageWithCompletionCheck(
                        R.layout.fragment_permission_request,
                        view -> {
                            shizukuSwitch = view.findViewById(R.id.shizuku_switch);
                            shizukuSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                                if (isChecked) {
                                    ShizukuTool.getInstance(OnboardingActivity.this).startCheckService();
                                } else {
                                    ShizukuTool.getInstance(OnboardingActivity.this).stopCheckService();
                                }
                            });

                            storageSwitch = view.findViewById(R.id.storage_switch);
                            storageSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                                if (isChecked) {
                                    PermissionTool.storePermission(OnboardingActivity.this);
                                }
                            });
                            
                            checkAndDisableStorageSwitch();
                        },
                        () -> PermissionTool.isStoragePermissionGranted(this)
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

            @NonNull
            @Override
            public androidx.fragment.app.Fragment createFragment(int position) {
                return OnboardingFragment.newInstance(pages.get(position).getLayoutRes(), position);
            }
        });

        disableViewPagerSwipe(viewPager);

        progressIndicator.setProgress(0);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                int progress = (int) (((position + 1) / (float) pages.size()) * 100);
                progressIndicator.setProgress(progress, true);
                updateButtonState();
                checkAndDisableShizukuSwitch();
                
                if (position == 1) {
                    checkAndDisableStorageSwitch();
                }
            }
        });

        btnBack.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() > 0) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
            }
        });

        btnNext.setOnClickListener(v -> {
            OnboardingPage currentPage = pages.get(viewPager.getCurrentItem());
            if (currentPage.getCompletionChecker().isCompleted()) {
                if (viewPager.getCurrentItem() < pages.size() - 1) {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                } else {
                    SPTool.saveBoolean(this, "isinit", true);
                    startActivityToMain();
                }
            } else {
                Snackbar.make(v, "请完成初始化", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAndDisableStorageSwitch();
        checkAndDisableShizukuSwitch();
    }

    private void checkAndDisableStorageSwitch() {
        if (storageSwitch != null && PermissionTool.isStoragePermissionGranted(this)) {
            storageSwitch.setChecked(true);
            storageSwitch.setEnabled(false);
            Toast.makeText(this, "存储权限已授予", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkAndDisableShizukuSwitch() {
        if (shizukuSwitch != null && ShizukuTool.getInstance(OnboardingActivity.this).shizukuServiceState) {
            shizukuSwitch.setChecked(true);
            shizukuSwitch.setEnabled(false);
            Toast.makeText(this, "Shizuku权限已授予", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateButtonState() {
        btnBack.setEnabled(viewPager.getCurrentItem() != 0);
        if (viewPager.getCurrentItem() == pages.size() - 1) btnNext.setText("完成设置");
        else btnNext.setText("下一步");
    }
    
    @Override
    public void bindPage(View view, int position) {
        if (position >= 0 && position < pages.size()) {
            pages.get(position).getBindView().bind(view);
        }
    }

    private void disableViewPagerSwipe(ViewPager2 viewPager) {
        viewPager.setUserInputEnabled(false);
    }

    private void startActivityToMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}