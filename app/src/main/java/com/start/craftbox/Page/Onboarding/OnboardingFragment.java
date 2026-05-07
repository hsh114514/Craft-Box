package com.start.craftbox.Page.Onboarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class OnboardingFragment extends Fragment {

    private static final String ARG_LAYOUT_RES = "layout_res";
    private static final String ARG_POSITION = "position";

    public static OnboardingFragment newInstance(int layoutRes, int position) {
        OnboardingFragment fragment = new OnboardingFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT_RES, layoutRes);
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getArguments() == null) {
            return null;
        }
        int layoutRes = getArguments().getInt(ARG_LAYOUT_RES);
        return inflater.inflate(layoutRes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() == null) return;
        int position = getArguments().getInt(ARG_POSITION, -1);

        if (getActivity() instanceof OnboardingPageBinder) {
            ((OnboardingPageBinder) getActivity()).bindPage(view, position);
        }
    }
}
