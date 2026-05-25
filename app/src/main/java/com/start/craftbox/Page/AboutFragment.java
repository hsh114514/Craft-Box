package com.start.craftbox.Page;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;
import com.start.craftbox.Network.HttpUtils;
import com.start.craftbox.R;

public class AboutFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View root, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);
        MaterialButton btn_github = root.findViewById(R.id.btnGitHub);
        btn_github.setOnClickListener(view -> HttpUtils.jumpWebSite(requireActivity(), "https://github.com/hsh114514/Craft-Box"));
        MaterialButton btn_Feedback = root.findViewById(R.id.btnFeedback);
        btn_Feedback.setOnClickListener(view ->  HttpUtils.jumpWebSite(requireActivity(), "https://github.com/hsh114514/Craft-Box/issues"));


    }
}