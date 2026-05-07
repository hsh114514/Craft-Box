package com.start.craftbox.Page.Onboarding;

import android.view.View;
import androidx.annotation.LayoutRes;

public class OnboardingPage {
    private final int layoutRes;
    private final PageBinder bindView;

    public interface PageBinder {
        void bind(View view);
    }

    public OnboardingPage(@LayoutRes int layoutRes, PageBinder bindView) {
        this.layoutRes = layoutRes;
        this.bindView = bindView != null ? bindView : view -> {};
    }

    public int getLayoutRes() {
        return layoutRes;
    }

    public PageBinder getBindView() {
        return bindView;
    }
}
