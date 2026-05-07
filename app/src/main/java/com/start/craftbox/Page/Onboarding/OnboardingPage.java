package com.start.craftbox.Page.Onboarding;

import android.view.View;
import androidx.annotation.LayoutRes;

public class OnboardingPage {
    private final int layoutRes;
    private final PageBinder bindView;

    private final CompletionChecker completionChecker;
    public interface PageBinder {
        void bind(View view);
    }

    public interface CompletionChecker {
        boolean isCompleted();
    }

    public OnboardingPage(@LayoutRes int layoutRes, PageBinder bindView) {
        this.layoutRes = layoutRes;
        this.bindView = bindView != null ? bindView : view -> {};
        this.completionChecker = () -> true;
    }

    public OnboardingPage(@LayoutRes int layoutRes, PageBinder bindView, CompletionChecker completionChecker) {
        this.layoutRes = layoutRes;
        this.bindView = bindView != null ? bindView : view -> {};
        this.completionChecker = completionChecker != null ? completionChecker : () -> true;
    }

    public int getLayoutRes() {
        return layoutRes;
    }

    public PageBinder getBindView() {
        return bindView;
    }

    public CompletionChecker getCompletionChecker() {
        return completionChecker;
    }
}
