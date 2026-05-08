package com.start.craftbox.Page.Onboarding;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;

import com.start.craftbox.R;

public class OnboardingPageFactory {
    public static OnboardingPage createDefaultPage(String title, String description, @DrawableRes int imageRes) {
        return new OnboardingPage(
                R.layout.fragment_onboarding_default,
                view -> {
                    TextView tvTitle = view.findViewById(R.id.tvTitle);
                    TextView tvDescription = view.findViewById(R.id.tvDescription);
                    ImageView ivIllustration = view.findViewById(R.id.ivIllustration);
                    if (tvTitle != null) tvTitle.setText(title);
                    if (tvDescription != null) tvDescription.setText(description);
                    if (ivIllustration != null) ivIllustration.setImageResource(imageRes);
                }
        );
    }

    public static OnboardingPage createCustomPage(int layoutRes, OnboardingPage.PageBinder bindView) {
        return new OnboardingPage(layoutRes, bindView);
    }
    public static OnboardingPage createCustomPageWithCompletionCheck(
            int layoutRes, 
            OnboardingPage.PageBinder bindView,
            OnboardingPage.CompletionChecker completionChecker) {
        return new OnboardingPage(layoutRes, bindView, completionChecker);
    }
}
